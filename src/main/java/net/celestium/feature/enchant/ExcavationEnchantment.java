package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Excavation : la pioche et la pelle creusent par carres.
 *
 * <p>Le cote du carre vaut {@code 2 x niveau + 1} : trois, cinq, sept, puis neuf. Les cotes sont
 * impairs parce que le carre se centre sur le bloc vise ; un carre de dix ne se centre sur rien et
 * obligerait a choisir arbitrairement de quel cote deborder. Neuf est donc le dernier palier sous
 * la dizaine demandee — un cinquieme niveau donnerait onze.
 */
public class ExcavationEnchantment extends Enchantment {

	/** Quatre niveaux : trois, cinq, sept et neuf blocs de cote. */
	private static final int MAX_LEVEL = 4;

	public ExcavationEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	/** Cote du carre creuse a ce niveau. */
	public static int sideFor(int level) {
		return 2 * Math.max(1, Math.min(MAX_LEVEL, level)) + 1;
	}

	/** Le rayon correspondant, en blocs de part et d'autre du bloc vise. */
	public static int radiusFor(int level) {
		return sideFor(level) / 2;
	}

	/** La pioche et la pelle. La hache a son propre enchantement, et la houe n'a rien a creuser. */
	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem;
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
