package net.celestium.feature.familiar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Ce que tous les familiers du mod ont en commun.
 *
 * <p>Un familier n'est ni un animal d'elevage ni une monture : il ne se reproduit pas, ne se monte
 * pas, ne rend rien a l'abattage. Il suit, il s'assied quand on le lui dit, et il mord ce qui mord
 * son maitre. C'est tout, et c'est ce qui le distingue du reste du bestiaire.
 *
 * <p>Il se dompte comme un loup — en lui tendant de quoi manger, plusieurs fois s'il le faut. Chaque
 * espece decide de ce qu'elle accepte, et ce choix la situe : le fennec veut du lapin, ceux des
 * dimensions veulent la matiere de chez eux. Un familier qu'on pourrait apprivoiser avec du ble
 * n'aurait aucun lien avec l'endroit ou on le trouve.
 *
 * <p>Une fois dompte, il ne disparait plus : un compagnon qui s'evapore parce qu'on s'est trop
 * eloigne n'est pas un compagnon.
 */
public abstract class FamiliarEntity extends TamableAnimal {

	/** Chances sur cette valeur de reussir l'apprivoisement a chaque bouchee. */
	private static final int TAMING_ODDS = 3;

	/** Ce qu'une bouchee rend de sante a un familier deja dompte. */
	private static final float HEALING = 4.0F;

	protected FamiliarEntity(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
		this.setTame(false);
	}

	/**
	 * Caracteristiques communes.
	 *
	 * <p>Assez de vie pour ne pas mourir a la premiere maladresse, assez de degats pour compter dans
	 * une bagarre sans la gagner seul. Un familier qui tuerait a la place de son maitre le priverait
	 * de la sienne.
	 */
	public static AttributeSupplier.Builder createAttributes() {
		return Animal.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 16.0)
				.add(Attributes.MOVEMENT_SPEED, 0.32)
				.add(Attributes.ATTACK_DAMAGE, 3.0)
				.add(Attributes.FOLLOW_RANGE, 24.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
		this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.15, 8.0F, 2.0F, false));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

		// Il ne choisit jamais sa cible : il reprend celle de son maitre, ou celui qui l'a frappe.
		// Un familier qui partirait en chasse de lui-meme reveillerait tout ce qu'on cherchait a
		// eviter.
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
	}

	/** Ce que cette espece accepte de manger, et donc ce qui permet de l'apprivoiser. */
	@Override
	public abstract boolean isFood(ItemStack stack);

	/**
	 * La main tendue.
	 *
	 * <p>Sur un familier libre, c'est une tentative d'apprivoisement ; sur le sien, c'est un ordre
	 * de s'asseoir, ou un repas s'il est blesse. Sur celui d'un autre, rien : un compagnon ne change
	 * pas de maitre pour une poignee de graines.
	 */
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack offered = player.getItemInHand(hand);

		if (this.level().isClientSide()) {
			boolean answers = this.obeys(player) || (!this.isTame() && this.isFood(offered));
			return answers ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}

		if (this.isTame()) {
			return this.obeys(player)
					? this.obey(player, offered)
					: InteractionResult.PASS;
		}

		if (!this.isFood(offered)) {
			return InteractionResult.PASS;
		}

		return this.court(player, offered);
	}

	/** Ce qu'un familier deja dompte fait de la main de son maitre. */
	private InteractionResult obey(Player owner, ItemStack offered) {
		if (this.isFood(offered) && this.getHealth() < this.getMaxHealth()) {
			this.eat(owner, offered);
			this.heal(HEALING);
			return InteractionResult.SUCCESS;
		}

		this.setOrderedToSit(!this.isOrderedToSit());
		this.jumping = false;
		this.navigation.stop();
		this.setTarget(null);

		return InteractionResult.SUCCESS;
	}

	/** La tentative d'apprivoisement, qui echoue deux fois sur trois. */
	private InteractionResult court(Player player, ItemStack offered) {
		this.eat(player, offered);

		if (this.random.nextInt(TAMING_ODDS) != 0) {
			// Le sept et le six sont les deux signaux que le jeu de base emploie pour le coeur et
			// la fumee : les reutiliser evite d'inventer un langage que le client ne parlerait pas.
			this.level().broadcastEntityEvent(this, (byte) 6);
			return InteractionResult.SUCCESS;
		}

		this.tame(player);
		this.setOrderedToSit(false);
		this.level().broadcastEntityEvent(this, (byte) 7);

		return InteractionResult.SUCCESS;
	}

	private void eat(Player player, ItemStack offered) {
		if (!player.getAbilities().instabuild) {
			offered.shrink(1);
		}
	}

	/**
	 * Vrai si ce joueur est le maitre de ce familier.
	 *
	 * <p>La comparaison porte sur les identifiants et non sur les entites. Le jeu de base compare
	 * les entites, ce qui l'oblige a retrouver le maitre dans le monde charge : la reponse depend
	 * alors de ce qui est charge, pour une question qui n'en depend pas.
	 */
	public boolean obeys(Player player) {
		return player.getUUID().equals(this.getOwnerUUID());
	}

	/** Un familier ne se reproduit pas : il s'apprivoise, un par un. */
	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return null;
	}

	@Override
	public boolean canMate(Animal partner) {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return !this.isTame();
	}

	@Override
	public boolean requiresCustomPersistence() {
		return super.requiresCustomPersistence() || this.isTame();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("Sitting", this.isOrderedToSit());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setOrderedToSit(tag.getBoolean("Sitting"));
		this.setInSittingPose(this.isOrderedToSit());
	}
}
