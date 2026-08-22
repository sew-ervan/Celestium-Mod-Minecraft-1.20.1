
package net.celestium.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem;

import net.celestium.init.CelestiumModTabs;
import net.celestium.init.CelestiumModItems;

public class CelestiumHoueItem extends HoeItem {
	public CelestiumHoueItem() {
		super(new Tier() {
			public int getUses() {
				return 5000;
			}

			public float getSpeed() {
				return 11f;
			}

			public float getAttackDamageBonus() {
				return -1f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 20;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CelestiumModItems.FRAGEMENTCELESTE.get()));
			}
		}, 0, 0f, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS).fireResistant());
	}
}
