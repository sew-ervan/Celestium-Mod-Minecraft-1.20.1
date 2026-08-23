package net.celestium.feature.mob;

import net.celestium.core.entity.AnimatedMonster;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * Parasite : la vermine des Terres du demon.
 *
 * <p>Faible pris isolement, dangereux en nombre. Il bondit sur sa cible et apparait par groupes,
 * ce qui en fait la menace de fond de la dimension la ou le demon epeiste en est l'evenement.
 *
 * <p>Il reprend le modele et les animations du gardien miniature, a une echelle bien moindre et
 * avec une texture corrompue : aucun nouvel asset n'est necessaire.
 */
public class ParasiteEntity extends AnimatedMonster {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

	/** Le parasite fait un tiers de la taille du gardien dont il emprunte le modele. */
	public static final float SCALE = 0.35F;

	public ParasiteEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 4;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 12.0)
				.add(Attributes.ARMOR, 1.0)
				.add(Attributes.ATTACK_DAMAGE, 4.0)
				// Vif : c'est sa vitesse et son nombre qui en font une menace, pas sa force.
				.add(Attributes.MOVEMENT_SPEED, 0.38)
				.add(Attributes.FOLLOW_RANGE, 20.0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	/** La boite de collision suit l'echelle du rendu, sinon la creature flotte ou s'enfonce. */
	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale(1.0F);
	}

	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		// Ne au milieu du feu et de la cendre : la lave et le feu ne lui font rien.
		if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)
				|| source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SILVERFISH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SILVERFISH_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SILVERFISH_AMBIENT;
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
