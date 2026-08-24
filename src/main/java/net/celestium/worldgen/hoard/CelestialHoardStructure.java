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
 *
 * <p>Il n'existe que dans les terres corrompues, la dimension ou l'on vient deja chercher de quoi
 * ouvrir la porte des Terres du demon. C'est ce qui lui donne son sens : on ne part pas en
 * expedition pour le tas, on tombe dessus en cherchant autre chose.
 */
public class CelestialHoardStructure extends Structure {

	public static final Codec<CelestialHoardStructure> CODEC = simpleCodec(CelestialHoardStructure::new);

	public CelestialHoardStructure(Structure.StructureSettings settings) {
		super(settings);
	}

	/**
	 * Le tas ne se pose pas sur l'eau.
	 *
	 * <p>Les terres corrompues ont des mers, la ou les Terres du demon n'en ont pas. Sans ce test,
	 * la carte des sommets renverrait la surface de l'eau et le monticule flotterait dessus, le
	 * dragon perche au-dessus des vagues. Un emplacement noye est donc simplement refuse : la
	 * structure n'apparait pas dans cette region, ce qui la rend un peu plus rare et un peu plus
	 * terrienne.
	 *
	 * <p>La comparaison se fait entre deux cartes de sommets : celle qui compte l'eau et celle qui
	 * ne la compte pas. Elles ne different que la ou il y a de l'eau.
	 */
	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		int x = chunk.getMiddleBlockX();
		int z = chunk.getMiddleBlockZ();

		if (height(context, x, z, Heightmap.Types.WORLD_SURFACE_WG)
				!= height(context, x, z, Heightmap.Types.OCEAN_FLOOR_WG)) {
			return Optional.empty();
		}

		return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG,
				builder -> addPieces(builder, context));
	}

	private static void addPieces(StructurePiecesBuilder builder, GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		int x = chunk.getMiddleBlockX();
		int z = chunk.getMiddleBlockZ();

		int surface = height(context, x, z, Heightmap.Types.WORLD_SURFACE_WG);

		builder.addPiece(new CelestialHoardPiece(new BlockPos(x, surface, z), context.random().nextLong()));
	}

	private static int height(GenerationContext context, int x, int z, Heightmap.Types type) {
		return context.chunkGenerator().getFirstOccupiedHeight(
				x, z, type, context.heightAccessor(), context.randomState());
	}

	@Override
	public StructureType<?> type() {
		return ModStructureTypes.CELESTIAL_HOARD.get();
	}
}
