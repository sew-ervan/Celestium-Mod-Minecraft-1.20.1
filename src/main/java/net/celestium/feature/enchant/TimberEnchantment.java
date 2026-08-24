package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Abattage : casser un rondin emporte tout l'arbre.
 *
 * <p>Un seul niveau. Un abattage a moitie fait n'aurait aucun sens — soit l'arbre tombe, soit il ne
 * tombe pas — et les niveaux serviraient au mieux a plafonner le nombre de blocs, ce que la limite
 * de securite fait deja mieux.
 */
public class TimberEnchantment extends Enchantment {

	public TimberEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	/**
	 * La hache seule.
	 *
	 * <p>La categorie du jeu de base ne descend pas plus bas que « outil de creusement », qui reunit
	 * pioche, pelle, hache et houe. Ce filtre est donc le seul endroit ou dire que l'abattage ne
	 * concerne que la hache.
	 */
	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof AxeItem;
	}

	/** Introuvable ailleurs qu'a la table corrompue. */
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
