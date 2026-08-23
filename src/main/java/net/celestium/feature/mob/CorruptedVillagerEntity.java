package net.celestium.feature.mob;

import net.celestium.feature.corruption.DimensionCorruption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Villageois corrompu : ce qu'il reste des habitants des Terres du demon.
 *
 * <p>Il peuple les villages de la dimension et juge les visiteurs a ce qu'ils portent. Qui arrive
 * en Celestium corrompu ou en Demonium passe pour un des leurs : on le laisse approcher, et on
 * commerce avec lui. Qui arrive sans rien est un intrus, et se fait charger a vue.
 *
 * <p>Cette condition n'est pas un detour : elle resout ce qui serait sinon contradictoire. Une
 * creature qui attaque des qu'elle vous voit ne peut pas tenir boutique, et une creature qui tient
 * boutique n'a plus rien de menacant. L'armure tranche — et elle donne au meme temps une raison de
 * plus de la porter.
 *
 * <p>Aucun nouvel asset : le rendu reprend le modele de villageois vanilla et la texture du
 * villageois zombifie, referencee et non copiee.
 */
public class CorruptedVillagerEntity extends Monster implements Merchant {

	/** Nombre de pieces d'armure protectrice a partir duquel on est reconnu des leurs. */
	private static final int RECOGNITION_THRESHOLD = 1;

	@Nullable
	private Player tradingPlayer;

	@Nullable
	private MerchantOffers offers;

	public CorruptedVillagerEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 8;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 22.0)
				.add(Attributes.ARMOR, 2.0)
				.add(Attributes.ATTACK_DAMAGE, 5.0)
				.add(Attributes.MOVEMENT_SPEED, 0.25)
				.add(Attributes.FOLLOW_RANGE, 24.0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.7));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		// On riposte a qui frappe, quelle que soit sa tenue : porter leur armure achete un passage,
		// pas l'impunite.
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,
				10, true, false, living -> living instanceof Player player && !recognises(player)));
	}

	/** Vrai si ce joueur porte assez de leur matiere pour passer pour un des leurs. */
	public static boolean recognises(Player player) {
		return DimensionCorruption.countProtectivePieces(player) >= RECOGNITION_THRESHOLD;
	}

	/**
	 * Le clic droit ouvre le troc, a condition d'etre reconnu.
	 *
	 * <p>Un intrus ne recoit rien : le clic passe au travers, et le villageois continue de charger.
	 */
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND || !this.isAlive() || !recognises(player)) {
			return super.mobInteract(player, hand);
		}

		if (this.getTradingPlayer() != null) {
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		}

		if (!this.level().isClientSide()) {
			this.setTradingPlayer(player);
			this.openTradingScreen(player, this.getDisplayName(), 1);
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide());
	}

	// --- Le troc ---

	@Override
	public void setTradingPlayer(@Nullable Player player) {
		this.tradingPlayer = player;
	}

	@Nullable
	@Override
	public Player getTradingPlayer() {
		return this.tradingPlayer;
	}

	@Override
	public MerchantOffers getOffers() {
		if (this.offers == null) {
			this.offers = CorruptedVillagerTrades.create(this.random);
		}
		return this.offers;
	}

	@Override
	public void overrideOffers(MerchantOffers replacement) {
		// Appele cote client a l'ouverture de l'ecran : le serveur reste seul maitre du contenu.
		this.offers = replacement;
	}

	@Override
	public void notifyTrade(MerchantOffer offer) {
		offer.increaseUses();
		this.ambientSoundTime = -this.getAmbientSoundInterval();
		this.playSound(this.getNotifyTradeSound(), this.getSoundVolume(), this.getVoicePitch());
	}

	@Override
	public void notifyTradeUpdated(ItemStack stack) {
		if (!this.level().isClientSide() && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
			this.ambientSoundTime = -this.getAmbientSoundInterval();
			this.playSound(this.getNotifyTradeSound(), this.getSoundVolume(), this.getVoicePitch());
		}
	}

	/** Ces villageois-la ne montent pas en grade : ils survivent, ils ne prosperent pas. */
	@Override
	public int getVillagerXp() {
		return 0;
	}

	@Override
	public void overrideXp(int xp) {
	}

	@Override
	public boolean showProgressBar() {
		return false;
	}

	@Override
	public SoundEvent getNotifyTradeSound() {
		return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
	}

	@Override
	public boolean isClientSide() {
		return this.level().isClientSide();
	}

	/** Le stock se conserve : quitter la dimension et revenir ne le renouvelle pas. */
	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Offers", this.getOffers().createTag());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Offers", CompoundTag.TAG_COMPOUND)) {
			this.offers = new MerchantOffers(tag.getCompound("Offers"));
		}
	}

	/** Un marchand mort ou disparu ne doit pas laisser un ecran ouvert derriere lui. */
	@Override
	public void die(DamageSource source) {
		super.die(source);
		this.setTradingPlayer(null);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ZOMBIE_VILLAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ZOMBIE_VILLAGER_DEATH;
	}

	@Override
	protected void playStepSound(net.minecraft.core.BlockPos pos,
			net.minecraft.world.level.block.state.BlockState state) {
		this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}
}
