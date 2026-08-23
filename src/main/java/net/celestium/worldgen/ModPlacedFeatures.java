package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

/**
 * Placement des filons.
 *
 * <p>Le Celestium reste un minerai de fin de progression : deux filons par section de monde, entre
 * les altitudes -64 et -48, soit la couche la plus profonde. Ce sont les valeurs du mod d'origine.
 */
public final class ModPlacedFeatures {

	public static final ResourceKey<PlacedFeature> CELESTIUM_ORE = key("celestium_ore");

	/** Placement propre a la dimension celeste, sans commune mesure avec celui de la surface. */
	public static final ResourceKey<PlacedFeature> DEMONIUM_ORE_WASTES = key("demonium_ore_wastes");

	/** Arbres des terres du demon. */
	public static final ResourceKey<PlacedFeature> DEMON_TREE = key("demon_tree");

	// Valeurs relevees a la demande du serveur. Le mod d'origine en donnait 2 filons de 3 blocs
	// entre -64 et -48, soit une bande de seize blocs au fond du monde : le Celestium y etait
	// pratiquement introuvable. Ces trois constantes se reglent independamment.
	private static final int VEINS_PER_CHUNK = 5;
	private static final int MIN_HEIGHT = -64;
	private static final int MAX_HEIGHT = -40;

	// Le Demonium se cherche exactement la ou se cherche le diamant : meme bande, meme repartition
	// en triangle qui concentre les filons vers le bas. C'est le seul minerai de la dimension.
	private static final int WASTES_VEINS_PER_CHUNK = 7;
	private static final int WASTES_MIN_HEIGHT = -64;
	private static final int WASTES_MAX_HEIGHT = 16;

	private ModPlacedFeatures() {
	}

	public static void bootstrap(BootstapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

		context.register(CELESTIUM_ORE, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.CELESTIUM_ORE),
				List.of(
						CountPlacement.of(VEINS_PER_CHUNK),
						InSquarePlacement.spread(),
						HeightRangePlacement.uniform(
								VerticalAnchor.absolute(MIN_HEIGHT),
								VerticalAnchor.absolute(MAX_HEIGHT)),
						BiomeFilter.biome())));

		context.register(DEMON_TREE, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.DEMON_TREE),
				List.of(
						CountPlacement.of(3),
						InSquarePlacement.spread(),
						HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING),
						BiomeFilter.biome())));

		context.register(DEMONIUM_ORE_WASTES, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.DEMONIUM_ORE),
				List.of(
						CountPlacement.of(WASTES_VEINS_PER_CHUNK),
						InSquarePlacement.spread(),
						HeightRangePlacement.triangle(
								VerticalAnchor.absolute(WASTES_MIN_HEIGHT),
								VerticalAnchor.absolute(WASTES_MAX_HEIGHT)),
						BiomeFilter.biome())));
	}

	private static ResourceKey<PlacedFeature> key(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, CelestiumMod.id(name));
	}
}
