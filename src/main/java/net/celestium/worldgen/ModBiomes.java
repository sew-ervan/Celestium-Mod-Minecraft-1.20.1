package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.celestium.init.ModEntities;
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
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Biomes propres au mod.
 *
 * <p>Les Terres du demon sont l'unique biome de la dimension demoniaque : un monde de nuit
 * permanente, baigne de rouge, ou le Demonium affleure et ou poussent les seuls arbres qui
 * supportent cette terre — le bois du demon.
 */
public final class ModBiomes {

	public static final ResourceKey<Biome> DEMON_WASTES = key("demon_wastes");

	/** L'unique biome des terres corrompues. */
	public static final ResourceKey<Biome> CORRUPTED_LANDS = key("corrupted_lands");

	// Teintes des terres corrompues : un ciel malade, entre le vert de l'un et le rouge de l'autre.
	private static final int CORRUPTED_SKY_COLOR = 0x3A2E1F;
	private static final int CORRUPTED_FOG_COLOR = 0x574328;
	private static final int CORRUPTED_WATER_COLOR = 0x4C5B2E;
	private static final int CORRUPTED_WATER_FOG_COLOR = 0x2B3318;
	private static final int CORRUPTED_GRASS_COLOR = 0x6E7A2C;
	private static final int CORRUPTED_FOLIAGE_COLOR = 0x7C6A22;

	/** Teintes de la dimension : ciel de braise, brume sanglante, feuillage carbonise. */
	private static final int SKY_COLOR = 0x2B0708;
	private static final int FOG_COLOR = 0x4A0F12;
	private static final int WATER_COLOR = 0x6B1418;
	private static final int WATER_FOG_COLOR = 0x2A0507;
	private static final int GRASS_COLOR = 0x7A2420;
	private static final int FOLIAGE_COLOR = 0x8E2B22;

	private ModBiomes() {
	}

	public static void bootstrap(BootstapContext<Biome> context) {
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

		context.register(DEMON_WASTES, demonWastes(features, carvers));
		context.register(CORRUPTED_LANDS, corruptedLands(features, carvers));
	}

	/**
	 * Les terres corrompues : l'Overworld et le Nether l'un dans l'autre.
	 *
	 * <p>La faune dit la meme chose que le sol. On y croise ce qui peuple l'Overworld et ce qui
	 * peuple le Nether, aux memes endroits — zombies et blazes, squelettes ordinaires et squelettes
	 * du Wither. Rien de la dimension du demon : celle-ci se merite, elle ne deborde pas.
	 */
	private static Biome corruptedLands(HolderGetter<PlacedFeature> features,
			HolderGetter<ConfiguredWorldCarver<?>> carvers) {

		MobSpawnSettings spawns = new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.ZOMBIE, 40, 2, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.SKELETON, 30, 2, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.CREEPER, 20, 1, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.ENDERMAN, 15, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.ZOMBIFIED_PIGLIN, 25, 2, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.MAGMA_CUBE, 15, 1, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.WITHER_SKELETON, 10, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						net.minecraft.world.entity.EntityType.BLAZE, 8, 1, 2))
				.build();

		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);

		BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);
		BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(generation);
		BiomeDefaultFeatures.addDefaultSprings(generation);
		BiomeDefaultFeatures.addDefaultOres(generation);

		// Ce qui justifie le voyage : le Celestium corrompu ne s'extrait que la, et c'est lui qui
		// ouvre les Terres du demon.
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
				features.getOrThrow(ModPlacedFeatures.CORRUPTED_CELESTIUM_ORE));

		return new Biome.BiomeBuilder()
				.hasPrecipitation(false)
				.temperature(0.9F)
				.downfall(0.2F)
				.specialEffects(new BiomeSpecialEffects.Builder()
						.skyColor(CORRUPTED_SKY_COLOR)
						.fogColor(CORRUPTED_FOG_COLOR)
						.waterColor(CORRUPTED_WATER_COLOR)
						.waterFogColor(CORRUPTED_WATER_FOG_COLOR)
						.grassColorOverride(CORRUPTED_GRASS_COLOR)
						.foliageColorOverride(CORRUPTED_FOLIAGE_COLOR)
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.ambientLoopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)
						.backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY))
						.build())
				.mobSpawnSettings(spawns)
				.generationSettings(generation.build())
				.build();
	}

	private static Biome demonWastes(HolderGetter<PlacedFeature> features,
			HolderGetter<ConfiguredWorldCarver<?>> carvers) {

		// Le parasite est la vermine de fond, le villageois corrompu hante les ruines, et le demon
		// epeiste reste l'evenement rare. Les poids traduisent ce rapport.
		MobSpawnSettings spawns = new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						ModEntities.PARASITE.get(), 70, 3, 6))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						ModEntities.CORRUPTED_VILLAGER.get(), 25, 1, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
						ModEntities.DEMON_SWORDSMAN.get(), 2, 1, 1))
				.build();

		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);

		// Un socle vanilla minimal : grottes, lacs et sources. Sans cela le terrain serait un
		// bloc plein sans relief ni ouverture.
		BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);
		BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(generation);

		// Pas de sources : elles poseraient de l'eau et de la lave dans une dimension qu'on a
		// justement asséchée en descendant son niveau de la mer sous le plancher du monde. Le
		// generateur de terrain n'en met plus, mais cette decoration-la en remettrait.

		// Ce qui justifie le voyage : le Demonium ne se trouve que la.
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
				features.getOrThrow(ModPlacedFeatures.DEMONIUM_ORE_WASTES));

		// Les seuls arbres qui poussent sur cette terre.
		generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
				features.getOrThrow(ModPlacedFeatures.DEMON_TREE));

		// Le bloc chance du demon ne se trouve que dans cette dimension.
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION,
				features.getOrThrow(ModPlacedFeatures.DEMON_LUCKY_BLOCK));

		return new Biome.BiomeBuilder()
				.hasPrecipitation(false)
				.temperature(1.2F)
				.downfall(0.0F)
				.specialEffects(new BiomeSpecialEffects.Builder()
						.skyColor(SKY_COLOR)
						.fogColor(FOG_COLOR)
						.waterColor(WATER_COLOR)
						.waterFogColor(WATER_FOG_COLOR)
						.grassColorOverride(GRASS_COLOR)
						.foliageColorOverride(FOLIAGE_COLOR)
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.ambientLoopSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)
						.backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_NETHER_WASTES))
						.build())
				.mobSpawnSettings(spawns)
				.generationSettings(generation.build())
				.build();
	}

	private static ResourceKey<Biome> key(String name) {
		return ResourceKey.create(Registries.BIOME, CelestiumMod.id(name));
	}
}
