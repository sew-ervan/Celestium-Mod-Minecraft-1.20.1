package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Aimant : ce qui tombe va droit dans les poches.
 *
 * <p>Le confort qu'il apporte se mesure au moment ou l'on creuse un carre de neuf : quatre-vingts
 * objets au sol, dont la moitie roule dans un trou. Il rend les autres enchantements du mod
 * utilisables plutot qu'il n'ajoute une capacite propre.
 *
 * <p>Il vaut pour tous les outils de creusement, ce qui est voulu : c'est le seul du lot a ne pas
 * se limiter a un type d'outil, parce qu'il ne fait rien qui depende de ce qu'on casse.
 */
public class MagnetismEnchantment extends Enchantment {

	public MagnetismEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof DiggerItem;
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
