package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Moisson : la houe recolte un carre de cultures mures et les replante derriere elle.
 *
 * <p>Elle donne enfin un role a la houe, que le mod laissait jusqu'ici sans emploi. Le replantage
 * fait la moitie de l'interet : recolter un carre de sept sans replanter ne ferait que deplacer la
 * corvee du cassage vers la semaille.
 *
 * <p>Trois niveaux : trois, cinq et sept blocs de cote. Un champ fait rarement plus large, et le
 * carre reste centre sur le plant vise comme celui de l'excavation.
 */
public class HarvestEnchantment extends Enchantment {

	private static final int MAX_LEVEL = 3;

	public HarvestEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	/** Cote du carre recolte a ce niveau. */
	public static int sideFor(int level) {
		return 2 * Math.max(1, Math.min(MAX_LEVEL, level)) + 1;
	}

	/** Le rayon correspondant, en blocs de part et d'autre du plant vise. */
	public static int radiusFor(int level) {
		return sideFor(level) / 2;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof HoeItem;
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
