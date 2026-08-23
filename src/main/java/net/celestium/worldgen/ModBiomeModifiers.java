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
	public static final ResourceKey<BiomeModifier> ADD_DEMON_SWORDSMAN = key("add_demon_swordsman");
	public static final ResourceKey<BiomeModifier> ADD_DEMONIUM_ORE = key("add_demonium_ore");

	/**
	 * Poids d'apparition du demon epeiste.
	 *
	 * <p>Pour donner l'echelle : un zombie pese 95, une araignee 100, un enderman 10, une sorciere
	 * 5. Le total des poids d'un biome sombre tourne autour de 400. A 3, le demon represente donc
	 * moins d'un pour cent des apparitions hostiles — soit une rencontre par plusieurs nuits, ce
	 * qui convient a un boss de 220 points de vie dote de sa propre barre de vie.
	 */
	private static final int DEMON_SPAWN_WEIGHT = 3;

	private ModBiomeModifiers() {
	}

	public static void bootstrap(BootstapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);

		context.register(ADD_CELESTIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.CELESTIUM_ORE)),
				GenerationStep.Decoration.UNDERGROUND_ORES));

		context.register(ADD_DEMONIUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(BiomeTags.IS_NETHER),
				HolderSet.direct(features.getOrThrow(ModPlacedFeatures.DEMONIUM_ORE)),
				GenerationStep.Decoration.UNDERGROUND_ORES));

		// Le mod d'origine avait ces reglages saisis dans MCreator mais desactives : la creature
		// n'apparaissait jamais autrement que par oeuf de spawn.
		context.register(ADD_DEMON_SWORDSMAN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
				biomes.getOrThrow(ModTags.Biomes.DEMON_SWORDSMAN_SPAWNS),
				List.of(new MobSpawnSettings.SpawnerData(
						ModEntities.DEMON_SWORDSMAN.get(), DEMON_SPAWN_WEIGHT, 1, 1))));
	}

	private static ResourceKey<BiomeModifier> key(String name) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, CelestiumMod.id(name));
	}
}
