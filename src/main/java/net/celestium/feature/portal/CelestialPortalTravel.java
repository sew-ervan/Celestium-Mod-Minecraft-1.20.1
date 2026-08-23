package net.celestium.feature.portal;

import net.celestium.init.ModBlocks;
import net.celestium.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Le voyage entre l'Overworld et la dimension celeste.
 *
 * <p>A l'arrivee, un portail existant est cherche autour des memes coordonnees ; s'il n'y en a
 * pas, un cadre complet est bati sur place pour garantir le retour. Sans cette construction, un
 * joueur arrivant dans la dimension celeste s'y retrouverait prisonnier.
 */
public final class CelestialPortalTravel implements ITeleporter {

	/** Rayon de recherche d'un portail deja existant a l'arrivee, en blocs. */
	private static final int SEARCH_RADIUS = 8;
	private static final int SEARCH_VERTICAL = 16;

	/** Dimensions interieures du cadre construit d'office. */
	private static final int BUILT_WIDTH = 2;
	private static final int BUILT_HEIGHT = 3;

	private CelestialPortalTravel() {
	}

	/** Declenche le voyage quand une entite traverse la surface du portail. */
	public static void onEntityInPortal(Entity entity) {
		if (entity.isOnPortalCooldown() || !(entity.level() instanceof ServerLevel origin)) {
			return;
		}
		entity.setPortalCooldown();

		MinecraftServer server = origin.getServer();
		ResourceKey<Level> targetKey = origin.dimension() == ModDimensions.CELESTIAL_LEVEL
				? Level.OVERWORLD
				: ModDimensions.CELESTIAL_LEVEL;

		ServerLevel destination = server.getLevel(targetKey);
		if (destination == null) {
			return;
		}

		Entity arrived = entity.changeDimension(destination, new CelestialPortalTravel());
		if (arrived instanceof ServerPlayer player) {
			CelestialPortalBlock.announceArrival(player, destination);
		}
	}

	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel destination,
			Function<ServerLevel, PortalInfo> defaultPortalInfo) {

		BlockPos arrival = findOrBuildPortal(destination, entity.blockPosition());
		return new PortalInfo(Vec3.atBottomCenterOf(arrival), Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	/** L'entite conserve son elan : rien a recalculer au-dela du placement. */
	@Override
	public boolean playTeleportSound(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
		return true;
	}

	private static BlockPos findOrBuildPortal(ServerLevel level, BlockPos around) {
		BlockPos existing = findExistingPortal(level, around);
		return existing != null ? existing : buildPortal(level, around);
	}

	/** Cherche une surface de portail deja posee dans le voisinage immediat. */
	@Nullable
	private static BlockPos findExistingPortal(ServerLevel level, BlockPos around) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
			for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
				for (int dy = -SEARCH_VERTICAL; dy <= SEARCH_VERTICAL; dy++) {
					cursor.set(around.getX() + dx, around.getY() + dy, around.getZ() + dz);
					if (level.getBlockState(cursor).is(ModBlocks.CELESTIAL_PORTAL.get())) {
						return cursor.immutable();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Bâtit un cadre complet et allume le portail.
	 *
	 * <p>Une plateforme est posee sous le cadre : sans elle, un portail cree au-dessus d'un
	 * ravin laisserait le joueur tomber a l'arrivee.
	 */
	private static BlockPos buildPortal(ServerLevel level, BlockPos around) {
		int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, around.getX(), around.getZ());
		int floor = Math.max(level.getMinBuildHeight() + 1, Math.min(surface, level.getMaxBuildHeight() - 8));
		BlockPos base = new BlockPos(around.getX(), floor, around.getZ());

		BlockState frame = ModBlocks.CELESTIUM_BLOCK.get().defaultBlockState();

		// Plateforme d'appui, un bloc plus large que le cadre de chaque cote.
		for (int dx = -1; dx <= BUILT_WIDTH; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				level.setBlock(base.offset(dx, -1, dz), frame, Block.UPDATE_ALL);
			}
		}

		// Degagement : le cadre ne peut pas s'allumer si l'interieur n'est pas vide.
		for (int dx = -1; dx <= BUILT_WIDTH; dx++) {
			for (int dy = 0; dy <= BUILT_HEIGHT + 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					level.setBlock(base.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
				}
			}
		}

		// Montants, linteau et seuil, sur l'axe X.
		for (int dy = -1; dy <= BUILT_HEIGHT; dy++) {
			level.setBlock(base.offset(-1, dy, 0), frame, Block.UPDATE_ALL);
			level.setBlock(base.offset(BUILT_WIDTH, dy, 0), frame, Block.UPDATE_ALL);
		}
		for (int dx = 0; dx < BUILT_WIDTH; dx++) {
			level.setBlock(base.offset(dx, -1, 0), frame, Block.UPDATE_ALL);
			level.setBlock(base.offset(dx, BUILT_HEIGHT, 0), frame, Block.UPDATE_ALL);
		}

		CelestialPortalShape shape = CelestialPortalShape.find(level, base);
		if (shape != null) {
			shape.createPortal();
		}

		return base;
	}

	/** Oriente le cadre construit selon l'axe X. */
	@SuppressWarnings("unused")
	private static Direction.Axis builtAxis() {
		return Direction.Axis.X;
	}
}
