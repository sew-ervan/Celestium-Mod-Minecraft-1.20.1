package net.celestium.feature.celestium;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Lingot de Celestium, obtenu en assemblant neuf fragments.
 *
 * <p>Porte en permanence le reflet des objets enchantes, pour le distinguer au premier coup d'oeil
 * dans un inventaire.
 */
public class CelestiumIngotItem extends Item {

	public CelestiumIngotItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.RARE));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
