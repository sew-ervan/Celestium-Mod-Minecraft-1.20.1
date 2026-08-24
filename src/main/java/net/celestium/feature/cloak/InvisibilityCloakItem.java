package net.celestium.feature.cloak;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * La cape d'invisibilite.
 *
 * <p>Elle se porte a la place du plastron, et c'est tout son prix : on disparait, mais on renonce a
 * la piece d'armure qui protege le plus. Aucun autre cout n'a ete ajoute — ni durabilite qui
 * s'epuise, ni compte a rebours. Le choix se fait a l'equipement, une fois, et se lit sur la barre
 * d'armure.
 *
 * <p>Ce n'est deliberement pas un {@code ArmorItem}. La couche qui dessine l'armure sur le
 * personnage ne s'occupe que de ceux-la : n'en etant pas un, la cape ne se dessine pas, et un
 * porteur invisible reste vraiment invisible. Un plastron ordinaire, lui, continuerait de flotter
 * dans le vide. C'est {@code getEquipmentSlot}, cote Forge, qui la fait accepter dans l'emplacement
 * du torse malgre tout.
 *
 * <p>Elle ne rend pas tout a fait indetectable. Le jeu mesure la discretion d'une creature
 * invisible a la part d'armure qu'elle porte encore, et la cape compte pour une piece sur quatre :
 * les creatures reperent son porteur a environ un sixieme de la distance habituelle. Une cape reste
 * du tissu qui bouge — l'effacer completement la rendrait plus forte que la potion elle-meme.
 *
 * <p>L'invisibilite est posee par {@link CloakInvisibility}, sans passer par l'effet de potion :
 * c'est ce qui evite les volutes grises. Les particules de course, d'eau ou de chute, elles,
 * continuent de trahir qui court.
 */
public class InvisibilityCloakItem extends Item {

	public InvisibilityCloakItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant());
	}

	/**
	 * Elle appartient au torse.
	 *
	 * <p>Ce seul retour suffit a la rendre placable dans l'emplacement du plastron et a la faire
	 * ramasser au bon endroit par les creatures : Forge interroge cette methode partout ou le jeu de
	 * base demandait « de quel type d'armure s'agit-il ».
	 */
	@Nullable
	@Override
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.CHEST;
	}

	/**
	 * Un clic droit l'enfile, comme n'importe quelle piece d'armure.
	 *
	 * <p>Le jeu de base fait cet echange dans {@code ArmorItem}, dont la cape n'herite pas : il faut
	 * donc le refaire, a l'identique. Ce qui etait porte revient en main, la malediction du lien
	 * est respectee, et le mode creatif ne consomme rien.
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);

		if (EnchantmentHelper.hasBindingCurse(worn) || ItemStack.matches(held, worn)) {
			return InteractionResultHolder.fail(held);
		}

		if (!level.isClientSide()) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		ItemStack returned = worn.isEmpty() ? held : worn.copyAndClear();
		player.setItemSlot(EquipmentSlot.CHEST, player.isCreative() ? held.copy() : held.copyAndClear());

		return InteractionResultHolder.sidedSuccess(returned, level.isClientSide());
	}
}
