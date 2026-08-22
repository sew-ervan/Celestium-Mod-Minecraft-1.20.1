package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Ajout des filons de Celestium aux biomes de l'Overworld.
 *
 * <p>Le mod d'origine ciblait {@code forge:any}, c'est-a-dire tous les biomes de toutes les
 * dimensions, et compensait ensuite dans le code de la feature par un test sur la dimension. Le
 * tag {@code is_overworld} exprime directement l'intention.
 */
public final class ModBiomeModifiers {

	public static final ResourceKey<BiomeModifier> ADD_CELESTIUM_ORE = key("add_celestium_ore");
	public static final ResourceKey<BiomeModifier> ADD_CEMETERY = key("add_cemetery");

	private ModBiomeModifiers() {
	}

	public static void bootstrap(BootstapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);

		context.register(ADD_CELESTIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.CELESTIUM_ORE)),
				GenerationStep.Decoration.UNDERGROUND_ORES));

		// Le cimetiere vise tout l'Overworld : c'est la feature elle-meme qui refuse de se poser
		// dans l'eau. Le mod d'origine ne le generait au contraire QUE dans les oceans et les
		// rivieres, ce qui ressemble a une inversion de la liste de biomes.
		context.register(ADD_CEMETERY, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.CEMETERY)),
				GenerationStep.Decoration.SURFACE_STRUCTURES));
	}

	private static ResourceKey<BiomeModifier> key(String name) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, CelestiumMod.id(name));
	}
}
