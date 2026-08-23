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
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Le voyage entre l'Overworld et les Terres du demon.
 *
 * <p>Le passage n'est pas immediat : il faut se tenir dans le portail un moment avant qu'il
 * n'emporte, comme celui du Nether. Une fois arrive, un delai empeche de repartir aussitot, sans
 * quoi le joueur ferait des allers-retours a chaque pas.
 *
 * <p>A l'arrivee, un portail existant est cherche autour des coordonnees correspondantes ; s'il
 * n'y en a pas, un cadre complet est bati sur place. Sans cette construction, un joueur arrivant
 * dans les Terres du demon y serait prisonnier.
 */
public final class DemonPortalTravel implements ITeleporter {

	/** Duree passee dans le portail avant qu'il n'emporte, en ticks. Le Nether en demande 80. */
	private static final int WARMUP_TICKS = 80;

	/** Rayon de recherche d'un portail deja existant a l'arrivee, en blocs. */
	private static final int SEARCH_RADIUS = 8;
	private static final int SEARCH_VERTICAL = 16;

	/** Dimensions interieures du cadre construit d'office. */
	private static final int BUILT_WIDTH = 2;
	private static final int BUILT_HEIGHT = 3;

	/** Compte le temps passe dans le portail, par entite. */
	private static final Map<UUID, Progress> PROGRESS = new HashMap<>();

	private DemonPortalTravel() {
	}

	/** Avancement d'une entite vers le passage, et dernier tick ou elle a ete vue dans le portail. */
	private static final class Progress {
		private int ticks;

		/** Negatif tant que l'entite n'a pas ete vue, pour ne pas coincider avec le tick zero. */
		private long lastSeen = -1;
	}

	/**
	 * Appele a chaque tick tant qu'une entite occupe la surface du portail.
	 *
	 * <p>Le compteur repart de zero des qu'un tick est manque : sortir du portail annule donc
	 * l'attente, exactement comme en vanilla.
	 *
	 * <p>Un joueur debout dans un portail occupe plusieurs blocs a la fois — deux en hauteur au
	 * minimum, quatre s'il chevauche deux colonnes. Cette methode est donc appelee plusieurs fois
	 * par tick, et il faut n'en retenir qu'une : sans ce garde-fou, le deuxieme appel voit un
	 * dernier passage date du tick courant, croit a une interruption et remet le compteur a zero.
	 * L'attente ne depassait jamais un tick et le portail n'emportait personne.
	 */
	public static void onEntityInPortal(Entity entity) {
		if (entity.isOnPortalCooldown() || !(entity.level() instanceof ServerLevel origin)) {
			return;
		}

		long now = origin.getGameTime();
		Progress progress = PROGRESS.computeIfAbsent(entity.getUUID(), id -> new Progress());

		if (progress.lastSeen == now) {
			return;
		}
		if (progress.lastSeen != now - 1) {
			progress.ticks = 0;
		}
		progress.lastSeen = now;
		progress.ticks++;

		if (progress.ticks < WARMUP_TICKS) {
			return;
		}

		PROGRESS.remove(entity.getUUID());
		entity.setPortalCooldown();
		travel(entity, origin);
	}

	private static void travel(Entity entity, ServerLevel origin) {
		MinecraftServer server = origin.getServer();
		ResourceKey<Level> targetKey = origin.dimension() == ModDimensions.DEMON_LEVEL
				? Level.OVERWORLD
				: ModDimensions.DEMON_LEVEL;

		ServerLevel destination = server.getLevel(targetKey);
		if (destination == null) {
			return;
		}

		Entity arrived = entity.changeDimension(destination, new DemonPortalTravel());
		if (arrived instanceof ServerPlayer player) {
			DemonPortalBlock.announceArrival(player, destination);
		}
	}

	/** Oublie l'attente d'une entite qui disparait, pour ne pas retenir son identifiant. */
	public static void forget(Entity entity) {
		PROGRESS.remove(entity.getUUID());
	}

	/**
	 * Nombre de ticks deja passes dans le portail par cette entite.
	 *
	 * <p>Expose pour les tests : c'est la seule facon d'observer que l'attente progresse bien d'un
	 * cran par tick, et non par appel.
	 */
	public static int warmupOf(Entity entity) {
		Progress progress = PROGRESS.get(entity.getUUID());
		return progress == null ? 0 : progress.ticks;
	}

	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel destination,
			Function<ServerLevel, PortalInfo> defaultPortalInfo) {

		BlockPos target = scaledPosition(entity, destination);
		BlockPos arrival = findOrBuildPortal(destination, target);
		return new PortalInfo(Vec3.atBottomCenterOf(arrival), Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	/**
	 * Convertit la position de depart dans l'echelle du monde d'arrivee.
	 *
	 * <p>Les Terres du demon sont six fois plus petites que l'Overworld : six mille blocs la-bas
	 * en valent mille ici. Sans cette conversion, la dimension serait aussi vaste que l'Overworld
	 * et son echelle declaree ne servirait a rien.
	 */
	private static BlockPos scaledPosition(Entity entity, ServerLevel destination) {
		DimensionType from = entity.level().dimensionType();
		double scale = DimensionType.getTeleportationScale(from, destination.dimensionType());

		int x = (int) Math.round(entity.getX() * scale);
		int z = (int) Math.round(entity.getZ() * scale);

		return new BlockPos(
				Mth_clamp(x, destination),
				Math.min(entity.getBlockY(), destination.getMaxBuildHeight() - 8),
				Mth_clamp(z, destination));
	}

	/** Ramene une coordonnee horizontale dans la bordure du monde d'arrivee. */
	private static int Mth_clamp(int coordinate, ServerLevel destination) {
		int limit = (int) destination.getWorldBorder().getSize() / 2 - 16;
		return Math.max(-limit, Math.min(limit, coordinate));
	}

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
					if (level.getBlockState(cursor).is(ModBlocks.DEMON_PORTAL.get())) {
						return cursor.immutable();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Batit un cadre complet et allume le portail.
	 *
	 * <p>Une plateforme est posee sous le cadre : sans elle, un portail cree au-dessus d'un ravin
	 * laisserait le joueur tomber a l'arrivee.
	 */
	private static BlockPos buildPortal(ServerLevel level, BlockPos around) {
		int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, around.getX(), around.getZ());
		int floor = Math.max(level.getMinBuildHeight() + 1, Math.min(surface, level.getMaxBuildHeight() - 8));
		BlockPos base = new BlockPos(around.getX(), floor, around.getZ());

		BlockState frame = ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get().defaultBlockState();

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

		DemonPortalShape shape = DemonPortalShape.find(level, base);
		if (shape != null) {
			shape.createPortal();
		}

		return base;
	}
}
