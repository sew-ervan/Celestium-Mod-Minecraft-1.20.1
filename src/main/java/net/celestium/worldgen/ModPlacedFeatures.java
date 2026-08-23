package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
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
	public static final ResourceKey<PlacedFeature> CELESTIUM_ORE_CELESTIAL = key("celestium_ore_celestial");

	/** Filons de Demonium, repartis sur toute la hauteur du Nether. */
	public static final ResourceKey<PlacedFeature> DEMONIUM_ORE = key("demonium_ore");

	// Valeurs relevees a la demande du serveur. Le mod d'origine en donnait 2 filons de 3 blocs
	// entre -64 et -48, soit une bande de seize blocs au fond du monde : le Celestium y etait
	// pratiquement introuvable. Ces trois constantes se reglent independamment.
	private static final int VEINS_PER_CHUNK = 5;
	private static final int MIN_HEIGHT = -64;
	private static final int MAX_HEIGHT = -40;

	// Dans la dimension celeste le Celestium est la regle et non l'exception : filons bien plus
	// nombreux, repartis sur toute la hauteur jouable. C'est ce qui justifie le voyage.
	private static final int CELESTIAL_VEINS_PER_CHUNK = 14;
	private static final int CELESTIAL_MIN_HEIGHT = 0;
	private static final int CELESTIAL_MAX_HEIGHT = 128;

	private static final int DEMONIUM_VEINS_PER_CHUNK = 8;
	private static final int DEMONIUM_MIN_HEIGHT = 8;
	private static final int DEMONIUM_MAX_HEIGHT = 120;

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

		context.register(DEMONIUM_ORE, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.DEMONIUM_ORE),
				List.of(
						CountPlacement.of(DEMONIUM_VEINS_PER_CHUNK),
						InSquarePlacement.spread(),
						HeightRangePlacement.uniform(
								VerticalAnchor.absolute(DEMONIUM_MIN_HEIGHT),
								VerticalAnchor.absolute(DEMONIUM_MAX_HEIGHT)),
						BiomeFilter.biome())));

		context.register(CELESTIUM_ORE_CELESTIAL, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.CELESTIUM_ORE),
				List.of(
						CountPlacement.of(CELESTIAL_VEINS_PER_CHUNK),
						InSquarePlacement.spread(),
						HeightRangePlacement.uniform(
								VerticalAnchor.absolute(CELESTIAL_MIN_HEIGHT),
								VerticalAnchor.absolute(CELESTIAL_MAX_HEIGHT)),
						BiomeFilter.biome())));
	}

	private static ResourceKey<PlacedFeature> key(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, CelestiumMod.id(name));
	}
}
