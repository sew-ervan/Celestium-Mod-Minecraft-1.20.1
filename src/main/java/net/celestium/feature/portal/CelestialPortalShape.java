package net.celestium.feature.portal;

import net.celestium.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

/**
 * Reconnait un cadre de portail celeste et le remplit.
 *
 * <p>Le cadre est fait de blocs de Celestium, sur le modele du portail du Nether : un rectangle
 * creux, pose verticalement, oriente selon l'axe X ou Z. L'interieur doit etre vide.
 *
 * <p>Vanilla resout le meme probleme avec {@code PortalShape}, mais cette classe est ecrite pour
 * l'obsidienne et le portail du Nether : elle n'est pas reutilisable telle quelle.
 */
public final class CelestialPortalShape {

	/** Dimensions interieures acceptees, bornes comprises. */
	private static final int MIN_WIDTH = 2;
	private static final int MAX_WIDTH = 21;
	private static final int MIN_HEIGHT = 3;
	private static final int MAX_HEIGHT = 21;

	private final LevelAccessor level;
	private final Direction.Axis axis;
	private final BlockPos bottomLeft;
	private final int width;
	private final int height;

	private CelestialPortalShape(LevelAccessor level, Direction.Axis axis, BlockPos bottomLeft,
			int width, int height) {
		this.level = level;
		this.axis = axis;
		this.bottomLeft = bottomLeft;
		this.width = width;
		this.height = height;
	}

	/**
	 * Cherche un cadre valide contenant la position donnee, sur l'un ou l'autre axe.
	 *
	 * @return la forme trouvee, ou {@code null} si la position n'est pas dans un cadre complet
	 */
	@Nullable
	public static CelestialPortalShape find(LevelAccessor level, BlockPos inside) {
		CelestialPortalShape onX = findOnAxis(level, inside, Direction.Axis.X);
		return onX != null ? onX : findOnAxis(level, inside, Direction.Axis.Z);
	}

	@Nullable
	private static CelestialPortalShape findOnAxis(LevelAccessor level, BlockPos inside, Direction.Axis axis) {
		Direction left = axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
		Direction right = left.getOpposite();

		BlockPos bottomLeft = scanToEdge(level, inside, Direction.DOWN);
		if (bottomLeft == null) {
			return null;
		}
		bottomLeft = scanToEdge(level, bottomLeft, left);
		if (bottomLeft == null) {
			return null;
		}

		int width = measure(level, bottomLeft, right);
		if (width < MIN_WIDTH || width > MAX_WIDTH) {
			return null;
		}

		int height = measure(level, bottomLeft, Direction.UP);
		if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
			return null;
		}

		CelestialPortalShape shape = new CelestialPortalShape(level, axis, bottomLeft, width, height);
		return shape.isComplete() ? shape : null;
	}

	/** Descend ou se decale jusqu'au dernier emplacement libre avant le cadre. */
	@Nullable
	private static BlockPos scanToEdge(LevelAccessor level, BlockPos from, Direction direction) {
		BlockPos current = from;
		for (int step = 0; step <= MAX_HEIGHT; step++) {
			BlockPos next = current.relative(direction);
			if (isFrame(level, next)) {
				return current;
			}
			if (!isEmpty(level, next)) {
				return null;
			}
			current = next;
		}
		return null;
	}

	/** Compte les emplacements libres consecutifs a partir d'une position, celle-ci comprise. */
	private static int measure(LevelAccessor level, BlockPos from, Direction direction) {
		int count = 0;
		BlockPos current = from;
		while (count <= MAX_WIDTH && isEmpty(level, current)) {
			count++;
			current = current.relative(direction);
		}
		return isFrame(level, current) ? count : 0;
	}

	/** Verifie que tout le pourtour du rectangle est bien du bloc de Celestium. */
	private boolean isComplete() {
		Direction right = this.axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;

		for (int column = 0; column < this.width; column++) {
			BlockPos base = this.bottomLeft.relative(right, column);
			if (!isFrame(this.level, base.below()) || !isFrame(this.level, base.above(this.height))) {
				return false;
			}
		}

		for (int row = 0; row < this.height; row++) {
			BlockPos base = this.bottomLeft.above(row);
			if (!isFrame(this.level, base.relative(right.getOpposite()))
					|| !isFrame(this.level, base.relative(right, this.width))) {
				return false;
			}
		}

		return true;
	}

	/** Remplit l'interieur du cadre de blocs de portail. */
	public void createPortal() {
		Direction right = this.axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
		BlockState portal = ModBlocks.CELESTIAL_PORTAL.get().defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_AXIS, this.axis);

		for (int column = 0; column < this.width; column++) {
			for (int row = 0; row < this.height; row++) {
				BlockPos pos = this.bottomLeft.relative(right, column).above(row);
				this.level.setBlock(pos, portal, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
			}
		}
	}

	private static boolean isFrame(LevelAccessor level, BlockPos pos) {
		return level.getBlockState(pos).is(ModBlocks.CELESTIUM_BLOCK.get());
	}

	/** Un emplacement interieur est libre s'il est vide ou deja occupe par un portail. */
	private static boolean isEmpty(LevelAccessor level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.isAir() || state.is(ModBlocks.CELESTIAL_PORTAL.get());
	}
}
