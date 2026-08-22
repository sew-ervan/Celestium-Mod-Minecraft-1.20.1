package net.celestium.core.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Base commune aux creatures animees du mod.
 *
 * <p>MCreator generait dans chaque entite le meme controleur d'animation, la meme fabrique
 * d'animations et le meme champ {@code animationprocedure} — plusieurs centaines de lignes
 * dupliquees. Une sous-classe n'a plus qu'a nommer ses animations.
 *
 * <p>Le portage vers GeckoLib 4 change tout le vocabulaire : {@code IAnimatable} devient
 * {@link GeoEntity}, {@code AnimationFactory} devient {@link AnimatableInstanceCache}, et
 * l'enregistrement des controleurs passe par un {@code ControllerRegistrar}.
 */
public abstract class AnimatedMonster extends Monster implements GeoEntity {

	private static final String MOVEMENT_CONTROLLER = "mouvement";

	/** Vitesse au carre au-dela de laquelle la creature est consideree en deplacement. */
	private static final double MOVING_THRESHOLD = 1.0E-6;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected AnimatedMonster(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	/** Animation jouee a l'arret. */
	protected abstract RawAnimation idleAnimation();

	/** Animation jouee en deplacement. */
	protected abstract RawAnimation walkAnimation();

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, MOVEMENT_CONTROLLER, 5, this::movementPredicate));
	}

	private <T extends GeoAnimatable> PlayState movementPredicate(AnimationState<T> state) {
		boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > MOVING_THRESHOLD || state.isMoving();
		state.getController().setAnimation(moving ? this.walkAnimation() : this.idleAnimation());
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
