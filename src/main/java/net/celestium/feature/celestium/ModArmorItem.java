package net.celestium.feature.celestium;

import net.celestium.core.material.ModArmorMaterials;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Une piece d'armure du mod, quel qu'en soit le materiau.
 *
 * <p>MCreator generait une classe abstraite et quatre sous-classes internes pour la seule armure
 * en Celestium, chacune redeclarant sa texture. Une classe parametree par materiau et par type
 * couvre les deux parures.
 */
public class ModArmorItem extends ArmorItem {

	private final ModArmorMaterials material;

	public ModArmorItem(ModArmorMaterials material, ArmorItem.Type type) {
		super(material, type, new Item.Properties().fireResistant());
		this.material = material;
	}

	public ModArmorMaterials getArmorMaterial() {
		return this.material;
	}

	/**
	 * Ce crochet Forge est necessaire : le rendu vanilla derive la texture du nom du materiau et
	 * ne sait pas construire un chemin en dehors de l'espace de noms {@code minecraft}.
	 */
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return this.material.getLayerTexture(this.getType());
	}
}
