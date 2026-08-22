
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.celestium.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;

import net.celestium.item.SacADosMagiquePetitItem;
import net.celestium.item.SacADosMagiqueItem;
import net.celestium.item.PiocheenCelestiumItem;
import net.celestium.item.MineraiCelesteItem;
import net.celestium.item.FragementcelesteItem;
import net.celestium.item.CelestiumswordItem;
import net.celestium.item.CelestiumstickItem;
import net.celestium.item.CelestiumarmorItem;
import net.celestium.item.CelestiumPelleItem;
import net.celestium.item.CelestiumHoueItem;
import net.celestium.item.CelestiumHacheItem;
import net.celestium.item.CacafumeurItem;
import net.celestium.item.Caca1Item;
import net.celestium.CelestiumMod;

public class CelestiumModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CelestiumMod.MODID);
	public static final RegistryObject<Item> FRAGEMENTCELESTE = REGISTRY.register("fragementceleste", () -> new FragementcelesteItem());
	public static final RegistryObject<Item> MINERAI_CELESTE = REGISTRY.register("minerai_celeste", () -> new MineraiCelesteItem());
	public static final RegistryObject<Item> PIOCHEEN_CELESTIUM = REGISTRY.register("piocheen_celestium", () -> new PiocheenCelestiumItem());
	public static final RegistryObject<Item> CELESTIUM_ORESTONE = block(CelestiumModBlocks.CELESTIUM_ORESTONE, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> CELESTIUMSWORD = REGISTRY.register("celestiumsword", () -> new CelestiumswordItem());
	public static final RegistryObject<Item> CELESTIUMSTICK = REGISTRY.register("celestiumstick", () -> new CelestiumstickItem());
	public static final RegistryObject<Item> CELESTIUMARMOR_HELMET = REGISTRY.register("celestiumarmor_helmet", () -> new CelestiumarmorItem.Helmet());
	public static final RegistryObject<Item> CELESTIUMARMOR_CHESTPLATE = REGISTRY.register("celestiumarmor_chestplate", () -> new CelestiumarmorItem.Chestplate());
	public static final RegistryObject<Item> CELESTIUMARMOR_LEGGINGS = REGISTRY.register("celestiumarmor_leggings", () -> new CelestiumarmorItem.Leggings());
	public static final RegistryObject<Item> CELESTIUMARMOR_BOOTS = REGISTRY.register("celestiumarmor_boots", () -> new CelestiumarmorItem.Boots());
	public static final RegistryObject<Item> CELESTIUMBLOCK = block(CelestiumModBlocks.CELESTIUMBLOCK, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> CELESTIUM_PELLE = REGISTRY.register("celestium_pelle", () -> new CelestiumPelleItem());
	public static final RegistryObject<Item> CELESTIUM_HACHE = REGISTRY.register("celestium_hache", () -> new CelestiumHacheItem());
	public static final RegistryObject<Item> CELESTIUM_HOUE = REGISTRY.register("celestium_houe", () -> new CelestiumHoueItem());
	public static final RegistryObject<Item> SAC_A_DOS_MAGIQUE = REGISTRY.register("sac_a_dos_magique", () -> new SacADosMagiqueItem());
	public static final RegistryObject<Item> LUCKY_BLOCK = block(CelestiumModBlocks.LUCKY_BLOCK, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> MINI_WARDEN_SPAWN_EGG = REGISTRY.register("mini_warden_spawn_egg", () -> new ForgeSpawnEggItem(CelestiumModEntities.MINI_WARDEN, -10092442, -16777063, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
	public static final RegistryObject<Item> SAC_A_DOS_MAGIQUE_PETIT = REGISTRY.register("sac_a_dos_magique_petit", () -> new SacADosMagiquePetitItem());
	public static final RegistryObject<Item> DEMON_EPEISTE_SPAWN_EGG = REGISTRY.register("demon_epeiste_spawn_egg", () -> new ForgeSpawnEggItem(CelestiumModEntities.DEMON_EPEISTE, -1, -1, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
	public static final RegistryObject<Item> BOIS_DU_DEMON_WOOD = block(CelestiumModBlocks.BOIS_DU_DEMON_WOOD, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_LOG = block(CelestiumModBlocks.BOIS_DU_DEMON_LOG, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_PLANKS = block(CelestiumModBlocks.BOIS_DU_DEMON_PLANKS, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_LEAVES = block(CelestiumModBlocks.BOIS_DU_DEMON_LEAVES, CreativeModeTab.TAB_DECORATIONS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_STAIRS = block(CelestiumModBlocks.BOIS_DU_DEMON_STAIRS, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_SLAB = block(CelestiumModBlocks.BOIS_DU_DEMON_SLAB, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_FENCE = block(CelestiumModBlocks.BOIS_DU_DEMON_FENCE, CreativeModeTab.TAB_DECORATIONS);
	public static final RegistryObject<Item> BOIS_DU_DEMON_FENCE_GATE = block(CelestiumModBlocks.BOIS_DU_DEMON_FENCE_GATE, CreativeModeTab.TAB_REDSTONE);
	public static final RegistryObject<Item> BOIS_DU_DEMON_PRESSURE_PLATE = block(CelestiumModBlocks.BOIS_DU_DEMON_PRESSURE_PLATE, CreativeModeTab.TAB_REDSTONE);
	public static final RegistryObject<Item> BOIS_DU_DEMON_BUTTON = block(CelestiumModBlocks.BOIS_DU_DEMON_BUTTON, CelestiumModTabs.TAB_CELESTIAL_UNIVERS);
	public static final RegistryObject<Item> CACA_1 = REGISTRY.register("caca_1", () -> new Caca1Item());
	public static final RegistryObject<Item> CACAFUMEUR = REGISTRY.register("cacafumeur", () -> new CacafumeurItem());

	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}
