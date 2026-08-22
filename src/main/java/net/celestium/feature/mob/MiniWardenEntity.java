package net.celestium.feature.mob;

import net.celestium.core.entity.AnimatedMonster;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.RawAnimation;

/** Gardien miniature : version reduite et rapide du Warden. */
public class MiniWardenEntity extends AnimatedMonster {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

	public MiniWardenEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 15;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 40.0)
				.add(Attributes.ARMOR, 4.0)
				.add(Attributes.ATTACK_DAMAGE, 6.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FOLLOW_RANGE, 24.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.WARDEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.WARDEN_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.WARDEN_AMBIENT;
	}

	/**
	 * Le gardien ignore les potions jetees et la noyade.
	 *
	 * <p>En 1.20.1 les types de degats sont des donnees et non plus des constantes Java :
	 * {@code source == DamageSource.DROWN} devient {@code source.is(DamageTypes.DROWN)}.
	 */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getDirectEntity() instanceof ThrownPotion || source.is(DamageTypes.DROWN)) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	protected RawAnimation idleAnimation() {
		return IDLE;
	}

	@Override
	protected RawAnimation walkAnimation() {
		return WALK;
	}
}
