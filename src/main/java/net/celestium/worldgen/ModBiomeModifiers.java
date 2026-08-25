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
import net.minecraft.world.level.biome.Biomes;
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

	/** La licorne dans les grandes etendues plates, le fennec dans le desert. */
	public static final ResourceKey<BiomeModifier> ADD_UNICORN = key("add_unicorn");
	public static final ResourceKey<BiomeModifier> ADD_FENNEC = key("add_fennec");

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

		// La licorne ne court que la ou il y a de la place pour courir : plaines, prairies, champs
		// de fleurs. La poser dans une foret ou une montagne aurait fait d'une bete qu'on repere de
		// loin une bete qu'on ne verrait jamais.
		context.register(ADD_UNICORN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
				HolderSet.direct(
						biomes.getOrThrow(Biomes.PLAINS),
						biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS),
						biomes.getOrThrow(Biomes.MEADOW),
						biomes.getOrThrow(Biomes.FLOWER_FOREST)),
				List.of(new MobSpawnSettings.SpawnerData(ModEntities.UNICORN.get(), 3, 1, 1))));

		// Le fennec au desert, et nulle part ailleurs : c'est la seule chose qui donne une raison
		// de s'arreter dans une etendue que le mod traversait jusqu'ici sans rien y trouver.
		context.register(ADD_FENNEC, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
				HolderSet.direct(biomes.getOrThrow(Biomes.DESERT)),
				List.of(new MobSpawnSettings.SpawnerData(ModEntities.FENNEC.get(), 12, 1, 2))));
	}

	private static ResourceKey<BiomeModifier> key(String name) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, CelestiumMod.id(name));
	}
}
