
package net.celestium.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

import net.celestium.init.CelestiumModTabs;

public class CelestiumstickItem extends Item {
	public CelestiumstickItem() {
		super(new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS).stacksTo(64).rarity(Rarity.COMMON));
	}
}
