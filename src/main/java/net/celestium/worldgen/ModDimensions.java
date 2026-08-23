package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.OptionalLong;

/**
 * La dimension celeste.
 *
 * <p>Un monde de nuit permanente, eclaire par une lumiere d'ambiance elevee plutot que par le
 * soleil : on y voit clair sous un ciel etoile. Le relief reprend le generateur de bruit de
 * l'Overworld — ce qui donne un terrain credible sans ecrire de generateur — mais un unique biome
 * le recouvre et lui donne ses teintes.
 *
 * <p>Trois registres se repondent ici. Le {@link DimensionType} decrit les regles physiques du
 * monde ; le {@link LevelStem} associe ces regles a un generateur de terrain ; et la cle
 * {@link #DEMON_LEVEL} sert au reste du mod a designer la dimension pour y teleporter.
 */
public final class ModDimensions {

	/** Cle du monde, telle qu'utilisee pour la teleportation. */
	public static final ResourceKey<Level> DEMON_LEVEL =
			ResourceKey.create(Registries.DIMENSION, CelestiumMod.id("demon"));

	public static final ResourceKey<LevelStem> DEMON_STEM =
			ResourceKey.create(Registries.LEVEL_STEM, CelestiumMod.id("demon"));

	public static final ResourceKey<DimensionType> DEMON_TYPE =
			ResourceKey.create(Registries.DIMENSION_TYPE, CelestiumMod.id("demon"));

	/** Minuit fixe : le ciel reste etoile en permanence. */
	private static final long FIXED_TIME = 18_000L;

	/** Lumiere d'ambiance elevee, sans quoi une nuit permanente serait injouable. */
	private static final float AMBIENT_LIGHT = 0.55F;

	/**
	 * Le monde demoniaque est six fois plus petit que l'Overworld : mille blocs parcourus ici en
	 * valent six mille la-bas. Meme principe que le Nether, dont le rapport est de huit.
	 */
	private static final double COORDINATE_SCALE = 6.0;

	/**
	 * Memes bornes verticales que l'Overworld. Le generateur de bruit reutilise est concu pour
	 * cette plage : s'en ecarter deformait le relief, et privait la dimension des couches
	 * profondes ou se trouve le Demonium.
	 */
	private static final int MIN_Y = -64;
	private static final int HEIGHT = 384;

	private ModDimensions() {
	}

	public static void bootstrapType(BootstapContext<DimensionType> context) {
		context.register(DEMON_TYPE, new DimensionType(
				OptionalLong.of(FIXED_TIME),
				true,
				false,
				false,
				true,
				COORDINATE_SCALE,
				true,
				false,
				MIN_Y,
				HEIGHT,
				HEIGHT,
				BlockTags.INFINIBURN_OVERWORLD,
				BuiltinDimensionTypes.OVERWORLD_EFFECTS,
				AMBIENT_LIGHT,
				// Le seuil de lumiere est nul : rien n'apparait spontanement dans le noir.
				new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 0), 0)));
	}

	public static void bootstrapStem(BootstapContext<LevelStem> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
		HolderGetter<NoiseGeneratorSettings> noises = context.lookup(Registries.NOISE_SETTINGS);

		NoiseBasedChunkGenerator generator = new NoiseBasedChunkGenerator(
				new FixedBiomeSource(biomes.getOrThrow(ModBiomes.DEMON_WASTES)),
				noises.getOrThrow(NoiseGeneratorSettings.OVERWORLD));

		context.register(DEMON_STEM, new LevelStem(types.getOrThrow(DEMON_TYPE), generator));
	}
}
