package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Filon : casser un bloc de minerai emporte tout le gisement.
 *
 * <p>C'est a l'excavation ce que l'abattage est a la hache — la meme idee, appliquee a ce qui se
 * tient en amas plutot qu'en carre. Les deux se completent : l'excavation degage un couloir, le
 * filon ramasse ce qu'on y trouve.
 *
 * <p>Un seul niveau, pour la meme raison que l'abattage : un gisement a moitie sorti n'a pas de
 * sens, et c'est la limite de securite qui plafonne la depense.
 */
public class VeinMinerEnchantment extends Enchantment {

	public VeinMinerEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof PickaxeItem;
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
