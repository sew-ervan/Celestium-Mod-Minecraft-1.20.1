package net.celestium.feature.portal;

import net.celestium.init.ModBlocks;
import net.celestium.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * Le voyage vers les terres corrompues, et le retour.
 *
 * <p>Les deux mondes partagent leur echelle : une position s'y retrouve a l'identique. C'est ce qui
 * fait sens pour une dimension presentee comme une version alternative de l'Overworld — on arrive
 * la ou l'on serait, dans l'autre monde.
 *
 * <p>A l'arrivee, une plateforme est batie si le terrain ne s'y prete pas. Le portail de l'End ne
 * se soucie pas de ce probleme parce que sa destination est fixe et connue ; ici elle depend d'ou
 * l'on part, donc elle peut tomber n'importe ou — dans un lac de lave ou en plein ciel.
 */
public final class CorruptedPortalTravel implements ITeleporter {

	/** Rayon de la plateforme d'arrivee, en blocs. */
	private static final int PLATFORM_RADIUS = 2;

	/** Hauteur degagee au-dessus de la plateforme. */
	private static final int HEADROOM = 3;

	/** Ecart entre le point d'arrivee et l'anneau du retour, en blocs. */
	private static final int RETURN_OFFSET = 7;

	/** Rayon de recherche d'un retour deja bati. */
	private static final int RETURN_SEARCH = 16;

	private CorruptedPortalTravel() {
	}

	public static void travel(Entity entity, ServerLevel origin) {
		MinecraftServer server = origin.getServer();
		ResourceKey<Level> targetKey = origin.dimension() == ModDimensions.CORRUPTED_LEVEL
				? Level.OVERWORLD
				: ModDimensions.CORRUPTED_LEVEL;

		ServerLevel destination = server.getLevel(targetKey);
		if (destination == null) {
			return;
		}

		Entity arrived = entity.changeDimension(destination, new CorruptedPortalTravel());
		if (arrived instanceof ServerPlayer player) {
			CorruptedPortalBlock.announce(player, destination);
		}
	}

	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel destination,
			Function<ServerLevel, PortalInfo> defaultPortalInfo) {

		BlockPos arrival = platform(destination, entity.blockPosition());

		if (destination.dimension() == ModDimensions.CORRUPTED_LEVEL && !hasWayBack(destination, arrival)) {
			buildWayBack(destination, arrival.offset(RETURN_OFFSET, 0, 0));
		}

		return new PortalInfo(Vec3.atBottomCenterOf(arrival), Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	/** Vrai s'il y a deja de quoi repartir dans le voisinage. */
	private static boolean hasWayBack(ServerLevel level, BlockPos around) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int dx = -RETURN_SEARCH; dx <= RETURN_SEARCH; dx++) {
			for (int dz = -RETURN_SEARCH; dz <= RETURN_SEARCH; dz++) {
				for (int dy = -4; dy <= 4; dy++) {
					cursor.set(around.getX() + dx, around.getY() + dy, around.getZ() + dz);
					if (level.getBlockState(cursor).is(ModBlocks.CORRUPTED_PORTAL.get())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Batit l'anneau du retour, deja garni.
	 *
	 * <p>Sans lui, entrer coute douze yeux et sortir en couterait douze autres, a fabriquer sur place
	 * avec des perles d'Ender qu'on n'y trouve pas : la dimension serait un aller simple.
	 *
	 * <p>L'anneau est pose a l'ecart du point d'arrivee, et non dessous. Y deposer le voyageur le
	 * renverrait chez lui des la fin de son delai de portail, avant meme qu'il ait eu le temps de
	 * regarder autour de lui.
	 */
	private static void buildWayBack(ServerLevel level, BlockPos centre) {
		BlockState floor = Blocks.BLACKSTONE.defaultBlockState();
		BlockState frame = ModBlocks.CORRUPTED_PORTAL_FRAME.get().defaultBlockState()
				.setValue(CorruptedPortalFrameBlock.HAS_EYE, true);

		// Un socle degage, faute de quoi l'anneau se retrouverait a moitie enterre.
		for (int dx = -3; dx <= 3; dx++) {
			for (int dz = -3; dz <= 3; dz++) {
				level.setBlock(centre.offset(dx, -1, dz), floor, Block.UPDATE_ALL);
				for (int dy = 0; dy <= HEADROOM; dy++) {
					level.setBlock(centre.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(),
							Block.UPDATE_ALL);
				}
			}
		}

		for (int[] offset : CorruptedPortalShape.ringOffsets()) {
			level.setBlock(centre.offset(offset[0], 0, offset[1]), frame, Block.UPDATE_ALL);
		}

		CorruptedPortalShape.light(level, centre);
	}

	@Override
	public boolean playTeleportSound(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
		return true;
	}

	/**
	 * Pose de quoi tenir debout, et rend l'emplacement ou deposer le voyageur.
	 *
	 * <p>La plateforme est en pierre noire : elle se distingue des deux sols de la dimension, ce qui
	 * donne un repere pour retrouver son point d'arrivee.
	 */
	private static BlockPos platform(ServerLevel level, BlockPos around) {
		int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, around.getX(), around.getZ());
		int floor = Math.max(level.getMinBuildHeight() + 2,
				Math.min(surface + 1, level.getMaxBuildHeight() - HEADROOM - 2));

		BlockPos base = new BlockPos(around.getX(), floor, around.getZ());
		BlockState slab = Blocks.BLACKSTONE.defaultBlockState();
		BlockState air = Blocks.AIR.defaultBlockState();

		for (int dx = -PLATFORM_RADIUS; dx <= PLATFORM_RADIUS; dx++) {
			for (int dz = -PLATFORM_RADIUS; dz <= PLATFORM_RADIUS; dz++) {
				level.setBlock(base.offset(dx, -1, dz), slab, Block.UPDATE_ALL);

				for (int dy = 0; dy < HEADROOM; dy++) {
					level.setBlock(base.offset(dx, dy, dz), air, Block.UPDATE_ALL);
				}
			}
		}

		return base;
	}
}
