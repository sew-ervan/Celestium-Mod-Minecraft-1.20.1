package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.worldgen.ModBiomeModifiers;
import net.celestium.worldgen.ModBiomes;
import net.celestium.worldgen.ModDimensions;
import net.celestium.worldgen.ModConfiguredFeatures;
import net.celestium.worldgen.ModPlacedFeatures;
import net.celestium.worldgen.ModStructures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Genere les fichiers de generation du monde.
 *
 * <p>Depuis la 1.19.3, filons et modificateurs de biome sont des donnees et non plus du code
 * enregistre au demarrage. Les classes {@code Feature} du mod d'origine, qui appelaient
 * {@code FeatureUtils.register} depuis un initialiseur statique, n'ont plus lieu d'etre.
 */
public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {

	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
			.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
			.add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
			.add(Registries.TEMPLATE_POOL, ModStructures::bootstrapPool)
			.add(Registries.STRUCTURE, ModStructures::bootstrapStructure)
			.add(Registries.STRUCTURE_SET, ModStructures::bootstrapSet)
			.add(Registries.BIOME, ModBiomes::bootstrap)
			.add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapType)
			.add(Registries.LEVEL_STEM, ModDimensions::bootstrapStem);

	public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(CelestiumMod.MOD_ID));
	}
}
