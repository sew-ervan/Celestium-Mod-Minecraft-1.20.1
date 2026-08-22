package net.celestium.feature.celestium;

import net.celestium.core.material.CelestiumArmorMaterial;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Une piece de l'armure en Celestium.
 *
 * <p>MCreator generait une classe abstraite et quatre sous-classes internes, chacune redeclarant
 * sa texture. Une seule classe parametree par {@link ArmorItem.Type} suffit.
 */
public class CelestiumArmorItem extends ArmorItem {

	private static final String LAYER_1 = "celestium:textures/models/armor/celestium_layer_1.png";
	private static final String LAYER_2 = "celestium:textures/models/armor/celestium_layer_2.png";

	public CelestiumArmorItem(ArmorItem.Type type) {
		super(CelestiumArmorMaterial.CELESTIUM, type, new Item.Properties().fireResistant());
	}

	/**
	 * Les jambieres se dessinent sur la couche interne, les trois autres pieces sur l'externe.
	 * Ce crochet Forge est necessaire : le rendu vanilla derive la texture du nom du materiau et ne
	 * sait pas construire un chemin en dehors de l'espace de noms {@code minecraft}.
	 */
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return slot == EquipmentSlot.LEGS ? LAYER_2 : LAYER_1;
	}
}
