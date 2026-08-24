package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Genere les modeles des items qui ne sont pas des blocs.
 *
 * <p>Les modeles d'items de blocs sont produits par {@link ModBlockStateProvider}, au plus pres
 * des modeles de blocs dont ils derivent.
 */
public class ModItemModelProvider extends ItemModelProvider {

	public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, CelestiumMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		flat(ModItems.CELESTIUM_FRAGMENT);
		flat(ModItems.CELESTIUM_INGOT);
		flat(ModItems.CELESTIUM_STICK);

		flat(ModItems.CELESTIUM_HELMET);
		flat(ModItems.CELESTIUM_CHESTPLATE);
		flat(ModItems.CELESTIUM_LEGGINGS);
		flat(ModItems.CELESTIUM_BOOTS);

		handheld(ModItems.CELESTIUM_SWORD);
		handheld(ModItems.CELESTIUM_PICKAXE);
		handheld(ModItems.CELESTIUM_AXE);
		handheld(ModItems.CELESTIUM_SHOVEL);
		handheld(ModItems.CELESTIUM_HOE);

		// Les trois sacs partagent deux textures : le format moyen reprend celle du grand.
		flat(ModItems.BACKPACK);
		flat(ModItems.BACKPACK_SMALL);
		texturedFlat(ModItems.BACKPACK_MEDIUM, "backpack");
		texturedFlat(ModItems.BACKPACK_HUGE, "backpack");

		// Les textures du Demonium sont derivees de celles du Celestium par corruption : meme
		// silhouette, teinte basculee vers le rouge, surface brulee.
		flat(ModItems.CORRUPTED_BOOK);
		flat(ModItems.CORRUPTED_EYE);
		flat(ModItems.CORRUPTED_CELESTIUM_FRAGMENT);
		flat(ModItems.CORRUPTED_CELESTIUM_INGOT);
		flat(ModItems.CORRUPTED_CELESTIUM_HELMET);
		flat(ModItems.CORRUPTED_CELESTIUM_CHESTPLATE);
		flat(ModItems.CORRUPTED_CELESTIUM_LEGGINGS);
		flat(ModItems.CORRUPTED_CELESTIUM_BOOTS);

		handheld(ModItems.CORRUPTED_CELESTIUM_SWORD);
		handheld(ModItems.CORRUPTED_CELESTIUM_PICKAXE);
		handheld(ModItems.CORRUPTED_CELESTIUM_AXE);
		handheld(ModItems.CORRUPTED_CELESTIUM_SHOVEL);
		handheld(ModItems.CORRUPTED_CELESTIUM_HOE);

		flat(ModItems.DEMON_HEART);

		flat(ModItems.DEMONIUM_FRAGMENT);
		flat(ModItems.DEMONIUM_INGOT);
		flat(ModItems.DEMONIUM_STICK);

		handheld(ModItems.DEMONIUM_SWORD);
		handheld(ModItems.DEMONIUM_PICKAXE);
		handheld(ModItems.DEMONIUM_AXE);
		handheld(ModItems.DEMONIUM_SHOVEL);
		handheld(ModItems.DEMONIUM_HOE);

		flat(ModItems.DEMONIUM_HELMET);
		flat(ModItems.DEMONIUM_CHESTPLATE);
		flat(ModItems.DEMONIUM_LEGGINGS);
		flat(ModItems.DEMONIUM_BOOTS);

		spawnEgg(ModItems.MINI_WARDEN_SPAWN_EGG);
		spawnEgg(ModItems.DEMON_SWORDSMAN_SPAWN_EGG);
		spawnEgg(ModItems.PARASITE_SPAWN_EGG);
		spawnEgg(ModItems.CORRUPTED_VILLAGER_SPAWN_EGG);
	}

	/** Objet plat, tenu comme une ressource : lingots, fragments, pieces d'armure. */
	private void flat(RegistryObject<Item> item) {
		String name = name(item);
		withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("item/" + name));
	}

	/** Objet plat dont la texture porte un autre nom que l'item. */
	private void texturedFlat(RegistryObject<Item> item, String textureName) {
		withExistingParent(name(item), mcLoc("item/generated")).texture("layer0", modLoc("item/" + textureName));
	}

	/** Oeuf d'apparition : le modele vanilla se teinte des deux couleurs de l'oeuf. */
	private void spawnEgg(RegistryObject<Item> item) {
		withExistingParent(name(item), mcLoc("item/template_spawn_egg"));
	}

	/** Objet tenu par le manche : outils et armes. */
	private void handheld(RegistryObject<Item> item) {
		String name = name(item);
		withExistingParent(name, mcLoc("item/handheld")).texture("layer0", modLoc("item/" + name));
	}

	private String name(RegistryObject<Item> item) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(item.get());
		if (key == null) {
			throw new IllegalStateException("Item non enregistre : " + item.getId());
		}
		return key.getPath();
	}
}
