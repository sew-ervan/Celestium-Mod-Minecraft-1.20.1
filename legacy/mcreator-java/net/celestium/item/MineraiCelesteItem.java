
package net.celestium.item;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.celestium.init.CelestiumModTabs;

public class MineraiCelesteItem extends Item {
	public MineraiCelesteItem() {
		super(new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS).stacksTo(64).fireResistant().rarity(Rarity.RARE));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}
}
