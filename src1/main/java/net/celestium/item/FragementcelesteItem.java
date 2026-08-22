
package net.celestium.item;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.celestium.init.CelestiumModTabs;

public class FragementcelesteItem extends Item {
	public FragementcelesteItem() {
		super(new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS).stacksTo(64).fireResistant().rarity(Rarity.COMMON));
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 1.1F;
	}
}
