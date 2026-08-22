
package net.celestium.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.celestium.init.CelestiumModTabs;
import net.celestium.init.CelestiumModItems;

public class PiocheenCelestiumItem extends PickaxeItem {
	public PiocheenCelestiumItem() {
		super(new Tier() {
			public int getUses() {
				return 5000;
			}

			public float getSpeed() {
				return 10f;
			}

			public float getAttackDamageBonus() {
				return 5f;
			}

			public int getLevel() {
				return 5;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CelestiumModItems.FRAGEMENTCELESTE.get()));
			}
		}, 1, -2.8f, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS).fireResistant());
	}
}
