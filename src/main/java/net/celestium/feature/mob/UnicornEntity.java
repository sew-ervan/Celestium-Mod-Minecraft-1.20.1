package net.celestium.feature.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * La licorne.
 *
 * <p>Elle vit dans l'Overworld, en pleins champs, et sa seule defense est sa vitesse. Elle ne
 * frappe pas, ne se defend pas, ne poursuit personne : elle part. C'est ce qui en fait une chasse
 * plutot qu'un combat, et ce qui donne son prix a ce qu'elle laisse.
 *
 * <p>Elle herite du cheval du jeu de base, et cela lui vaut d'etre domptable a l'ancienne — en la
 * montant jusqu'a ce qu'elle cede. Ce n'est pas un effet de bord subi : une monture qui distance
 * tout ce qui court est exactement la recompense que merite une bete qu'on n'a pas reussi a
 * approcher autrement. Qui prefere sa corne devra la tuer, et les deux voies s'excluent.
 *
 * <p>Sa corne n'existe pas dans la geometrie du cheval : elle est ajoutee au modele, sur le crane,
 * par {@code UnicornModel}.
 */
public class UnicornEntity extends AbstractHorse {

	/**
	 * Vitesse de deplacement.
	 *
	 * <p>Les chevaux du jeu de base vont de 0,1125 a 0,3375 ; celle-ci depasse le plus rapide d'un
	 * bon tiers. C'est deliberement au-dessus de tout ce qui existe — une licorne « tres rapide »
	 * qui se laisserait rattraper au sprint ne serait qu'un cheval blanc.
	 */
	private static final double SPEED = 0.45;

	/** Distance a laquelle elle prend la fuite devant un joueur. */
	private static final float FLIGHT_DISTANCE = 14.0F;

	/** Chances sur cent de laisser une trace de lumiere a un tick donne. */
	private static final int SPARKLE_RATE = 25;

	public UnicornEntity(EntityType<? extends AbstractHorse> type, Level level) {
		super(type, level);
		this.xpReward = 10;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createBaseHorseAttributes()
				.add(Attributes.MAX_HEALTH, 30.0)
				.add(Attributes.MOVEMENT_SPEED, SPEED)
				.add(Attributes.JUMP_STRENGTH, 1.1);
	}

	/**
	 * Elle fuit qui l'approche, tant qu'elle est sauvage.
	 *
	 * <p>Le predicat compte autant que le but lui-meme : sans lui, une licorne domptee s'enfuirait
	 * de son propre cavalier des qu'il met pied a terre.
	 */
	@Override
	protected void addBehaviourGoals() {
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class,
				FLIGHT_DISTANCE, 1.6, 1.9, player -> !this.isTamed()));

		// Le poulain, lui, suit celui qui l'a fait naitre : voir {@link FollowMasterGoal}.
		this.goalSelector.addGoal(2, new FollowMasterGoal(this, 1.3, 6.0F, 2.0F));
	}

	/**
	 * Ses caracteristiques ne sont pas tirees au sort.
	 *
	 * <p>Le jeu de base fait varier la vitesse et la sante de chaque cheval, ce qui a du sens quand
	 * on en eleve un troupeau. Il n'y a qu'une sorte de licorne : deux exemplaires doivent se valoir,
	 * sans quoi on serait tente d'en tuer plusieurs pour comparer.
	 */
	@Override
	protected void randomizeAttributes(RandomSource random) {
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SPEED);
		this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(1.1);
	}

	/**
	 * Elle laisse une trainee de lumiere derriere elle.
	 *
	 * <p>Les particules ne sont posees que du cote du client : c'est du decor, et le serveur n'a
	 * aucune raison de l'envoyer a qui ne la regarde pas.
	 */
	@Override
	public void aiStep() {
		super.aiStep();

		if (!this.level().isClientSide() || this.random.nextInt(100) >= SPARKLE_RATE) {
			return;
		}

		this.level().addParticle(ParticleTypes.END_ROD,
				this.getRandomX(0.6), this.getY(0.5), this.getRandomZ(0.6),
				0.0, 0.02, 0.0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return SoundEvents.HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
		return SoundEvents.HORSE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.HORSE_DEATH;
	}
}
