package net.celestium.core.registry;

import net.celestium.CelestiumMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

/** Tous les tags du mod, declares en un seul endroit. */
public final class ModTags {

	private ModTags() {
	}

	public static final class Blocks {

		/** Blocs qui exigent au moins un outil en Celestium pour laisser leur butin. */
		public static final TagKey<Block> NEEDS_CELESTIUM_TOOL = tag("needs_celestium_tool");

		private Blocks() {
		}

		private static TagKey<Block> tag(String name) {
			return TagKey.create(Registries.BLOCK, CelestiumMod.id(name));
		}
	}

	public static final class Items {

		/**
		 * Recompenses possibles du bloc chance. Le tirage se fait directement dans la table de
		 * butin du bloc, sans code.
		 */
		public static final TagKey<Item> LUCKY_BLOCK_REWARDS = tag("lucky_block_rewards");

		/** Rondins du bois du demon, pour la recette de planches. */
		public static final TagKey<Item> BOIS_DU_DEMON_LOGS = tag("bois_du_demon_logs");

		private Items() {
		}

		private static TagKey<Item> tag(String name) {
			return TagKey.create(Registries.ITEM, CelestiumMod.id(name));
		}
	}

	public static final class Biomes {

		/**
		 * Biomes ou le demon epeiste peut apparaitre : les lieux sombres et hostiles de la surface,
		 * plus le Deep Dark.
		 */
		public static final TagKey<Biome> DEMON_SWORDSMAN_SPAWNS = tag("demon_swordsman_spawns");

		private Biomes() {
		}

		private static TagKey<Biome> tag(String name) {
			return TagKey.create(Registries.BIOME, CelestiumMod.id(name));
		}
	}

	/** Tags partages entre mods, dans l'espace de noms {@code forge}. */
	public static final class Forge {

		public static final TagKey<Item> INGOTS_CELESTIUM = itemTag("ingots/celestium");
		public static final TagKey<Item> NUGGETS_CELESTIUM = itemTag("nuggets/celestium");
		public static final TagKey<Item> STORAGE_BLOCKS_CELESTIUM = itemTag("storage_blocks/celestium");
		public static final TagKey<Item> ORES_CELESTIUM = itemTag("ores/celestium");

		public static final TagKey<Block> BLOCK_ORES_CELESTIUM = blockTag("ores/celestium");
		public static final TagKey<Block> BLOCK_STORAGE_BLOCKS_CELESTIUM = blockTag("storage_blocks/celestium");

		private Forge() {
		}

		private static TagKey<Item> itemTag(String name) {
			return TagKey.create(Registries.ITEM, new ResourceLocation("forge", name));
		}

		private static TagKey<Block> blockTag(String name) {
			return TagKey.create(Registries.BLOCK, new ResourceLocation("forge", name));
		}
	}
}
