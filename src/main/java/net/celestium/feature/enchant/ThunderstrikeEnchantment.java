package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Eclair fulgurant : frapper appelle parfois la foudre sur ce qu'on frappe.
 *
 * <p>Trois niveaux, et deux baremes. Contre une creature, une chance sur dix, cinq ou trois selon
 * le niveau ; contre un joueur, bien moins. L'ecart est voulu : un enchantement qui foudroierait un
 * joueur une fois sur trois ne laisserait aucune place au duel.
 */
public class ThunderstrikeEnchantment extends Enchantment {

	private static final int MAX_LEVEL = 3;

	/** Chances sur cent de foudroyer une creature, par niveau. */
	private static final int[] AGAINST_MOB = {10, 20, 30};

	/** Chances sur cent de foudroyer un joueur, par niveau. */
	private static final int[] AGAINST_PLAYER = {3, 7, 13};

	public ThunderstrikeEnchantment() {
		super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	/** Chances sur cent, selon le niveau et la nature de la cible. */
	public static int chanceFor(int level, boolean againstPlayer) {
		int index = Math.max(1, Math.min(MAX_LEVEL, level)) - 1;
		return againstPlayer ? AGAINST_PLAYER[index] : AGAINST_MOB[index];
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof SwordItem;
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
