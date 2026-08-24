package net.celestium.worldgen;

import com.mojang.datafixers.util.Pair;
import net.celestium.CelestiumMod;
import net.celestium.worldgen.hoard.CelestialHoardStructure;
import net.celestium.worldgen.sanctum.CorruptedSanctumStructure;
import net.celestium.worldgen.village.DemonVillageStructure;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Le cimetiere, en tant que structure du monde.
 *
 * <p>Le mod d'origine le posait depuis une {@code Feature}, et je l'avais porte tel quel. C'etait
 * le mauvais outil : une feature n'a le droit d'ecrire que dans le chunk qu'elle genere, plus une
 * marge de huit blocs. Le cimetiere mesure 17 sur 32 blocs, soit plus de deux chunks de long, et
 * debordait donc systematiquement — le jeu le signalait par une avalanche d'erreurs
 * {@code Detected setBlock in a far chunk}, avec le risque de generation en cascade que cela
 * comporte.
 *
 * <p>Une structure, elle, sait s'etendre sur plusieurs chunks : le jeu decoupe la pose et differe
 * chaque morceau jusqu'a ce que son chunk soit pret. Aucune classe Java n'est necessaire, le type
 * jigsaw vanilla suffit pour une piece unique.
 */
public final class ModStructures {

	public static final ResourceKey<StructureTemplatePool> CEMETERY_POOL = poolKey("cemetery/start");
	public static final ResourceKey<Structure> CEMETERY = structureKey("cemetery");
	public static final ResourceKey<StructureSet> CEMETERY_SET = setKey("cemetery");

	public static final ResourceKey<Structure> DEMON_VILLAGE = structureKey("demon_village");
	public static final ResourceKey<StructureSet> DEMON_VILLAGE_SET = setKey("demon_village");

	/** Le sanctuaire qui abrite le cadre du portail corrompu. */
	public static final ResourceKey<Structure> CORRUPTED_SANCTUM = structureKey("corrupted_sanctum");
	public static final ResourceKey<StructureSet> CORRUPTED_SANCTUM_SET = setKey("corrupted_sanctum");

	/** Le tas du dragon celeste. */
	public static final ResourceKey<Structure> CELESTIAL_HOARD = structureKey("celestial_hoard");
	public static final ResourceKey<StructureSet> CELESTIAL_HOARD_SET = setKey("celestial_hoard");

	/** Un cimetiere en moyenne tous les 24 chunks, avec au moins 8 chunks entre deux. */
	private static final int SPACING = 24;
	private static final int SEPARATION = 8;

	/** Graine propre a la structure : deux structures de meme sel se superposeraient. */
	private static final int SALT = 874_310_291;

	/** Les villages demoniaques sont plus rapproches que les cimetieres, sans etre communs. */
	private static final int VILLAGE_SPACING = 20;
	private static final int VILLAGE_SEPARATION = 7;
	private static final int VILLAGE_SALT = 615_003_477;

	// Le sanctuaire est la structure la plus rare du mod : il n'en faut qu'un pour ouvrir la voie,
	// et en trouver un second n'apporte rien qu'on n'ait deja.
	private static final int SANCTUM_SPACING = 40;
	private static final int SANCTUM_SEPARATION = 14;
	private static final int SANCTUM_SALT = 402_118_663;

	// Le tas est rare, mais moins que le sanctuaire : on peut vouloir en piller plusieurs, la ou un
	// seul sanctuaire suffit pour toute une partie.
	private static final int HOARD_SPACING = 32;
	private static final int HOARD_SEPARATION = 12;
	private static final int HOARD_SALT = 771_204_558;

	private ModStructures() {
	}

	public static void bootstrapPool(BootstapContext<StructureTemplatePool> context) {
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
		Holder<StructureTemplatePool> empty = pools.getOrThrow(Pools.EMPTY);

		context.register(CEMETERY_POOL, new StructureTemplatePool(
				empty,
				List.<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>of(
						Pair.of(StructurePoolElement.single(CelestiumMod.MOD_ID + ":vraicimetiere11"), 1)),
				StructureTemplatePool.Projection.RIGID));
	}

	public static void bootstrapStructure(BootstapContext<Structure> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

		context.register(CEMETERY, new JigsawStructure(
				new Structure.StructureSettings(
						biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
						Map.of(),
						GenerationStep.Decoration.SURFACE_STRUCTURES,
						// Le terrain se moule autour du cimetiere plutot que de le laisser
						// flotter ou l'enterrer sur un relief accidente.
						TerrainAdjustment.BEARD_THIN),
				pools.getOrThrow(CEMETERY_POOL),
				Optional.empty(),
				1,
				ConstantHeight.of(VerticalAnchor.absolute(0)),
				false,
				Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
				80));

		// Le village est bati par code : sa structure ne porte que ses conditions d'apparition.
		context.register(DEMON_VILLAGE, new DemonVillageStructure(
				new Structure.StructureSettings(
						HolderSet.direct(biomes.getOrThrow(ModBiomes.DEMON_WASTES)),
						Map.of(),
						GenerationStep.Decoration.SURFACE_STRUCTURES,
						TerrainAdjustment.BEARD_THIN)));

		// Le sanctuaire est enterre : aucun ajustement du terrain, il se creuse sa place.
		context.register(CORRUPTED_SANCTUM, new CorruptedSanctumStructure(
				new Structure.StructureSettings(
						biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
						Map.of(),
						GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
						TerrainAdjustment.NONE)));

		// Le tas se pose a ciel ouvert, et le terrain se moule autour : un monticule a moitie enterre
		// sur un relief accidente ne ressemblerait pas a un tresor.
		//
		// Il n'appartient qu'aux terres corrompues. Un tas d'or garde par un dragon pose dans une
		// plaine de l'Overworld serait une curiosite ; pose dans la dimension ou l'on vient deja
		// chercher le Celestium corrompu, il devient une raison de plus d'y rester, et le danger
		// qu'il represente s'ajoute a celui que la dimension fait deja peser.
		context.register(CELESTIAL_HOARD, new CelestialHoardStructure(
				new Structure.StructureSettings(
						HolderSet.direct(biomes.getOrThrow(ModBiomes.CORRUPTED_LANDS)),
						Map.of(),
						GenerationStep.Decoration.SURFACE_STRUCTURES,
						TerrainAdjustment.BEARD_THIN)));
	}

	public static void bootstrapSet(BootstapContext<StructureSet> context) {
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

		context.register(CEMETERY_SET, new StructureSet(
				structures.getOrThrow(CEMETERY),
				new RandomSpreadStructurePlacement(SPACING, SEPARATION, RandomSpreadType.LINEAR, SALT)));

		context.register(DEMON_VILLAGE_SET, new StructureSet(
				structures.getOrThrow(DEMON_VILLAGE),
				new RandomSpreadStructurePlacement(VILLAGE_SPACING, VILLAGE_SEPARATION,
						RandomSpreadType.LINEAR, VILLAGE_SALT)));

		context.register(CORRUPTED_SANCTUM_SET, new StructureSet(
				structures.getOrThrow(CORRUPTED_SANCTUM),
				new RandomSpreadStructurePlacement(SANCTUM_SPACING, SANCTUM_SEPARATION,
						RandomSpreadType.LINEAR, SANCTUM_SALT)));

		context.register(CELESTIAL_HOARD_SET, new StructureSet(
				structures.getOrThrow(CELESTIAL_HOARD),
				new RandomSpreadStructurePlacement(HOARD_SPACING, HOARD_SEPARATION,
						RandomSpreadType.LINEAR, HOARD_SALT)));
	}

	private static ResourceKey<StructureTemplatePool> poolKey(String name) {
		return ResourceKey.create(Registries.TEMPLATE_POOL, CelestiumMod.id(name));
	}

	private static ResourceKey<Structure> structureKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE, CelestiumMod.id(name));
	}

	private static ResourceKey<StructureSet> setKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE_SET, CelestiumMod.id(name));
	}

}
