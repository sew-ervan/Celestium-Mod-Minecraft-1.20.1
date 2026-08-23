package net.celestium.feature.portal;

import net.celestium.CelestiumMod;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Allumage d'un portail celeste.
 *
 * <p>Le rituel : dresser un cadre en blocs de Celestium, puis frotter un fragment celeste contre
 * l'un de ses blocs. Le fragment est consomme.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class PortalIgnition {

	private PortalIgnition() {
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		ItemStack held = event.getItemStack();

		if (!held.is(ModItems.CELESTIUM_FRAGMENT.get())) {
			return;
		}
		if (!level.getBlockState(event.getPos()).is(ModBlocks.CELESTIUM_BLOCK.get())) {
			return;
		}

		// Le cadre s'allume depuis l'interieur : on part de la face touchee.
		BlockPos inside = event.getPos().relative(event.getFace());
		if (!level.getBlockState(inside).isAir()) {
			return;
		}

		if (level.isClientSide()) {
			// Le client ne decide pas : il se contente de ne pas laisser passer le clic au bloc.
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
			return;
		}

		CelestialPortalShape shape = CelestialPortalShape.find(level, inside);
		if (shape == null) {
			return;
		}

		shape.createPortal();
		level.playSound(null, event.getPos(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.4F);

		Player player = event.getEntity();
		if (!player.getAbilities().instabuild) {
			held.shrink(1);
		}

		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.CONSUME);
	}
}
