package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Fonte : ce qu'on extrait sort deja fondu.
 *
 * <p>C'est le complement naturel du filon — sortir un gisement entier pour devoir ensuite le passer
 * au four annule une bonne part du temps gagne.
 *
 * <p>Elle est incompatible avec la Fortune, comme le Toucher de soie l'est : les deux se disputent
 * le butin du meme bloc, et laisser cumuler reviendrait a multiplier des lingots.
 */
public class SmeltingEnchantment extends Enchantment {

	public SmeltingEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return super.checkCompatibility(other)
				&& other != Enchantments.BLOCK_FORTUNE
				&& other != Enchantments.SILK_TOUCH;
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
