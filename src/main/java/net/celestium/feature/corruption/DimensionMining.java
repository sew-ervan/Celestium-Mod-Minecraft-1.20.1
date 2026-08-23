package net.celestium.feature.corruption;

import net.celestium.CelestiumMod;
import net.celestium.core.material.ModTiers;
import net.celestium.worldgen.ModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * La pierre des Terres du demon ne cede qu'a ce qui lui ressemble.
 *
 * <p>Rien ne s'y creuse a mains nues, ni avec un outil de l'Overworld, si bon soit-il : une pioche
 * en netherite n'y arrache pas un bloc. Seuls mordent le Celestium corrompu, qu'on emporte avec
 * soi, et le Demonium, qu'on ramasse sur place. C'est ce qui donne son prix a la panoplie de
 * voyage : sans elle on entre, on regarde, et on repart les mains vides.
 *
 * <p>Le blocage passe par deux evenements distincts, car un seul ne suffit pas.
 * {@link PlayerEvent.BreakSpeed} couvre le minage ordinaire, celui qui prend du temps. Mais les
 * blocs a durete nulle — herbes, torches, pousses — se cassent sans jamais passer par la vitesse
 * de minage ; il faut {@link BlockEvent.BreakEvent} pour les retenir eux aussi.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class DimensionMining {

	/** Intervalle minimal entre deux rappels a l'ecran, en ticks. */
	private static final int WARNING_INTERVAL = 40;

	private DimensionMining() {
	}

	/** Le minage ordinaire : la vitesse tombe a rien, le bloc ne cede jamais. */
	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (!restricted(player)) {
			return;
		}
		event.setCanceled(true);
		warn(player);
	}

	/** Les blocs qui cassent d'un seul coup, que la vitesse de minage ne freine pas. */
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		Player player = event.getPlayer();
		if (!restricted(player)) {
			return;
		}
		event.setCanceled(true);
		warn(player);
	}

	/**
	 * Vrai lorsque ce joueur, ici et maintenant, n'a pas de quoi casser.
	 *
	 * <p>Le mode creatif echappe a la regle : elle sert a doser une progression, pas a empecher de
	 * construire.
	 */
	private static boolean restricted(Player player) {
		if (player == null || player.getAbilities().instabuild) {
			return false;
		}

		Level level = player.level();
		return level.dimension() == ModDimensions.DEMON_LEVEL && !breaks(player.getMainHandItem());
	}

	/** Vrai pour les outils dont le palier mord la pierre du demon. */
	public static boolean breaks(ItemStack stack) {
		if (!(stack.getItem() instanceof TieredItem tool)) {
			return false;
		}
		return tool.getTier() == ModTiers.CORRUPTED_CELESTIUM || tool.getTier() == ModTiers.DEMONIUM;
	}

	/**
	 * Rappelle la regle, sans inonder l'ecran.
	 *
	 * <p>Le minage declenche l'evenement de vitesse a chaque tick : sans cet espacement le message
	 * serait reecrit vingt fois par seconde.
	 */
	private static void warn(Player player) {
		if (player.level().isClientSide() || player.tickCount % WARNING_INTERVAL != 0) {
			return;
		}
		player.displayClientMessage(Component.translatable("message.celestium.mining.refused"), true);
	}
}
