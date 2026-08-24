package net.celestium.feature.portal;

import net.celestium.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Reconnaissance d'un anneau de cadres corrompus.
 *
 * <p>Le motif est celui du portail de l'End : un carre de cinq sur cinq dont on retire les quatre
 * coins et le coeur de trois sur trois. Restent douze emplacements, tous a garnir d'un oeil.
 *
 * <p>La verification part du bloc qu'on vient de garnir et essaie tous les centres possibles autour
 * de lui. C'est plus simple qu'un motif oriente, et plus indulgent : l'anneau s'allume quel que
 * soit le sens dans lequel on l'a bati, ce qui evite l'echec incomprehensible d'un cadre pose a
 * l'envers.
 */
public final class CorruptedPortalShape {

	/**
	 * Les douze emplacements de l'anneau, en ecart au centre.
	 *
	 * <p>Trois par cote, coins exclus.
	 */
	private static final int[][] RING = {
			{-2, -1}, {-2, 0}, {-2, 1},
			{2, -1}, {2, 0}, {2, 1},
			{-1, -2}, {0, -2}, {1, -2},
			{-1, 2}, {0, 2}, {1, 2},
	};

	/** Demi-cote du coeur, celui qui se remplit de portail. */
	private static final int CORE = 1;

	private CorruptedPortalShape() {
	}

	/**
	 * Les douze ecarts de l'anneau, pour qui veut le batir plutot que le reconnaitre.
	 *
	 * <p>Le tableau est rendu tel quel : il n'est lu que par le code qui bat l'anneau du retour, et
	 * le recopier a chaque arrivee ne protegerait de rien qui puisse arriver ici.
	 */
	public static int[][] ringOffsets() {
		return RING;
	}

	/**
	 * Cherche un anneau complet dont ce bloc fait partie.
	 *
	 * <p>Un bloc de cadre peut appartenir a plusieurs anneaux possibles selon sa position dans le
	 * motif ; on les essaie tous et on retient le premier qui soit entierement garni.
	 *
	 * @return le centre de l'anneau, ou {@code null} s'il n'y en a pas de complet
	 */
	@Nullable
	public static BlockPos findCompleteRing(LevelAccessor level, BlockPos framePos) {
		for (int[] offset : RING) {
			// Si ce bloc occupe cet emplacement du motif, le centre se deduit par soustraction.
			BlockPos centre = framePos.offset(-offset[0], 0, -offset[1]);
			if (isComplete(level, centre)) {
				return centre;
			}
		}
		return null;
	}

	/** Vrai si les douze emplacements autour de ce centre portent un cadre garni. */
	public static boolean isComplete(LevelAccessor level, BlockPos centre) {
		for (int[] offset : RING) {
			BlockState state = level.getBlockState(centre.offset(offset[0], 0, offset[1]));

			if (!state.is(ModBlocks.CORRUPTED_PORTAL_FRAME.get())
					|| !state.getValue(CorruptedPortalFrameBlock.HAS_EYE)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Remplit le coeur de l'anneau.
	 *
	 * <p>Les blocs de portail sont poses sans notifier les voisins : chacun d'eux declencherait
	 * sinon la mise a jour des huit autres, pour un motif qui ne bouge plus une fois pose.
	 */
	public static void light(Level level, BlockPos centre) {
		BlockState portal = ModBlocks.CORRUPTED_PORTAL.get().defaultBlockState();

		for (int dx = -CORE; dx <= CORE; dx++) {
			for (int dz = -CORE; dz <= CORE; dz++) {
				level.setBlock(centre.offset(dx, 0, dz), portal, Block.UPDATE_CLIENTS);
			}
		}

		level.levelEvent(1038, centre, 0);
	}
}
