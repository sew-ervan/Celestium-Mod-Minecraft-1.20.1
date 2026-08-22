package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/** Traductions anglaises. Doit couvrir exactement les memes cles que {@link ModFrenchProvider}. */
public class ModEnglishProvider extends LanguageProvider {

	public ModEnglishProvider(PackOutput output) {
		super(output, CelestiumMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.celestium.celestial_univers", "Celestial Univers");

		addItem(ModItems.CELESTIUM_FRAGMENT, "Celestial Fragment");
		addItem(ModItems.CELESTIUM_INGOT, "Celestium Ingot");
		addItem(ModItems.CELESTIUM_STICK, "Celestium Stick");

		addItem(ModItems.CELESTIUM_SWORD, "Celestium Sword");
		addItem(ModItems.CELESTIUM_PICKAXE, "Celestium Pickaxe");
		addItem(ModItems.CELESTIUM_AXE, "Celestium Axe");
		addItem(ModItems.CELESTIUM_SHOVEL, "Celestium Shovel");
		addItem(ModItems.CELESTIUM_HOE, "Celestium Hoe");

		addItem(ModItems.CELESTIUM_HELMET, "Celestium Helmet");
		addItem(ModItems.CELESTIUM_CHESTPLATE, "Celestium Chestplate");
		addItem(ModItems.CELESTIUM_LEGGINGS, "Celestium Leggings");
		addItem(ModItems.CELESTIUM_BOOTS, "Celestium Boots");

		addBlock(ModBlocks.CELESTIUM_ORE, "Celestium Ore");
		addBlock(ModBlocks.CELESTIUM_BLOCK, "Block of Celestium");
		addBlock(ModBlocks.LUCKY_BLOCK, "Lucky Block");

		WoodSet demon = ModBlocks.BOIS_DU_DEMON;
		addBlock(demon.log, "Demon Wood Log");
		addBlock(demon.wood, "Demon Wood");
		addBlock(demon.planks, "Demon Wood Planks");
		addBlock(demon.leaves, "Demon Leaves");
		addBlock(demon.stairs, "Demon Wood Stairs");
		addBlock(demon.slab, "Demon Wood Slab");
		addBlock(demon.fence, "Demon Wood Fence");
		addBlock(demon.fenceGate, "Demon Wood Fence Gate");
		addBlock(demon.pressurePlate, "Demon Wood Pressure Plate");
		addBlock(demon.button, "Demon Wood Button");
	}
}
