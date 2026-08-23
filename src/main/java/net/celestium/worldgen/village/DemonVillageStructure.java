package net.celestium.worldgen.village;

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
 * Le village demoniaque, en tant que structure du monde.
 *
 * <p>C'est une structure et non une feature, parce qu'il s'etend sur plusieurs chunks : seul le
 * systeme de structures sait differer la pose de chaque morceau jusqu'a ce que son chunk soit
 * pret.
 */
public class DemonVillageStructure extends Structure {

	public static final Codec<DemonVillageStructure> CODEC = simpleCodec(DemonVillageStructure::new);

	public DemonVillageStructure(Structure.StructureSettings settings) {
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
		int y = context.chunkGenerator().getFirstOccupiedHeight(
				x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

		builder.addPiece(new DemonVillagePiece(new BlockPos(x, y, z)));
	}

	@Override
	public StructureType<?> type() {
		return ModStructureTypes.DEMON_VILLAGE.get();
	}
}
