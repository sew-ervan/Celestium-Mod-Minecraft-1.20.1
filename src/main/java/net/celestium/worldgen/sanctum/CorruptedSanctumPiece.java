package net.celestium.worldgen.sanctum;

import net.celestium.feature.portal.CorruptedPortalFrameBlock;
import net.celestium.feature.portal.CorruptedPortalShape;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * La salle du sanctuaire, batie bloc par bloc.
 *
 * <p>Une chambre de pierre noire enterree, l'anneau des douze cadres en son centre, et un puits de
 * lumiere pour qu'on ne s'y perde pas. La brique de pierre noire et l'obsidienne pleureuse la
 * rattachent visuellement au Celestium corrompu, dont le cadre est fait.
 *
 * <p>Comme le village demoniaque, tout passe par {@link #placeAbsolute}, qui decoupe l'ecriture
 * selon la boite du morceau en cours : c'est ce qui permet a la salle de s'etendre sur plusieurs
 * chunks sans jamais ecrire dans un chunk qui n'est pas pret.
 */
public class CorruptedSanctumPiece extends StructurePiece {

	/** Demi-cote de la salle, murs compris. */
	private static final int RADIUS = 7;

	/** Hauteur interieure, du sol au plafond. */
	private static final int HEIGHT = 7;

	/**
	 * Chances sur dix qu'un cadre soit deja garni.
	 *
	 * <p>Le donjon de l'End procede de meme. C'est ce qui fait qu'aucun anneau ne se ressemble : on
	 * en trouve avec deux yeux en place et d'autres avec aucun, et le prix du voyage varie d'autant.
	 */
	private static final int EYE_CHANCE = 1;

	private final long seed;

	public CorruptedSanctumPiece(BlockPos origin, long seed) {
		super(ModStructurePieces.CORRUPTED_SANCTUM.get(), 0, boundsAround(origin));
		this.seed = seed;
		this.setOrientation(null);
	}

	public CorruptedSanctumPiece(CompoundTag tag) {
		super(ModStructurePieces.CORRUPTED_SANCTUM.get(), tag);
		this.seed = tag.getLong("Seed");
	}

	private static BoundingBox boundsAround(BlockPos origin) {
		return new BoundingBox(
				origin.getX() - RADIUS, origin.getY() - 1, origin.getZ() - RADIUS,
				origin.getX() + RADIUS, origin.getY() + HEIGHT, origin.getZ() + RADIUS);
	}

	/**
	 * La graine est conservee.
	 *
	 * <p>Sans elle, les cadres deja garnis seraient retires au hasard a chaque pose, et une salle
	 * generee en deux fois — parce qu'elle chevauche deux chunks — n'aurait pas les memes yeux d'un
	 * cote et de l'autre.
	 */
	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putLong("Seed", this.seed);
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
			RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

		int centreX = this.boundingBox.getCenter().getX();
		int floor = this.boundingBox.minY() + 1;
		int centreZ = this.boundingBox.getCenter().getZ();

		shell(level, box, centreX, floor, centreZ);
		ring(level, box, centreX, floor, centreZ);
		braziers(level, box, centreX, floor, centreZ);
	}

	/** Sol, murs et plafond. */
	private void shell(WorldGenLevel level, BoundingBox box, int centreX, int floor, int centreZ) {
		BlockState wall = Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
		BlockState ground = Blocks.BLACKSTONE.defaultBlockState();
		BlockState air = Blocks.AIR.defaultBlockState();

		for (int dx = -RADIUS; dx <= RADIUS; dx++) {
			for (int dz = -RADIUS; dz <= RADIUS; dz++) {
				boolean onEdge = Math.abs(dx) == RADIUS || Math.abs(dz) == RADIUS;

				placeAbsolute(level, ground, centreX + dx, floor - 1, centreZ + dz, box);
				placeAbsolute(level, wall, centreX + dx, floor + HEIGHT - 1, centreZ + dz, box);

				for (int dy = 0; dy < HEIGHT - 1; dy++) {
					placeAbsolute(level, onEdge ? wall : air, centreX + dx, floor + dy, centreZ + dz, box);
				}
			}
		}
	}

	/**
	 * L'anneau des douze cadres, au centre.
	 *
	 * <p>Les ecarts sont ceux que la reconnaissance attend : la salle et le mecanisme d'allumage
	 * lisent le meme tableau, donc un anneau genere ici est forcement un anneau valide.
	 */
	private void ring(WorldGenLevel level, BoundingBox box, int centreX, int floor, int centreZ) {
		RandomSource random = RandomSource.create(this.seed);

		for (int[] offset : CorruptedPortalShape.ringOffsets()) {
			boolean filled = random.nextInt(10) < EYE_CHANCE;

			BlockState frame = ModBlocks.CORRUPTED_PORTAL_FRAME.get().defaultBlockState()
					.setValue(CorruptedPortalFrameBlock.HAS_EYE, filled);

			placeAbsolute(level, frame, centreX + offset[0], floor, centreZ + offset[1], box);
		}
	}

	/** Quatre feux aux angles : sans eux la salle serait noire et illisible. */
	private void braziers(WorldGenLevel level, BoundingBox box, int centreX, int floor, int centreZ) {
		BlockState pillar = Blocks.CRYING_OBSIDIAN.defaultBlockState();
		BlockState flame = Blocks.SOUL_FIRE.defaultBlockState();

		int reach = RADIUS - 2;
		int[][] corners = {{-reach, -reach}, {-reach, reach}, {reach, -reach}, {reach, reach}};

		for (int[] corner : corners) {
			placeAbsolute(level, pillar, centreX + corner[0], floor, centreZ + corner[1], box);
			placeAbsolute(level, flame, centreX + corner[0], floor + 1, centreZ + corner[1], box);
		}
	}

	/** Pose un bloc a des coordonnees absolues, en respectant le decoupage par chunk. */
	private void placeAbsolute(WorldGenLevel level, BlockState state, int x, int y, int z, BoundingBox box) {
		BlockPos pos = new BlockPos(x, y, z);
		if (box.isInside(pos)) {
			level.setBlock(pos, state, 2);
		}
	}
}
