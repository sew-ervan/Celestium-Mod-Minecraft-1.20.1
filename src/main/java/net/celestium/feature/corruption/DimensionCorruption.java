package net.celestium.feature.corruption;

import net.celestium.CelestiumMod;
import net.celestium.core.material.ModArmorMaterials;
import net.celestium.feature.celestium.ModArmorItem;
import net.celestium.worldgen.ModDamageTypes;
import net.celestium.worldgen.ModDimensions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * La corruption des Terres du demon.
 *
 * <p>Le monde du demon rejette qui n'est pas des siens. Sans protection, la corruption ralentit,
 * engourdit, aveugle, puis ronge. Chaque piece d'armure protectrice portee en repousse un cran :
 * la parure complete l'annule entierement.
 *
 * <p>Deux parures protegent. Le Celestium corrompu, fabricable des l'Overworld, est la tenue du
 * voyage : sans elle on ne peut pas s'installer. Le Demonium, qu'on ne trouve que sur place,
 * protege aussi — de quoi troquer la tenue de voyage contre un vrai equipement une fois etabli.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class DimensionCorruption {

	/** Nombre de pieces necessaires a une protection totale. */
	private static final int FULL_PROTECTION = 4;

	/** Periode de reevaluation, en ticks. Inutile de recompter l'armure vingt fois par seconde. */
	private static final int CHECK_INTERVAL = 20;

	/** Duree des effets appliques, un peu superieure a la periode pour ne jamais s'interrompre. */
	private static final int EFFECT_DURATION = 60;

	/** Degats infliges a chaque reevaluation lorsque rien ne protege. */
	private static final float UNPROTECTED_DAMAGE = 1.0F;

	private DimensionCorruption() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (player.level().dimension() != ModDimensions.DEMON_LEVEL) {
			return;
		}
		if (player.tickCount % CHECK_INTERVAL != 0) {
			return;
		}
		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		applyCorruption(player, countProtectivePieces(player));
	}

	/**
	 * Compte les pieces portees qui repoussent la corruption.
	 *
	 * <p>Publique parce que la corruption n'est pas seule a s'y interesser : les villageois
	 * corrompus jugent un visiteur a ce qu'il porte, et ne reconnaissent des leurs que qui en a
	 * au moins une piece sur le dos.
	 */
	public static int countProtectivePieces(Player player) {
		int worn = 0;
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof ModArmorItem armor && protects(armor.getArmorMaterial())) {
				worn++;
			}
		}
		return worn;
	}

	private static boolean protects(ModArmorMaterials material) {
		return material == ModArmorMaterials.CORRUPTED_CELESTIUM || material == ModArmorMaterials.DEMONIUM;
	}

	/**
	 * Applique la sanction correspondant au nombre de pieces portees.
	 *
	 * <p>La progression est volontairement graduelle : chaque piece trouvee compte, plutot qu\u2019un
	 * tout ou rien qui rendrait les trois premieres inutiles.
	 */
	private static void applyCorruption(ServerPlayer player, int protection) {
		if (protection >= FULL_PROTECTION) {
			return;
		}

		// Toujours present des qu'il manque une piece : le monde pese sur le voyageur.
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION, 0, true, false));

		if (protection <= 2) {
			player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECT_DURATION, 0, true, false));
		}
		if (protection <= 1) {
			player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, EFFECT_DURATION, 0, true, false));
		}
		if (protection == 0) {
			player.hurt(corruptionSource(player), UNPROTECTED_DAMAGE);
			warn(player);
		}
	}

	private static DamageSource corruptionSource(ServerPlayer player) {
		Holder<DamageType> type = player.level().registryAccess()
				.registryOrThrow(Registries.DAMAGE_TYPE)
				.getHolderOrThrow(ModDamageTypes.CORRUPTION);
		return new DamageSource(type);
	}

	/** Previent le joueur de ce qui le tue, sinon la sanction reste incomprehensible. */
	private static void warn(ServerPlayer player) {
		player.displayClientMessage(Component.translatable("message.celestium.corruption.unprotected"), true);
	}
}
