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

		// Le Demonium n'a pas encore de textures propres. Plutot que de dupliquer celles du
		// Celestium, ses modeles pointent vers des textures vanilla : le client les possede deja,
		// rien n'est copie, et le rendu reste distinct.
		flatVanilla(ModItems.DEMONIUM_FRAGMENT, "item/netherite_scrap");
		flatVanilla(ModItems.DEMONIUM_INGOT, "item/netherite_ingot");
		handheldVanilla(ModItems.DEMONIUM_STICK, "item/blaze_rod");

		handheldVanilla(ModItems.DEMONIUM_SWORD, "item/netherite_sword");
		handheldVanilla(ModItems.DEMONIUM_PICKAXE, "item/netherite_pickaxe");
		handheldVanilla(ModItems.DEMONIUM_AXE, "item/netherite_axe");
		handheldVanilla(ModItems.DEMONIUM_SHOVEL, "item/netherite_shovel");
		handheldVanilla(ModItems.DEMONIUM_HOE, "item/netherite_hoe");

		flatVanilla(ModItems.DEMONIUM_HELMET, "item/netherite_helmet");
		flatVanilla(ModItems.DEMONIUM_CHESTPLATE, "item/netherite_chestplate");
		flatVanilla(ModItems.DEMONIUM_LEGGINGS, "item/netherite_leggings");
		flatVanilla(ModItems.DEMONIUM_BOOTS, "item/netherite_boots");

		spawnEgg(ModItems.MINI_WARDEN_SPAWN_EGG);
		spawnEgg(ModItems.DEMON_SWORDSMAN_SPAWN_EGG);
	}

	/** Objet plat, tenu comme une ressource : lingots, fragments, pieces d'armure. */
	private void flat(RegistryObject<Item> item) {
		String name = name(item);
		withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("item/" + name));
	}

	/** Objet plat dont la texture est empruntee au jeu de base. */
	private void flatVanilla(RegistryObject<Item> item, String vanillaTexture) {
		withExistingParent(name(item), mcLoc("item/generated")).texture("layer0", mcLoc(vanillaTexture));
	}

	/** Objet tenu par le manche dont la texture est empruntee au jeu de base. */
	private void handheldVanilla(RegistryObject<Item> item, String vanillaTexture) {
		withExistingParent(name(item), mcLoc("item/handheld")).texture("layer0", mcLoc(vanillaTexture));
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
