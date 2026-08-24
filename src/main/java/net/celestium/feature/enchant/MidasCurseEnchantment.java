package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Malediction de Midas : tout ce qui vaut mieux que le fer se change en or.
 *
 * <p>C'est bien une malediction et non un enchantement deguise : le diamant, l'emeraude et le
 * Celestium en ressortent en lingots d'or, ce qui est presque toujours une perte. Elle se declare
 * comme telle, donc s'affiche en rouge et resiste a la meule.
 *
 * <p>Elle reste proposee par la table. Personne n'est oblige de la prendre, et une malediction
 * qu'on ne pourrait jamais obtenir n'existerait pas vraiment.
 */
public class MidasCurseEnchantment extends Enchantment {

	public MidasCurseEnchantment() {
		super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof PickaxeItem;
	}

	/**
	 * Incompatible avec la Fonte et le Toucher de soie.
	 *
	 * <p>Les trois se disputent le butin du meme bloc. Les laisser cohabiter reviendrait a faire
	 * dependre le resultat de l'ordre dans lequel le jeu applique les modificateurs, ce qu'aucun
	 * joueur ne peut deviner.
	 */
	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other)
				&& other != Enchantments.SILK_TOUCH
				&& !(other instanceof SmeltingEnchantment);
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}
}
