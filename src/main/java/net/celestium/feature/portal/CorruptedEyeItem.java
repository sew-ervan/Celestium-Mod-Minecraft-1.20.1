package net.celestium.feature.portal;

import net.celestium.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * L'oeil qui garnit un cadre corrompu.
 *
 * <p>Il se pose comme l'oeil de l'Ender sur le portail de l'End : un clic droit sur un cadre encore
 * vide, et l'oeil y reste. Douze suffisent a ouvrir le passage.
 *
 * <p>Contrairement a celui de l'Ender, il ne se lance pas pour chercher une structure : il n'y a
 * rien a trouver, le cadre se batit ou l'on veut. C'est le prix des douze yeux qui fait l'epreuve,
 * pas la recherche.
 */
public class CorruptedEyeItem extends Item {

	public CorruptedEyeItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);

		if (!state.is(ModBlocks.CORRUPTED_PORTAL_FRAME.get())
				|| state.getValue(CorruptedPortalFrameBlock.HAS_EYE)) {
			return InteractionResult.PASS;
		}

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		BlockState filled = state.setValue(CorruptedPortalFrameBlock.HAS_EYE, true);
		level.setBlock(pos, filled, Block.UPDATE_ALL);
		level.updateNeighbourForOutputSignal(pos, ModBlocks.CORRUPTED_PORTAL_FRAME.get());

		context.getItemInHand().shrink(consumed(context.getPlayer()) ? 1 : 0);
		level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 0.8F);

		BlockPos centre = CorruptedPortalShape.findCompleteRing(level, pos);
		if (centre != null) {
			CorruptedPortalShape.light(level, centre);
			level.playSound(null, centre, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 0.7F);
		}

		return InteractionResult.CONSUME;
	}

	private static boolean consumed(@Nullable Player player) {
		return player == null || !player.getAbilities().instabuild;
	}
}
