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
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/**
 * Demon epeiste : creature de melee lourde, haute de quatre blocs et demi.
 *
 * <p>Son fichier d'animation contient quatorze animations, dont une seule etait utilisee. Un
 * second controleur joue desormais l'attaque, et la cape s'anime en continu.
 */
public class DemonSwordsmanEntity extends AnimatedMonster {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
	private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("sword_hit");
	private static final RawAnimation CAPE = RawAnimation.begin().thenLoop("cape_idle");

	private static final String ATTACK_CONTROLLER = "attaque";
	private static final String CAPE_CONTROLLER = "cape";

	public DemonSwordsmanEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 40;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 120.0)
				.add(Attributes.ARMOR, 10.0)
				.add(Attributes.ATTACK_DAMAGE, 14.0)
				.add(Attributes.MOVEMENT_SPEED, 0.28)
				.add(Attributes.FOLLOW_RANGE, 32.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		super.registerControllers(controllers);
		controllers.add(new AnimationController<>(this, ATTACK_CONTROLLER, 0, this::attackPredicate));
		controllers.add(new AnimationController<>(this, CAPE_CONTROLLER, 5,
				state -> state.setAndContinue(CAPE)));
	}

	private PlayState attackPredicate(AnimationState<DemonSwordsmanEntity> state) {
		if (this.swinging && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
			state.getController().forceAnimationReset();
			state.getController().setAnimation(ATTACK);
			this.swinging = false;
		}
		return PlayState.CONTINUE;
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
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN)) {
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
