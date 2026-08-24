package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Dompteur : ce qui vous prend pour cible se ravise parfois.
 *
 * <p>La chance depend du niveau, mais aussi de ce qu'on a en face. Un zombie se detourne aisement ;
 * un demon epeiste, presque jamais. C'est ce qui empeche l'enchantement de rendre les affrontements
 * de fin de partie sans enjeu — il allege la pression du nombre, pas celle d'un adversaire serieux.
 */
public class TamerEnchantment extends Enchantment {

	private static final int MAX_LEVEL = 3;

	/** Chances sur cent de detourner une creature fragile, par niveau. */
	private static final int[] BASE_CHANCE = {25, 45, 65};

	/**
	 * Points de vie au-dela desquels une creature ne se laisse plus detourner du tout.
	 *
	 * <p>Quarante correspond au double d'un zombie renforce : tout ce qui tient davantage releve du
	 * combat serieux, ou l'enchantement n'a pas a peser.
	 */
	private static final float RESISTANT_HEALTH = 40.0F;

	public TamerEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	/**
	 * Chances sur cent de detourner cette creature.
	 *
	 * <p>La chance de base decroit lineairement avec les points de vie maximaux de la cible, et
	 * tombe a zero au seuil de resistance.
	 */
	public static int chanceFor(int level, float maxHealth) {
		int base = BASE_CHANCE[Math.max(1, Math.min(MAX_LEVEL, level)) - 1];
		float sturdiness = Math.max(0.0F, 1.0F - maxHealth / RESISTANT_HEALTH);

		return Math.round(base * sturdiness);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem armor && armor.getType() == ArmorItem.Type.HELMET;
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
