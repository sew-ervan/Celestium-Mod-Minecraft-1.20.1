package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** Genere les tags d'items, dont la liste des recompenses du bloc chance. */
public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, CelestiumMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		WoodSet demon = ModBlocks.BOIS_DU_DEMON;

		tag(ModTags.Forge.NUGGETS_CELESTIUM).add(ModItems.CELESTIUM_FRAGMENT.get());
		tag(ModTags.Forge.INGOTS_CELESTIUM).add(ModItems.CELESTIUM_INGOT.get());
		tag(ModTags.Forge.ORES_CELESTIUM).add(ModBlocks.CELESTIUM_ORE.get().asItem());
		tag(ModTags.Forge.STORAGE_BLOCKS_CELESTIUM).add(ModBlocks.CELESTIUM_BLOCK.get().asItem());

		tag(ModTags.Forge.NUGGETS_DEMONIUM).add(ModItems.DEMONIUM_FRAGMENT.get());
		tag(ModTags.Forge.INGOTS_DEMONIUM).add(ModItems.DEMONIUM_INGOT.get());
		tag(ModTags.Forge.ORES_DEMONIUM).add(ModBlocks.DEMONIUM_ORE.get().asItem());
		tag(ModTags.Forge.STORAGE_BLOCKS_DEMONIUM).add(ModBlocks.DEMONIUM_BLOCK.get().asItem());

		tag(ModTags.Items.BOIS_DU_DEMON_LOGS).add(demon.log.get().asItem(), demon.wood.get().asItem());

		tag(ItemTags.LOGS).addTag(ModTags.Items.BOIS_DU_DEMON_LOGS);
		tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.BOIS_DU_DEMON_LOGS);
		tag(ItemTags.PLANKS).add(demon.planks.get().asItem());
		tag(ItemTags.LEAVES).add(demon.leaves.get().asItem());
		tag(ItemTags.WOODEN_STAIRS).add(demon.stairs.get().asItem());
		tag(ItemTags.WOODEN_SLABS).add(demon.slab.get().asItem());
		tag(ItemTags.WOODEN_FENCES).add(demon.fence.get().asItem());
		tag(ItemTags.FENCE_GATES).add(demon.fenceGate.get().asItem());
		tag(ItemTags.WOODEN_PRESSURE_PLATES).add(demon.pressurePlate.get().asItem());
		tag(ItemTags.WOODEN_BUTTONS).add(demon.button.get().asItem());

		// Recompenses du bloc chance. La table de butin tire une entree au hasard dans ce tag :
		// modifier l'equilibrage du bloc revient a editer cette liste, sans toucher au code.
		tag(ModTags.Items.LUCKY_BLOCK_REWARDS).add(
				ModItems.CELESTIUM_FRAGMENT.get(),
				ModItems.CELESTIUM_INGOT.get(),
				demon.log.get().asItem(),
				demon.planks.get().asItem(),
				Items.COAL_ORE,
				Items.IRON_INGOT,
				Items.DIAMOND,
				Items.ENCHANTING_TABLE,
				Items.DIAMOND_HOE,
				Items.BREWING_STAND,
				Items.DAMAGED_ANVIL,
				Items.GOAT_HORN,
				Items.WITHER_SKELETON_SKULL,
				Items.LAPIS_BLOCK,
				Items.SCULK_SENSOR,
				Items.GOLDEN_HORSE_ARMOR,
				Items.DIAMOND_HORSE_ARMOR,
				Items.AMETHYST_BLOCK,
				Items.BEACON,
				Items.DIRT);
	}
}
