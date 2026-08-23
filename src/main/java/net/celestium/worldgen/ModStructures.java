package net.celestium.worldgen;

import com.mojang.datafixers.util.Pair;
import net.celestium.CelestiumMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
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

	/** Un cimetiere en moyenne tous les 24 chunks, avec au moins 8 chunks entre deux. */
	private static final int SPACING = 24;
	private static final int SEPARATION = 8;

	/** Graine propre a la structure : deux structures de meme sel se superposeraient. */
	private static final int SALT = 874_310_291;

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
	}

	public static void bootstrapSet(BootstapContext<StructureSet> context) {
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

		context.register(CEMETERY_SET, new StructureSet(
				structures.getOrThrow(CEMETERY),
				new RandomSpreadStructurePlacement(SPACING, SEPARATION, RandomSpreadType.LINEAR, SALT)));
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
