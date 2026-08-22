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
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

/**
 * Placement des filons.
 *
 * <p>Le Celestium reste un minerai de fin de progression : deux filons par section de monde, entre
 * les altitudes -64 et -48, soit la couche la plus profonde. Ce sont les valeurs du mod d'origine.
 */
public final class ModPlacedFeatures {

	public static final ResourceKey<PlacedFeature> CELESTIUM_ORE = key("celestium_ore");
	public static final ResourceKey<PlacedFeature> CEMETERY = key("cemetery");

	private static final int VEINS_PER_CHUNK = 2;
	private static final int MIN_HEIGHT = -64;
	private static final int MAX_HEIGHT = -48;

	/** Un cimetiere en moyenne tous les 667 chunks. */
	private static final int CEMETERY_RARITY = 667;

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

		// Le mod d'origine tirait la rarete a la main dans le code de la feature :
		// random.nextInt(1000000) + 1 <= 1500, soit environ un chunk sur 667.
		context.register(CEMETERY, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.CEMETERY),
				List.of(
						RarityFilter.onAverageOnceEvery(CEMETERY_RARITY),
						InSquarePlacement.spread(),
						HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
						BiomeFilter.biome())));
	}

	private static ResourceKey<PlacedFeature> key(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, CelestiumMod.id(name));
	}
}
