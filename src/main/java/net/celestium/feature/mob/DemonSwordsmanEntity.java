package net.celestium.feature.mob;

import net.celestium.core.entity.AnimatedMonster;
import net.celestium.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

/**
 * Demon epeiste : le boss du mod.
 *
 * <p>Son fichier d'animation contient quatorze animations, dont une seule etait exploitee par le
 * mod d'origine. Le combat en utilise desormais huit : repos, marche, course, coup d'epee, frappe
 * sismique, invocation de sbires, passage en seconde phase et cape au vent.
 *
 * <p>Le combat se joue en deux temps. Sous la moitie de ses points de vie, le demon change de
 * phase : il accelere, frappe plus fort, cesse de craindre les fleches et se met a invoquer des
 * gardiens miniatures.
 */
public class DemonSwordsmanEntity extends AnimatedMonster {

	private static final EntityDataAccessor<Boolean> SECOND_PHASE =
			SynchedEntityData.defineId(DemonSwordsmanEntity.class, EntityDataSerializers.BOOLEAN);

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
	private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
	private static final RawAnimation CAPE = RawAnimation.begin().thenLoop("cape_idle");

	private static final RawAnimation SWORD_HIT = RawAnimation.begin().thenPlay("sword_hit");
	private static final RawAnimation SEISMIC = RawAnimation.begin().thenPlay("seismic_hit");
	private static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("minions_spawn");
	private static final RawAnimation SECOND_STAGE = RawAnimation.begin().thenPlay("second_stage");

	private static final String ABILITY_CONTROLLER = "capacites";
	private static final String CAPE_CONTROLLER = "cape";

	/** Seuil de bascule en seconde phase, en fraction des points de vie. */
	private static final float SECOND_PHASE_THRESHOLD = 0.5F;

	private static final int SEISMIC_COOLDOWN = 200;
	private static final double SEISMIC_RANGE = 6.0;
	private static final float SEISMIC_DAMAGE = 8.0F;

	private static final int SUMMON_COOLDOWN = 400;
	private static final int MINIONS_PER_SUMMON = 2;

	private final ServerBossEvent bossEvent = new ServerBossEvent(
			this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

	private int seismicCooldown = SEISMIC_COOLDOWN;
	private int summonCooldown = SUMMON_COOLDOWN;

	public DemonSwordsmanEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 250;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 220.0)
				.add(Attributes.ARMOR, 12.0)
				.add(Attributes.ATTACK_DAMAGE, 14.0)
				.add(Attributes.MOVEMENT_SPEED, 0.28)
				.add(Attributes.FOLLOW_RANGE, 40.0)
				// Un boss que l'on repousse a coups d'epee n'en est pas un.
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SECOND_PHASE, false);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public boolean isSecondPhase() {
		return this.entityData.get(SECOND_PHASE);
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();

		this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

		if (!this.isSecondPhase() && this.getHealth() < this.getMaxHealth() * SECOND_PHASE_THRESHOLD) {
			enterSecondPhase();
		}

		LivingEntity target = this.getTarget();
		if (target == null) {
			return;
		}

		if (--this.seismicCooldown <= 0 && this.distanceToSqr(target) < SEISMIC_RANGE * SEISMIC_RANGE) {
			seismicSlam();
			this.seismicCooldown = SEISMIC_COOLDOWN;
		}

		if (this.isSecondPhase() && --this.summonCooldown <= 0) {
			summonMinions();
			this.summonCooldown = SUMMON_COOLDOWN;
		}
	}

	/** Bascule en seconde phase : plus rapide, plus dur, et accompagne. */
	private void enterSecondPhase() {
		this.entityData.set(SECOND_PHASE, true);
		this.triggerAnim(ABILITY_CONTROLLER, "second_stage");

		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.36);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(19.0);

		this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
		this.level().playSound(null, this.blockPosition(),
				SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.6F);

		// Le changement de phase repousse ce qui se trouve au contact : impossible de rester
		// colle au boss pendant la transformation.
		pushBack(4.0, 0.0F);
	}

	/** Frappe le sol : degats et projection dans un rayon autour du demon. */
	private void seismicSlam() {
		this.triggerAnim(ABILITY_CONTROLLER, "seismic");
		this.level().playSound(null, this.blockPosition(),
				SoundEvents.RAVAGER_ATTACK, SoundSource.HOSTILE, 1.5F, 0.7F);
		pushBack(SEISMIC_RANGE, SEISMIC_DAMAGE);
	}

	/** Applique degats et projection a ce qui entoure le demon, lui et ses sbires exceptes. */
	private void pushBack(double radius, float damage) {
		AABB area = this.getBoundingBox().inflate(radius);
		List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, area,
				entity -> entity != this && entity.isAlive() && !(entity instanceof MiniWardenEntity));

		for (LivingEntity victim : victims) {
			if (damage > 0.0F) {
				victim.hurt(this.damageSources().mobAttack(this), damage);
			}
			Vec3 away = victim.position().subtract(this.position()).normalize();
			victim.push(away.x * 0.8, 0.45, away.z * 0.8);
		}
	}

	/** Invoque des gardiens miniatures en renfort. */
	private void summonMinions() {
		if (!(this.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		this.triggerAnim(ABILITY_CONTROLLER, "summon");

		for (int i = 0; i < MINIONS_PER_SUMMON; i++) {
			MiniWardenEntity minion = ModEntities.MINI_WARDEN.get().create(serverLevel);
			if (minion == null) {
				continue;
			}
			double angle = (Math.PI * 2 / MINIONS_PER_SUMMON) * i;
			minion.moveTo(this.getX() + Math.cos(angle) * 3.0, this.getY(),
					this.getZ() + Math.sin(angle) * 3.0, this.getYRot(), 0.0F);
			minion.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(minion.blockPosition()),
					MobSpawnType.MOB_SUMMONED, null, null);
			minion.setTarget(this.getTarget());
			serverLevel.addFreshEntity(minion);
		}
	}

	/**
	 * En seconde phase, le demon ignore les fleches : le combat doit se gagner au corps a corps,
	 * pas a l'arc depuis une corniche.
	 */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN)) {
			return false;
		}
		if (this.isSecondPhase() && source.getDirectEntity() instanceof AbstractArrow) {
			return false;
		}
		return super.hurt(source, amount);
	}

	/** Un boss que l'on empoisonne a distance perd tout interet. */
	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		if (effect.getEffect() == MobEffects.POISON || effect.getEffect() == MobEffects.WITHER) {
			return false;
		}
		return super.canBeAffected(effect);
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossEvent.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossEvent.removePlayer(player);
	}

	@Override
	public void setCustomName(Component name) {
		super.setCustomName(name);
		this.bossEvent.setName(this.getDisplayName());
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.RAVAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.RAVAGER_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.RAVAGER_AMBIENT;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		super.registerControllers(controllers);

		controllers.add(new AnimationController<>(this, ABILITY_CONTROLLER, 0, state -> PlayState.STOP)
				.triggerableAnim("sword_hit", SWORD_HIT)
				.triggerableAnim("seismic", SEISMIC)
				.triggerableAnim("summon", SUMMON)
				.triggerableAnim("second_stage", SECOND_STAGE));

		controllers.add(new AnimationController<>(this, CAPE_CONTROLLER, 5,
				state -> state.setAndContinue(CAPE)));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean hurt = super.doHurtTarget(target);
		if (hurt) {
			this.triggerAnim(ABILITY_CONTROLLER, "sword_hit");
		}
		return hurt;
	}

	@Override
	protected RawAnimation idleAnimation() {
		return IDLE;
	}

	/** En seconde phase le demon charge : l'animation de course remplace la marche. */
	@Override
	protected RawAnimation walkAnimation() {
		return this.isSecondPhase() ? RUN : WALK;
	}
}
