package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Biomes propres au mod.
 *
 * <p>Le Vide Celeste est l'unique biome de la dimension celeste : un plateau nocturne eclaire par
 * une lumiere d'ambiance elevee, ou le Celestium affleure bien plus qu'en surface.
 */
public final class ModBiomes {

	public static final ResourceKey<Biome> CELESTIAL_VOID = key("celestial_void");

	/** Teintes de la dimension : nuit violette, brume mauve, feuillage bleute. */
	private static final int SKY_COLOR = 0x1A0A33;
	private static final int FOG_COLOR = 0x2B1B4D;
	private static final int WATER_COLOR = 0x4A5BC4;
	private static final int WATER_FOG_COLOR = 0x140A2E;
	private static final int GRASS_COLOR = 0x7FA8FF;
	private static final int FOLIAGE_COLOR = 0x9BB8FF;

	private ModBiomes() {
	}

	public static void bootstrap(BootstapContext<Biome> context) {
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

		context.register(CELESTIAL_VOID, celestialVoid(features, carvers));
	}

	private static Biome celestialVoid(HolderGetter<PlacedFeature> features,
			HolderGetter<ConfiguredWorldCarver<?>> carvers) {

		// La dimension ne fait apparaitre aucune creature d'elle-meme : ce qui s'y trouve y est
		// place par le mod, pas par le cycle d'apparition ordinaire.
		MobSpawnSettings spawns = new MobSpawnSettings.Builder().build();

		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);

		// Un socle vanilla minimal : grottes, lacs et surface. Sans cela le terrain serait un
		// bloc plein sans relief ni ouverture.
		BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);
		BiomeDefaultFeatures.addDefaultCrystalFormations(generation);
		BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(generation);
		BiomeDefaultFeatures.addDefaultSprings(generation);
		BiomeDefaultFeatures.addSurfaceFreezing(generation);

		// Ce qui justifie le voyage : le Celestium y affleure sur toute la hauteur jouable, la ou
		// il faut creuser jusqu'au fond du monde en surface.
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
				features.getOrThrow(ModPlacedFeatures.CELESTIUM_ORE_CELESTIAL));

		return new Biome.BiomeBuilder()
				.hasPrecipitation(false)
				.temperature(0.6F)
				.downfall(0.0F)
				.specialEffects(new BiomeSpecialEffects.Builder()
						.skyColor(SKY_COLOR)
						.fogColor(FOG_COLOR)
						.waterColor(WATER_COLOR)
						.waterFogColor(WATER_FOG_COLOR)
						.grassColorOverride(GRASS_COLOR)
						.foliageColorOverride(FOLIAGE_COLOR)
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.ambientLoopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)
						.backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK))
						.build())
				.mobSpawnSettings(spawns)
				.generationSettings(generation.build())
				.build();
	}

	private static ResourceKey<Biome> key(String name) {
		return ResourceKey.create(Registries.BIOME, CelestiumMod.id(name));
	}
}
