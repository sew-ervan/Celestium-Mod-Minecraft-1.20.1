package net.celestium.worldgen.village;

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

import net.celestium.feature.mob.CorruptedVillagerEntity;

/**
 * Un village demoniaque, bati bloc par bloc.
 *
 * <p>Faute de fichiers de batiment, le village est construit par code : une place centrale portant
 * l'autel d'invocation, huit huttes en bois du demon reparties sur deux couronnes, et leurs
 * habitants — deux par hutte, plus quelques-uns qui trainent sur la place.
 *
 * <p>Tout passe par {@link #placeBlock}, qui decoupe l'ecriture selon la boite du morceau en cours
 * de generation. C'est ce qui permet a la construction de s'etendre sur plusieurs chunks sans
 * jamais ecrire dans un chunk qui n'est pas pret — l'erreur exacte que commettait le cimetiere
 * quand il etait une simple feature.
 */
public class DemonVillagePiece extends StructurePiece {

	/** Demi-cote de la place centrale. */
	private static final int PLAZA_RADIUS = 7;

	/** Ecart entre le centre du village et la premiere couronne de huttes. */
	private static final int INNER_RING = 13;

	/** Ecart de la seconde couronne, posee en diagonale pour ne pas aligner les facades. */
	private static final int OUTER_RING = 20;

	private static final int HUT_RADIUS = 4;
	private static final int HUT_HEIGHT = 5;

	/** Habitants par hutte. Un village desert n'est pas un village. */
	private static final int VILLAGERS_PER_HUT = 2;

	/** Habitants qui trainent sur la place, en plus de ceux des huttes. */
	private static final int PLAZA_DWELLERS = 4;

	public DemonVillagePiece(BlockPos origin) {
		super(ModStructurePieces.DEMON_VILLAGE.get(), 0, boundsAround(origin));
		this.setOrientation(null);
	}

	public DemonVillagePiece(CompoundTag tag) {
		super(ModStructurePieces.DEMON_VILLAGE.get(), tag);
	}

	/** Boite englobante du village entier : elle deborde volontairement sur plusieurs chunks. */
	private static BoundingBox boundsAround(BlockPos origin) {
		int reach = OUTER_RING + HUT_RADIUS + 1;
		return new BoundingBox(
				origin.getX() - reach, origin.getY() - 2, origin.getZ() - reach,
				origin.getX() + reach, origin.getY() + HUT_HEIGHT + 2, origin.getZ() + reach);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		// La forme du village se deduit entierement de sa boite : rien a sauvegarder en plus.
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
			RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

		int centreX = this.boundingBox.getCenter().getX();
		int centreY = this.boundingBox.minY() + 2;
		int centreZ = this.boundingBox.getCenter().getZ();

		plaza(level, box, centreX, centreY, centreZ);
		plazaDwellers(level, box, random, centreX, centreY, centreZ);

		// Quatre huttes aux points cardinaux, quatre autres en diagonale et plus loin. La seconde
		// couronne est decalee pour qu'aucune facade n'en regarde une autre de face : un village
		// dont les maisons s'alignent se lit comme un lotissement, pas comme une ruine habitee.
		hut(level, box, random, centreX + INNER_RING, centreY, centreZ);
		hut(level, box, random, centreX - INNER_RING, centreY, centreZ);
		hut(level, box, random, centreX, centreY, centreZ + INNER_RING);
		hut(level, box, random, centreX, centreY, centreZ - INNER_RING);

		int diagonal = (int) Math.round(OUTER_RING / Math.sqrt(2.0));
		hut(level, box, random, centreX + diagonal, centreY, centreZ + diagonal);
		hut(level, box, random, centreX + diagonal, centreY, centreZ - diagonal);
		hut(level, box, random, centreX - diagonal, centreY, centreZ + diagonal);
		hut(level, box, random, centreX - diagonal, centreY, centreZ - diagonal);
	}

	/** Place dallee portant l'autel en son centre. */
	private void plaza(WorldGenLevel level, BoundingBox box, int centreX, int centreY, int centreZ) {
		BlockState floor = ModBlocks.BOIS_DU_DEMON.planks.get().defaultBlockState();

		for (int dx = -PLAZA_RADIUS; dx <= PLAZA_RADIUS; dx++) {
			for (int dz = -PLAZA_RADIUS; dz <= PLAZA_RADIUS; dz++) {
				placeAbsolute(level, floor, centreX + dx, centreY - 1, centreZ + dz, box);
				placeAbsolute(level, Blocks.AIR.defaultBlockState(), centreX + dx, centreY, centreZ + dz, box);
			}
		}

		placeAbsolute(level, ModBlocks.SUMMONING_ALTAR.get().defaultBlockState(),
				centreX, centreY, centreZ, box);
	}

	/**
	 * Les habitants qui trainent sur la place, en plus de ceux des huttes.
	 *
	 * <p>Un village ou l'on ne croise personne dehors se traverse sans rien remarquer. Ceux-ci sont
	 * les premiers qu'on voit, et donc les premiers a qui l'on peut parler.
	 */
	private void plazaDwellers(WorldGenLevel level, BoundingBox box, RandomSource random,
			int centreX, int centreY, int centreZ) {

		for (int i = 0; i < PLAZA_DWELLERS; i++) {
			int x = centreX + random.nextInt(PLAZA_RADIUS * 2 - 1) - (PLAZA_RADIUS - 1);
			int z = centreZ + random.nextInt(PLAZA_RADIUS * 2 - 1) - (PLAZA_RADIUS - 1);

			spawnVillager(level, box, random, x, centreY, z);
		}
	}

	/** Hutte carree en planches, montants en rondins, ouverte sur la place. */
	private void hut(WorldGenLevel level, BoundingBox box, RandomSource random,
			int centreX, int centreY, int centreZ) {

		BlockState planks = ModBlocks.BOIS_DU_DEMON.planks.get().defaultBlockState();
		BlockState log = ModBlocks.BOIS_DU_DEMON.log.get().defaultBlockState();
		BlockState air = Blocks.AIR.defaultBlockState();

		for (int dx = -HUT_RADIUS; dx <= HUT_RADIUS; dx++) {
			for (int dz = -HUT_RADIUS; dz <= HUT_RADIUS; dz++) {
				boolean onEdge = Math.abs(dx) == HUT_RADIUS || Math.abs(dz) == HUT_RADIUS;
				boolean onCorner = Math.abs(dx) == HUT_RADIUS && Math.abs(dz) == HUT_RADIUS;

				placeAbsolute(level, planks, centreX + dx, centreY - 1, centreZ + dz, box);
				placeAbsolute(level, planks, centreX + dx, centreY + HUT_HEIGHT, centreZ + dz, box);

				for (int dy = 0; dy < HUT_HEIGHT; dy++) {
					BlockState wall = onCorner ? log : planks;
					placeAbsolute(level, onEdge ? wall : air, centreX + dx, centreY + dy, centreZ + dz, box);
				}
			}
		}

		// Une porte percee dans le mur nord de chaque hutte.
		placeAbsolute(level, air, centreX, centreY, centreZ - HUT_RADIUS, box);
		placeAbsolute(level, air, centreX, centreY + 1, centreZ - HUT_RADIUS, box);

		spawnInhabitants(level, box, random, centreX, centreY, centreZ);
	}

	/** Peuple la hutte, si son centre appartient au chunk en cours de generation. */
	private void spawnInhabitants(WorldGenLevel level, BoundingBox box, RandomSource random,
			int centreX, int centreY, int centreZ) {

		for (int i = 0; i < VILLAGERS_PER_HUT; i++) {
			spawnVillager(level, box, random, centreX, centreY, centreZ);
		}
	}

	/**
	 * Pose un habitant, si l'endroit appartient au chunk en cours de generation.
	 *
	 * <p>Sans ce test, chaque habitant serait cree autant de fois que la structure touche de chunks —
	 * et un village qui en couvre quatre en compterait quatre fois trop.
	 */
	private void spawnVillager(WorldGenLevel level, BoundingBox box, RandomSource random,
			int x, int y, int z) {

		BlockPos home = new BlockPos(x, y, z);
		if (!box.isInside(home)) {
			return;
		}

		CorruptedVillagerEntity villager = ModEntities.CORRUPTED_VILLAGER.get().create(level.getLevel());
		if (villager == null) {
			return;
		}

		villager.moveTo(x + 0.5, y, z + 0.5, random.nextFloat() * 360.0F, 0.0F);
		villager.finalizeSpawn(level, level.getCurrentDifficultyAt(home),
				MobSpawnType.STRUCTURE, null, null);

		// Sans cela, ils disparaissent des que le joueur s'eloigne. Un village dont les habitants
		// s'evaporent entre deux visites n'est plus un village, et leur boutique n'aurait aucun sens :
		// on revient rarement dans la minute.
		villager.setPersistenceRequired();

		level.addFreshEntity(villager);
	}

	/**
	 * Pose un bloc a des coordonnees absolues.
	 *
	 * <p>{@link #placeBlock} attend des coordonnees relatives a l'orientation du morceau ; le
	 * village etant bati sans orientation, on passe par la boite directement, ce qui conserve le
	 * decoupage par chunk.
	 */
	private void placeAbsolute(WorldGenLevel level, BlockState state, int x, int y, int z, BoundingBox box) {
		BlockPos pos = new BlockPos(x, y, z);
		if (box.isInside(pos)) {
			level.setBlock(pos, state, 2);
		}
	}
}
