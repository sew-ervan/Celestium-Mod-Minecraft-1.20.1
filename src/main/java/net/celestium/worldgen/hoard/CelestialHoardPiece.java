package net.celestium.worldgen.hoard;

import net.celestium.feature.mob.CelestialDragonEntity;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEntities;
import net.celestium.init.ModStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
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
 * Le monticule lui-meme, bati bloc par bloc.
 *
 * <p>Un dome dont le rayon se resserre en montant, et dont chaque bloc est tire entre l'or, le
 * Celestium et la matiere noire. Le melange n'est pas decoratif : c'est ce qui fait qu'on ignore ce
 * qu'on va deterrer, et qu'on creuse le tas entier plutot que d'en prendre le dessus.
 *
 * <p>Les proportions sont volontairement inegales. L'or domine — c'est le metal des tresors — la
 * matiere noire reste rare, et le Celestium tient le milieu. Un tas equilibre se lirait comme une
 * distribution ; celui-ci se lit comme un butin.
 */
public class CelestialHoardPiece extends StructurePiece {

	/** Rayon du tas a sa base. */
	private static final int RADIUS = 7;

	/** Hauteur du dome. */
	private static final int HEIGHT = 5;

	/** Hauteur a laquelle le gardien se tient au-dessus du sommet. */
	private static final int PERCH = 6;

	// Parts de chaque matiere, sur cent.
	private static final int GOLD_SHARE = 60;
	private static final int CELESTIUM_SHARE = 30;

	private final long seed;

	public CelestialHoardPiece(BlockPos origin, long seed) {
		super(ModStructurePieces.CELESTIAL_HOARD.get(), 0, boundsAround(origin));
		this.seed = seed;
		this.setOrientation(null);
	}

	public CelestialHoardPiece(CompoundTag tag) {
		super(ModStructurePieces.CELESTIAL_HOARD.get(), tag);
		this.seed = tag.getLong("Seed");
	}

	private static BoundingBox boundsAround(BlockPos origin) {
		return new BoundingBox(
				origin.getX() - RADIUS, origin.getY() - 1, origin.getZ() - RADIUS,
				origin.getX() + RADIUS, origin.getY() + HEIGHT + PERCH, origin.getZ() + RADIUS);
	}

	/**
	 * La graine est conservee.
	 *
	 * <p>Sans elle, un tas genere en deux fois — parce qu'il chevauche deux chunks — n'aurait pas
	 * le meme melange d'un cote et de l'autre, et la couture se verrait.
	 */
	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putLong("Seed", this.seed);
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
			RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

		int centreX = this.boundingBox.getCenter().getX();
		int base = this.boundingBox.minY() + 1;
		int centreZ = this.boundingBox.getCenter().getZ();

		this.mound(level, box, centreX, base, centreZ);
		this.guardian(level, box, random, centreX, base, centreZ);
	}

	/** Le dome, couche par couche, chaque bloc tire au sort. */
	private void mound(WorldGenLevel level, BoundingBox box, int centreX, int base, int centreZ) {
		RandomSource random = RandomSource.create(this.seed);

		for (int dy = 0; dy < HEIGHT; dy++) {
			// Le rayon se resserre en montant : un cylindre serait un batiment, pas un tas.
			int reach = RADIUS - (dy * RADIUS) / HEIGHT;

			for (int dx = -reach; dx <= reach; dx++) {
				for (int dz = -reach; dz <= reach; dz++) {
					if (dx * dx + dz * dz > reach * reach) {
						continue;
					}
					placeAbsolute(level, treasure(random), centreX + dx, base + dy, centreZ + dz, box);
				}
			}
		}
	}

	/** Un bloc de tresor, tire selon les parts declarees. */
	private static BlockState treasure(RandomSource random) {
		int roll = random.nextInt(100);

		if (roll < GOLD_SHARE) {
			return Blocks.GOLD_BLOCK.defaultBlockState();
		}
		if (roll < GOLD_SHARE + CELESTIUM_SHARE) {
			return ModBlocks.CELESTIUM_BLOCK.get().defaultBlockState();
		}
		return ModBlocks.DARK_MATTER_BLOCK.get().defaultBlockState();
	}

	/**
	 * Le gardien, pose au-dessus du sommet.
	 *
	 * <p>Il n'est cree que si son perchoir appartient au chunk en cours : sans ce test, un tas qui
	 * chevauche quatre chunks aurait quatre dragons.
	 */
	private void guardian(WorldGenLevel level, BoundingBox box, RandomSource random,
			int centreX, int base, int centreZ) {

		BlockPos perch = new BlockPos(centreX, base + HEIGHT + PERCH - 1, centreZ);
		if (!box.isInside(perch)) {
			return;
		}

		CelestialDragonEntity dragon = ModEntities.CELESTIAL_DRAGON.get().create(level.getLevel());
		if (dragon == null) {
			return;
		}

		dragon.moveTo(perch.getX() + 0.5, perch.getY(), perch.getZ() + 0.5,
				random.nextFloat() * 360.0F, 0.0F);
		dragon.finalizeSpawn(level, level.getCurrentDifficultyAt(perch), MobSpawnType.STRUCTURE, null, null);

		// Il ne doit pas s'evaporer : un tas sans gardien est un cadeau, pas un defi.
		dragon.setPersistenceRequired();

		level.addFreshEntity(dragon);
	}

	private void placeAbsolute(WorldGenLevel level, BlockState state, int x, int y, int z, BoundingBox box) {
		BlockPos pos = new BlockPos(x, y, z);
		if (box.isInside(pos)) {
			level.setBlock(pos, state, 2);
		}
	}
}
