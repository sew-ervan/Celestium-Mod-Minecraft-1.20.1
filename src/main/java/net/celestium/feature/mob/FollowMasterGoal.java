package net.celestium.feature.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Le poulain suit celui qui l'a fait naitre.
 *
 * <p>Le jeu de base sait faire suivre un maitre — mais seulement aux creatures apprivoisables, une
 * famille a laquelle les chevaux n'appartiennent pas : ils se domptent, ils ne s'attachent pas. Ce
 * but rend ce comportement a un cheval, et a un seul cas : le poulain.
 *
 * <p>La regle est simple et se retient : le poulain suit, l'adulte se monte. Un poulain trop jeune
 * pour porter quiconque n'aurait aucune raison d'exister s'il fallait le laisser derriere soi ;
 * une licorne adulte qui trottinerait derriere son cavalier n'en aurait pas davantage.
 *
 * <p>Au-dela d'une certaine distance, il se replace au lieu de courir. Un compagnon qui reste
 * bloque derriere une colline est un compagnon perdu, et le jeu de base fait exactement pareil avec
 * les chiens.
 */
public class FollowMasterGoal extends Goal {

	/** Distance au carre au-dela de laquelle le poulain se replace au lieu de courir. */
	private static final double TELEPORT_BEYOND = 400.0;

	/** Intervalle entre deux recalculs de chemin, en ticks. */
	private static final int REPATH_INTERVAL = 10;

	private final AbstractHorse foal;
	private final double speed;
	private final float startDistance;
	private final float stopDistance;

	@Nullable
	private LivingEntity master;
	private int countdown;

	public FollowMasterGoal(AbstractHorse foal, double speed, float startDistance, float stopDistance) {
		this.foal = foal;
		this.speed = speed;
		this.startDistance = startDistance;
		this.stopDistance = stopDistance;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (!this.follows()) {
			return false;
		}

		LivingEntity owner = this.owner();
		if (owner == null || owner.isSpectator()) {
			return false;
		}
		if (this.foal.distanceToSqr(owner) < this.startDistance * this.startDistance) {
			return false;
		}

		this.master = owner;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.master != null
				&& this.follows()
				&& !this.foal.getNavigation().isDone()
				&& this.foal.distanceToSqr(this.master) > this.stopDistance * this.stopDistance;
	}

	@Override
	public void start() {
		this.countdown = 0;
	}

	@Override
	public void stop() {
		this.master = null;
		this.foal.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (this.master == null) {
			return;
		}

		this.foal.getLookControl().setLookAt(this.master, 10.0F, this.foal.getMaxHeadXRot());

		if (--this.countdown > 0) {
			return;
		}
		this.countdown = this.adjustedTickDelay(REPATH_INTERVAL);

		if (this.foal.distanceToSqr(this.master) > TELEPORT_BEYOND) {
			this.foal.moveTo(this.master.getX(), this.master.getY(), this.master.getZ(),
					this.foal.getYRot(), this.foal.getXRot());
			this.foal.getNavigation().stop();
			return;
		}

		this.foal.getNavigation().moveTo(this.master, this.speed);
	}

	/** Le poulain suit ; l'adulte non, et le sauvage encore moins. */
	private boolean follows() {
		return this.foal.isTamed() && this.foal.isBaby() && !this.foal.isVehicle();
	}

	@Nullable
	private LivingEntity owner() {
		UUID id = this.foal.getOwnerUUID();
		return id == null ? null : this.foal.level().getPlayerByUUID(id);
	}
}
