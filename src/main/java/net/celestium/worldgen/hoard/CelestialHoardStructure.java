package net.celestium.worldgen.hoard;

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
 * Le tas du dragon : un monticule d'or, de Celestium et de matiere noire, et son gardien dessus.
 *
 * <p>Il se pose a ciel ouvert. Un tresor enterre serait une chasse ; celui-ci se voit de loin, ce
 * qui est le propre d'un tresor garde — son proprietaire n'a pas besoin de le cacher.
 */
public class CelestialHoardStructure extends Structure {

	public static final Codec<CelestialHoardStructure> CODEC = simpleCodec(CelestialHoardStructure::new);

	public CelestialHoardStructure(Structure.StructureSettings settings) {
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

		builder.addPiece(new CelestialHoardPiece(new BlockPos(x, surface, z), context.random().nextLong()));
	}

	@Override
	public StructureType<?> type() {
		return ModStructureTypes.CELESTIAL_HOARD.get();
	}
}
