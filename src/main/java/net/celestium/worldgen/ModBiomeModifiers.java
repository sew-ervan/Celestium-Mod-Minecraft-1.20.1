package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.init.ModEntities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Ajout des filons de Celestium aux biomes de l'Overworld.
 *
 * <p>Le mod d'origine ciblait {@code forge:any}, c'est-a-dire tous les biomes de toutes les
 * dimensions, et compensait ensuite dans le code de la feature par un test sur la dimension. Le
 * tag {@code is_overworld} exprime directement l'intention.
 */
public final class ModBiomeModifiers {

	public static final ResourceKey<BiomeModifier> ADD_CELESTIUM_ORE = key("add_celestium_ore");

	/**
	 * Chaque bloc chance se seme dans le monde qui lui correspond : l'ordinaire dans l'Overworld,
	 * le corrompu dans le Nether — d'ou vient la matiere qui corrompt le Celestium — et celui du
	 * demon dans les Terres du demon, qui recoivent le leur par leur biome plutot que par un
	 * modificateur.
	 */
	public static final ResourceKey<BiomeModifier> ADD_LUCKY_BLOCK = key("add_lucky_block");
	public static final ResourceKey<BiomeModifier> ADD_CORRUPTED_LUCKY_BLOCK =
			key("add_corrupted_lucky_block");

	/** La matiere noire, dans tout l'Overworld. */
	public static final ResourceKey<BiomeModifier> ADD_DARK_MATTER_ORE = key("add_dark_matter_ore");

	private ModBiomeModifiers() {
	}

	public static void bootstrap(BootstapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);

		context.register(ADD_CELESTIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.CELESTIUM_ORE)),
				GenerationStep.Decoration.UNDERGROUND_ORES));

		context.register(ADD_LUCKY_BLOCK, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.LUCKY_BLOCK)),
				GenerationStep.Decoration.UNDERGROUND_DECORATION));

		context.register(ADD_CORRUPTED_LUCKY_BLOCK, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_NETHER),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.CORRUPTED_LUCKY_BLOCK)),
				GenerationStep.Decoration.UNDERGROUND_DECORATION));

		context.register(ADD_DARK_MATTER_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.DARK_MATTER_ORE)),
				GenerationStep.Decoration.UNDERGROUND_ORES));
	}

	private static ResourceKey<BiomeModifier> key(String name) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, CelestiumMod.id(name));
	}
}
