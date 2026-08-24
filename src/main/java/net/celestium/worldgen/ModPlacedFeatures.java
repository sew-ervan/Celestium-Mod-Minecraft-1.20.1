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
import net.minecraft.world.level.levelgen.placement.RarityFilter;
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

	/** Le minerai des terres corrompues. */
	public static final ResourceKey<PlacedFeature> CORRUPTED_CELESTIUM_ORE = key("corrupted_celestium_ore");

	/** Arbres des terres du demon. */
	public static final ResourceKey<PlacedFeature> DEMON_TREE = key("demon_tree");

	/** Les trois blocs chance, chacun dans son monde. */
	public static final ResourceKey<PlacedFeature> LUCKY_BLOCK = key("lucky_block");
	public static final ResourceKey<PlacedFeature> CORRUPTED_LUCKY_BLOCK = key("corrupted_lucky_block");
	public static final ResourceKey<PlacedFeature> DEMON_LUCKY_BLOCK = key("demon_lucky_block");

	// Frequence des blocs chance, en chunks entre deux tentatives. Ils doivent rester une trouvaille
	// et non une nuisance : un bloc chance tous les quelques chunks, c'est une surprise ; un par
	// chunk, c'est une horde permanente. Le corrompu et celui du demon sont plus rares encore,
	// puisqu'ils frappent plus fort.
	private static final int LUCKY_RARITY = 6;
	private static final int CORRUPTED_LUCKY_RARITY = 10;
	private static final int DEMON_LUCKY_RARITY = 8;

	// Valeurs relevees a la demande du serveur. Le mod d'origine en donnait 2 filons de 3 blocs
	// entre -64 et -48, soit une bande de seize blocs au fond du monde : le Celestium y etait
	// pratiquement introuvable. Ces trois constantes se reglent independamment.
	private static final int VEINS_PER_CHUNK = 5;
	private static final int MIN_HEIGHT = -64;
	private static final int MAX_HEIGHT = -40;

	// Le Demonium se cherche exactement la ou se cherche le diamant : meme bande, meme repartition
	// en triangle qui concentre les filons vers le bas. C'est le seul minerai de la dimension.
	private static final int WASTES_VEINS_PER_CHUNK = 7;

	/** Le Celestium corrompu est abondant : c est une etape, pas une fin. */
	private static final int CORRUPTED_VEINS_PER_CHUNK = 9;
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

		// Les blocs chance couvrent une large tranche d'altitude : on doit pouvoir en croiser en
		// creusant une cave comme en descendant au fond d'une mine.
		context.register(LUCKY_BLOCK, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.LUCKY_BLOCK),
				List.of(
						RarityFilter.onAverageOnceEvery(LUCKY_RARITY),
						InSquarePlacement.spread(),
						HeightRangePlacement.uniform(
								VerticalAnchor.absolute(-48),
								VerticalAnchor.absolute(80)),
						BiomeFilter.biome())));

		context.register(CORRUPTED_LUCKY_BLOCK, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.CORRUPTED_LUCKY_BLOCK),
				List.of(
						RarityFilter.onAverageOnceEvery(CORRUPTED_LUCKY_RARITY),
						InSquarePlacement.spread(),
						HeightRangePlacement.uniform(
								VerticalAnchor.absolute(8),
								VerticalAnchor.absolute(110)),
						BiomeFilter.biome())));

		context.register(DEMON_LUCKY_BLOCK, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.DEMON_LUCKY_BLOCK),
				List.of(
						RarityFilter.onAverageOnceEvery(DEMON_LUCKY_RARITY),
						InSquarePlacement.spread(),
						HeightRangePlacement.uniform(
								VerticalAnchor.absolute(-48),
								VerticalAnchor.absolute(90)),
						BiomeFilter.biome())));

		// Le Celestium corrompu se cherche a mi-profondeur : assez bas pour qu'il faille creuser,
		// assez haut pour ne pas obliger a descendre au fond d'un monde qui vous ronge.
		context.register(CORRUPTED_CELESTIUM_ORE, new PlacedFeature(
				configured.getOrThrow(ModConfiguredFeatures.CORRUPTED_CELESTIUM_ORE),
				List.of(
						CountPlacement.of(CORRUPTED_VEINS_PER_CHUNK),
						InSquarePlacement.spread(),
						HeightRangePlacement.triangle(
								VerticalAnchor.absolute(-40),
								VerticalAnchor.absolute(48)),
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
