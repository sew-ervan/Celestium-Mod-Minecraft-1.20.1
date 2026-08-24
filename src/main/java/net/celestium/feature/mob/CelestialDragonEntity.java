package net.celestium.feature.mob;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Le dragon celeste : le gardien d'un tresor, et rien d'autre.
 *
 * <p>Ce n'est pas un boss de fin de progression comme le demon epeiste — il ne verrouille aucune
 * dimension et ne garde aucune porte. Il veille sur un tas d'or, de Celestium et de matiere noire,
 * ce qui suffit a justifier qu'on aille le chercher : on le combat parce qu'on veut ce qui est
 * dessous.
 *
 * <p>Il herite du phantasme du jeu de base, et c'est un choix. Sa maniere de combattre — prendre de
 * la hauteur, fondre en pique, remonter hors de portee — est exactement celle qu'on attend d'un
 * dragon, et elle est deja ecrite. La reprendre libere le travail pour ce qui le distingue
 * vraiment : sa taille, sa resistance, et le fait qu'il ne craigne pas le jour.
 *
 * <p>Sa barre de vie n'apparait qu'a qui l'a approche. Visible de loin, elle annoncerait le combat
 * avant qu'on ait vu le tresor, et le tresor doit se decouvrir en premier.
 */
public class CelestialDragonEntity extends Phantom {

	/** Portee a laquelle la barre de vie s'affiche. */
	private static final double BOSS_BAR_RANGE = 48.0;

	/**
	 * Envergure. Le phantasme du jeu de base va de zero a sept ; celle-ci en fait un adversaire
	 * qu'on voit venir de loin sans qu'il devienne impossible a toucher au sol.
	 */
	private static final int WINGSPAN = 5;

	private final ServerBossEvent bossEvent = new ServerBossEvent(
			this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

	public CelestialDragonEntity(EntityType<? extends Phantom> type, Level level) {
		super(type, level);
		this.setPhantomSize(WINGSPAN);
		this.xpReward = 60;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 150.0)
				.add(Attributes.ATTACK_DAMAGE, 12.0)
				.add(Attributes.ARMOR, 8.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FOLLOW_RANGE, 48.0);
	}

	/**
	 * Il s'en prend a qui l'approche.
	 *
	 * <p>Le phantasme du jeu de base ne s'interesse qu'aux dormeurs : il attend qu'on ait passe
	 * trois nuits debout. Un gardien de tresor n'a pas ce luxe — il defend ce qui est sous lui,
	 * eveille ou non, et c'est ce but supplementaire qui le lui permet.
	 *
	 * <p>Pas de riposte declaree : le phantasme n'est pas un {@code PathfinderMob}, et le but de
	 * riposte du jeu de base en exige un. Ce n'est pas une perte — le ciblage ci-dessous vise le
	 * joueur visible le plus proche, donc celui qui vient de frapper.
	 */
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	/**
	 * Le jour ne le brule pas.
	 *
	 * <p>C'est ce qui le separe le plus nettement du phantasme : celui-ci n'existe que la nuit et
	 * prend feu au lever du soleil. Un tresor qu'on ne pourrait piller qu'entre deux aurores serait
	 * une contrainte de calendrier, pas un combat.
	 */
	@Override
	public boolean isSunBurnTick() {
		return false;
	}

	/**
	 * La barre de vie suit ceux qui sont assez pres.
	 *
	 * <p>Recalculee a chaque tick plutot que tenue a jour a l'entree et a la sortie : un joueur qui
	 * se deconnecte ou meurt ne previent pas, et une barre restee accrochee a un absent ne
	 * disparaitrait jamais.
	 */
	@Override
	public void aiStep() {
		super.aiStep();

		if (this.level().isClientSide()) {
			return;
		}

		this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
		this.bossEvent.removeAllPlayers();

		for (Player nearby : this.level().players()) {
			if (nearby instanceof ServerPlayer server && this.distanceTo(nearby) <= BOSS_BAR_RANGE) {
				this.bossEvent.addPlayer(server);
			}
		}
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		this.bossEvent.removeAllPlayers();
	}

	/** Rien ne le pousse : il tient sa position au-dessus du tas. */
	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENDER_DRAGON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENDER_DRAGON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENDER_DRAGON_DEATH;
	}
}
