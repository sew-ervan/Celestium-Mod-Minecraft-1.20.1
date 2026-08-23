package net.celestium.feature.altar;

import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.celestium.feature.mob.DemonSwordsmanEntity;

/**
 * Autel d'invocation.
 *
 * <p>Offrir un lingot de Demonium a l'autel rappelle le demon epeiste. Il repond ainsi a un
 * probleme concret : le boss n'apparait spontanement que dans les biomes sombres de la surface, ce
 * qui en fait une rencontre du hasard. L'autel permet de le provoquer, et donc de refaire le plein
 * de fragments sans errer des nuits durant.
 *
 * <p>Le cout est volontairement eleve — un lingot vaut neuf fragments — pour que l'invocation
 * reste un choix et non une routine.
 */
public class SummoningAltarBlock extends Block {

	/** Hauteur d'apparition au-dessus de l'autel, pour ne pas coincer le demon dans le sol. */
	private static final double SPAWN_HEIGHT = 1.5;

	public SummoningAltarBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {

		ItemStack held = player.getItemInHand(hand);
		if (!held.is(ModItems.DEMONIUM_INGOT.get())) {
			return InteractionResult.PASS;
		}

		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}

		DemonSwordsmanEntity demon = ModEntities.DEMON_SWORDSMAN.get().create(serverLevel);
		if (demon == null) {
			return InteractionResult.PASS;
		}

		demon.moveTo(pos.getX() + 0.5, pos.getY() + SPAWN_HEIGHT, pos.getZ() + 0.5,
				player.getYRot() + 180.0F, 0.0F);
		demon.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
				MobSpawnType.TRIGGERED, null, null);
		demon.setTarget(player);
		serverLevel.addFreshEntity(demon);

		serverLevel.playSound(null, pos, SoundEvents.WARDEN_ROAR, SoundSource.BLOCKS, 2.0F, 0.5F);
		player.displayClientMessage(Component.translatable("message.celestium.altar.summoned"), true);

		if (!player.getAbilities().instabuild) {
			held.shrink(1);
		}
		return InteractionResult.CONSUME;
	}
}
