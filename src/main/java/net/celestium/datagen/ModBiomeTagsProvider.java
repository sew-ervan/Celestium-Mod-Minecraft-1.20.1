package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** Genere les tags de biomes : ou apparaissent les creatures, ou se posent les structures. */
public class ModBiomeTagsProvider extends TagsProvider<Biome> {

	public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			ExistingFileHelper existingFileHelper) {
		super(output, Registries.BIOME, lookupProvider, CelestiumMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		// Le demon epeiste hante les lieux sombres : forets denses, marais, et les profondeurs
		// du Deep Dark. Le laisser apparaitre partout en ferait une nuisance plutot qu'une
		// rencontre.
		this.tag(ModTags.Biomes.DEMON_SWORDSMAN_SPAWNS)
				.add(Biomes.DARK_FOREST)
				.add(Biomes.SWAMP)
				.add(Biomes.MANGROVE_SWAMP)
				.add(Biomes.DEEP_DARK);
	}
}
