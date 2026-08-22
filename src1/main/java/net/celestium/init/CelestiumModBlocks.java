
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.celestium.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.celestium.block.LuckyBlockBlock;
import net.celestium.block.CelestiumblockBlock;
import net.celestium.block.CelestiumOrestoneBlock;
import net.celestium.block.Bois_du_demonWoodBlock;
import net.celestium.block.Bois_du_demonStairsBlock;
import net.celestium.block.Bois_du_demonSlabBlock;
import net.celestium.block.Bois_du_demonPressurePlateBlock;
import net.celestium.block.Bois_du_demonPlanksBlock;
import net.celestium.block.Bois_du_demonLogBlock;
import net.celestium.block.Bois_du_demonLeavesBlock;
import net.celestium.block.Bois_du_demonFenceGateBlock;
import net.celestium.block.Bois_du_demonFenceBlock;
import net.celestium.block.Bois_du_demonButtonBlock;
import net.celestium.CelestiumMod;

public class CelestiumModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, CelestiumMod.MODID);
	public static final RegistryObject<Block> CELESTIUM_ORESTONE = REGISTRY.register("celestium_orestone", () -> new CelestiumOrestoneBlock());
	public static final RegistryObject<Block> CELESTIUMBLOCK = REGISTRY.register("celestiumblock", () -> new CelestiumblockBlock());
	public static final RegistryObject<Block> LUCKY_BLOCK = REGISTRY.register("lucky_block", () -> new LuckyBlockBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_WOOD = REGISTRY.register("bois_du_demon_wood", () -> new Bois_du_demonWoodBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_LOG = REGISTRY.register("bois_du_demon_log", () -> new Bois_du_demonLogBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_PLANKS = REGISTRY.register("bois_du_demon_planks", () -> new Bois_du_demonPlanksBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_LEAVES = REGISTRY.register("bois_du_demon_leaves", () -> new Bois_du_demonLeavesBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_STAIRS = REGISTRY.register("bois_du_demon_stairs", () -> new Bois_du_demonStairsBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_SLAB = REGISTRY.register("bois_du_demon_slab", () -> new Bois_du_demonSlabBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_FENCE = REGISTRY.register("bois_du_demon_fence", () -> new Bois_du_demonFenceBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_FENCE_GATE = REGISTRY.register("bois_du_demon_fence_gate", () -> new Bois_du_demonFenceGateBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_PRESSURE_PLATE = REGISTRY.register("bois_du_demon_pressure_plate", () -> new Bois_du_demonPressurePlateBlock());
	public static final RegistryObject<Block> BOIS_DU_DEMON_BUTTON = REGISTRY.register("bois_du_demon_button", () -> new Bois_du_demonButtonBlock());
}
