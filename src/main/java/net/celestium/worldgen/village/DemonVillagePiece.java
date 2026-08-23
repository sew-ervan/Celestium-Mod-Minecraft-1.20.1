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
 * l'autel d'invocation, quatre huttes en bois du demon disposees autour, et leurs habitants.
 *
 * <p>Tout passe par {@link #placeBlock}, qui decoupe l'ecriture selon la boite du morceau en cours
 * de generation. C'est ce qui permet a la construction de s'etendre sur plusieurs chunks sans
 * jamais ecrire dans un chunk qui n'est pas pret — l'erreur exacte que commettait le cimetiere
 * quand il etait une simple feature.
 */
public class DemonVillagePiece extends StructurePiece {

	/** Demi-cote de la place centrale. */
	private static final int PLAZA_RADIUS = 4;

	/** Ecart entre le centre du village et celui de chaque hutte. */
	private static final int HUT_OFFSET = 9;

	private static final int HUT_RADIUS = 3;
	private static final int HUT_HEIGHT = 4;

	private static final int VILLAGERS_PER_HUT = 1;

	public DemonVillagePiece(BlockPos origin) {
		super(ModStructurePieces.DEMON_VILLAGE.get(), 0, boundsAround(origin));
		this.setOrientation(null);
	}

	public DemonVillagePiece(CompoundTag tag) {
		super(ModStructurePieces.DEMON_VILLAGE.get(), tag);
	}

	/** Boite englobante du village entier : elle deborde volontairement sur plusieurs chunks. */
	private static BoundingBox boundsAround(BlockPos origin) {
		int reach = HUT_OFFSET + HUT_RADIUS + 1;
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

		// Quatre huttes aux points cardinaux, autour de la place.
		hut(level, box, random, centreX + HUT_OFFSET, centreY, centreZ);
		hut(level, box, random, centreX - HUT_OFFSET, centreY, centreZ);
		hut(level, box, random, centreX, centreY, centreZ + HUT_OFFSET);
		hut(level, box, random, centreX, centreY, centreZ - HUT_OFFSET);
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

		BlockPos home = new BlockPos(centreX, centreY, centreZ);

		// Une hutte n'est peuplee que lorsque son centre tombe dans le chunk en cours de
		// generation. Sans ce test, ses habitants seraient crees autant de fois que la structure
		// touche de chunks.
		if (!box.isInside(home)) {
			return;
		}

		for (int i = 0; i < VILLAGERS_PER_HUT; i++) {
			CorruptedVillagerEntity villager = ModEntities.CORRUPTED_VILLAGER.get().create(level.getLevel());
			if (villager == null) {
				continue;
			}
			villager.moveTo(centreX + 0.5, centreY, centreZ + 0.5, random.nextFloat() * 360.0F, 0.0F);
			villager.finalizeSpawn(level, level.getCurrentDifficultyAt(home),
					MobSpawnType.STRUCTURE, null, null);
			level.addFreshEntity(villager);
		}
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
