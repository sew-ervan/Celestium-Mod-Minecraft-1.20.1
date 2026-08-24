package net.celestium.feature.enchant;

import net.celestium.CelestiumMod;
import net.celestium.init.ModEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * L'Aimant : ce qui tombe pres d'un porteur va droit dans ses poches.
 *
 * <p>L'interception se fait a l'apparition de l'objet plutot qu'au cassage du bloc. C'est le seul
 * point ou l'on voie passer tout ce qui tombe, quelle qu'en soit la cause : le bloc casse a la main,
 * ceux qu'emportent l'excavation ou le filon, et le butin d'une creature abattue au passage.
 *
 * <p>Que les depouilles suivent aussi est assume : un aimant qui trierait ce qu'il attire serait
 * une curiosite a expliquer, la ou celui-ci se comprend sans notice.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class Magnetism {

	/** Distance a laquelle l'aimant agit, en blocs. */
	private static final double RANGE = 6.0;

	private Magnetism() {
	}

	@SubscribeEvent
	public static void onItemSpawn(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ItemEntity item)) {
			return;
		}

		Player owner = bearerNear(event.getLevel(), item);
		if (owner == null) {
			return;
		}

		ItemStack stack = item.getItem();
		if (stack.isEmpty()) {
			return;
		}

		// Ce que l'inventaire refuse reste au sol : un aimant ne doit pas faire disparaitre ce qu'il
		// ne peut pas ranger.
		if (!owner.getInventory().add(stack)) {
			return;
		}

		event.setCanceled(true);
		owner.level().playSound(null, owner.blockPosition(), SoundEvents.ITEM_PICKUP,
				SoundSource.PLAYERS, 0.2F, 1.8F);
	}

	/** Le joueur le plus proche qui porte l'Aimant en main, s'il y en a un a portee. */
	private static Player bearerNear(Level level, ItemEntity item) {
		Player nearest = level.getNearestPlayer(item.getX(), item.getY(), item.getZ(), RANGE, false);

		if (nearest == null || nearest.isSpectator()) {
			return null;
		}

		ItemStack tool = nearest.getItemBySlot(EquipmentSlot.MAINHAND);
		boolean wearing =
				EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAGNETISM.get(), tool) > 0;

		return wearing ? nearest : null;
	}
}
