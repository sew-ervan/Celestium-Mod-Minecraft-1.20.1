package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** Genere les tags de blocs : outil requis, appartenance aux familles vanilla, tags Forge. */
public class ModBlockTagsProvider extends BlockTagsProvider {

	public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, CelestiumMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		WoodSet demon = ModBlocks.BOIS_DU_DEMON;

		Block ore = ModBlocks.CELESTIUM_ORE.get();
		Block storage = ModBlocks.CELESTIUM_BLOCK.get();

		Block demoniumOre = ModBlocks.DEMONIUM_ORE.get();
		Block demoniumBlock = ModBlocks.DEMONIUM_BLOCK.get();

		Block corrupted = ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get();

		Block altar = ModBlocks.SUMMONING_ALTAR.get();

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore, storage, demoniumOre, demoniumBlock, corrupted, altar);

		// Le mod d'origine testait le niveau de l'outil dans le code du bloc. En 1.20.1 c'est le
		// role des tags : le minerai et le bloc compact exigent au moins un outil en diamant.
		tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ore, storage, demoniumOre, demoniumBlock, corrupted, altar);
		tag(ModTags.Blocks.NEEDS_CELESTIUM_TOOL).add(storage);

		tag(ModTags.Forge.BLOCK_ORES_CELESTIUM).add(ore);
		tag(ModTags.Forge.BLOCK_STORAGE_BLOCKS_CELESTIUM).add(storage);
		tag(ModTags.Forge.BLOCK_ORES_DEMONIUM).add(demoniumOre);
		tag(ModTags.Forge.BLOCK_STORAGE_BLOCKS_DEMONIUM).add(demoniumBlock);

		// --- Bois du demon ---

		tag(BlockTags.MINEABLE_WITH_AXE).add(
				demon.log.get(), demon.wood.get(), demon.planks.get(), demon.stairs.get(),
				demon.slab.get(), demon.fence.get(), demon.fenceGate.get(),
				demon.pressurePlate.get(), demon.button.get());

		tag(BlockTags.MINEABLE_WITH_HOE).add(demon.leaves.get());

		tag(BlockTags.LOGS).add(demon.log.get(), demon.wood.get());
		tag(BlockTags.LOGS_THAT_BURN).add(demon.log.get(), demon.wood.get());
		tag(BlockTags.PLANKS).add(demon.planks.get());
		tag(BlockTags.LEAVES).add(demon.leaves.get());
		tag(BlockTags.WOODEN_STAIRS).add(demon.stairs.get());
		tag(BlockTags.WOODEN_SLABS).add(demon.slab.get());
		tag(BlockTags.WOODEN_FENCES).add(demon.fence.get());
		tag(BlockTags.FENCE_GATES).add(demon.fenceGate.get());
		tag(BlockTags.WOODEN_PRESSURE_PLATES).add(demon.pressurePlate.get());
		tag(BlockTags.WOODEN_BUTTONS).add(demon.button.get());
	}
}
