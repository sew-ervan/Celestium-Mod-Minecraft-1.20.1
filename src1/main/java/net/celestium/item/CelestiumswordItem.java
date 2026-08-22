
package net.celestium.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import net.celestium.procedures.MagieAttaqueTPProcedure;
import net.celestium.init.CelestiumModTabs;
import net.celestium.init.CelestiumModItems;

public class CelestiumswordItem extends SwordItem {
	public CelestiumswordItem() {
		super(new Tier() {
			public int getUses() {
				return 5000;
			}

			public float getSpeed() {
				return 0f;
			}

			public float getAttackDamageBonus() {
				return 7f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CelestiumModItems.FRAGEMENTCELESTE.get()));
			}
		}, 3, -2.4f, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS).fireResistant());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		MagieAttaqueTPProcedure.execute(world, entity, ar.getObject());
		return ar;
	}
}
