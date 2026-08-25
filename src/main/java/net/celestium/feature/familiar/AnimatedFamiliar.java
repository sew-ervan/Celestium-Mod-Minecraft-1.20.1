package net.celestium.feature.familiar;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Un familier dont la geometrie vient de GeckoLib.
 *
 * <p>Le controleur d'animation est le meme que celui de {@code AnimatedMonster} — repos a l'arret,
 * marche en mouvement — mais il faut le reecrire ici : Java ne permet pas d'heriter a la fois d'une
 * creature hostile et d'une creature apprivoisable, et c'est la seule chose que les deux partagent.
 * Le fennec, lui, n'en a pas besoin : son rendu emprunte un modele du jeu de base, qui s'anime tout
 * seul.
 */
public abstract class AnimatedFamiliar extends FamiliarEntity implements GeoEntity {

	private static final String MOVEMENT_CONTROLLER = "mouvement";

	/** Vitesse au carre au-dela de laquelle la creature est consideree en deplacement. */
	private static final double MOVING_THRESHOLD = 1.0E-6;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected AnimatedFamiliar(EntityType<? extends TamableAnimal> type, Level level) {
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
		// Un familier assis ne marche pas, meme si le jeu le fait glisser d'un cran.
		boolean moving = !this.isOrderedToSit()
				&& (this.getDeltaMovement().horizontalDistanceSqr() > MOVING_THRESHOLD || state.isMoving());

		state.getController().setAnimation(moving ? this.walkAnimation() : this.idleAnimation());
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
