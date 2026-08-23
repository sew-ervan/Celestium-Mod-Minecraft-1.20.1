package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;

/**
 * Les regles de terrain des Terres du demon.
 *
 * <p>Le relief reprend celui de l'Overworld — meme bruit, memes reliefs, memes grottes — mais le
 * monde y est sec. C'est le seul point qui change, et il tient a quatre reglages.
 *
 * <p>La mer est descendue sous le plancher du monde et le fluide par defaut devient de l'air : il
 * ne reste ni ocean, ni lac, ni la nappe de lave que le generateur pose d'office sous l'altitude
 * -54, puisque celle-ci ne s'applique qu'en dessous du niveau de la mer declare. Les nappes
 * souterraines sont coupees, sans quoi les grottes se rempliraient d'eau a leur tour.
 *
 * <p>Les veines de minerai vanilla sont coupees elles aussi : le Demonium doit rester le seul
 * minerai de la dimension, et ces veines y auraient sinon depose du cuivre et du fer.
 */
public final class ModNoiseSettings {

	public static final ResourceKey<NoiseGeneratorSettings> DEMON_WASTES =
			ResourceKey.create(Registries.NOISE_SETTINGS, CelestiumMod.id("demon_wastes"));

	/**
	 * Niveau de la mer, place sous le plancher du monde.
	 *
	 * <p>Ce n'est pas qu'une precaution : le generateur choisit la lave plutot que le fluide par
	 * defaut sous le plus bas de -54 et de cette valeur. La descendre a -64 place donc aussi la
	 * lave hors du monde.
	 */
	private static final int SEA_LEVEL = -64;

	/**
	 * Forme du bruit, identique a celle de l'Overworld.
	 *
	 * <p>Les quatre valeurs doivent s'accorder avec la hauteur declaree par le type de dimension
	 * dans {@link ModDimensions} : plancher a -64, trois cent quatre-vingt-quatre blocs de haut.
	 */
	private static final NoiseSettings SHAPE = NoiseSettings.create(-64, 384, 1, 2);

	private ModNoiseSettings() {
	}

	public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
		HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParameters = context.lookup(Registries.NOISE);

		// Les quatre booleens de fin, dans l'ordre : generation des creatures a la creation du
		// chunk, nappes souterraines, veines de minerai vanilla, et generateur aleatoire herite.
		// Ce dernier date de la 1.17 et ne sert qu'aux mondes d'alors : le routeur de bruit repris
		// ici attend le generateur moderne.
		context.register(DEMON_WASTES, new NoiseGeneratorSettings(
				SHAPE,
				Blocks.STONE.defaultBlockState(),
				Blocks.AIR.defaultBlockState(),
				NoiseRouterData.overworld(densityFunctions, noiseParameters, false, false),
				SurfaceRuleData.overworld(),
				List.of(),
				SEA_LEVEL,
				false,
				false,
				false,
				false));
	}
}
