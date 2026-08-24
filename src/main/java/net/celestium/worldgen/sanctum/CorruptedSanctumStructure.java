package net.celestium.worldgen.sanctum;

import com.mojang.serialization.Codec;
import net.celestium.init.ModStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

/**
 * Le sanctuaire corrompu : la salle qui abrite le cadre du portail.
 *
 * <p>Elle joue pour les terres corrompues le role que le donjon joue pour l'End. Sans elle, l'anneau
 * de douze cadres devait se batir de toutes pieces, ce qui otait au voyage sa part de decouverte :
 * on savait ou l'on allait avant meme d'y penser.
 *
 * <p>Elle se cache sous terre, assez profond pour ne pas s'apercevoir depuis la surface et assez
 * haut pour se croiser en creusant. La commande {@code /locate structure #celestium:structures} la
 * trouve, comme les autres structures du mod.
 */
public class CorruptedSanctumStructure extends Structure {

	public static final Codec<CorruptedSanctumStructure> CODEC =
			simpleCodec(CorruptedSanctumStructure::new);

	/** Profondeur sous la surface, en blocs. */
	private static final int DEPTH = 28;

	/** Bornes verticales, pour ne finir ni dans le vide ni sous le plancher du monde. */
	private static final int MIN_Y = -50;
	private static final int MAX_Y = 30;

	public CorruptedSanctumStructure(Structure.StructureSettings settings) {
		super(settings);
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG,
				builder -> addPieces(builder, context));
	}

	private static void addPieces(StructurePiecesBuilder builder, GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		int x = chunk.getMiddleBlockX();
		int z = chunk.getMiddleBlockZ();

		int surface = context.chunkGenerator().getFirstOccupiedHeight(
				x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

		int y = Math.max(MIN_Y, Math.min(MAX_Y, surface - DEPTH));

		builder.addPiece(new CorruptedSanctumPiece(new BlockPos(x, y, z), context.random().nextLong()));
	}

	@Override
	public StructureType<?> type() {
		return ModStructureTypes.CORRUPTED_SANCTUM.get();
	}
}
