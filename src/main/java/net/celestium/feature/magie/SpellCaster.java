package net.celestium.feature.magie;

import net.celestium.server.data.ModCapabilities;
import net.celestium.server.data.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Point d'entree unique pour lancer un sort.
 *
 * <p>Verifie le camp, l'energie et la recharge avant de deleguer au sort lui-meme, puis preleve
 * le cout. Les temps de recharge vivent en memoire : ils durent quelques secondes et n'ont pas a
 * survivre a un redemarrage.
 */
public final class SpellCaster {

	private static final Map<UUID, Map<ResourceLocation, Long>> COOLDOWNS = new HashMap<>();

	private SpellCaster() {
	}

	/** Tente de lancer un sort et renvoie vrai s'il a effectivement ete lance. */
	public static boolean tryCast(ServerPlayer caster, Spell spell) {
		PlayerData data = ModCapabilities.of(caster);

		Faction required = spell.requiredFaction();
		if (required != null && data.getFaction() != required) {
			fail(caster, Component.translatable("message.celestium.spell.wrong_faction", required.getDisplayName()));
			return false;
		}

		long now = caster.level().getGameTime();
		long readyAt = cooldownsOf(caster).getOrDefault(spell.id(), 0L);
		if (now < readyAt) {
			fail(caster, Component.translatable("message.celestium.spell.cooling_down",
					(readyAt - now) / 20 + 1));
			return false;
		}

		if (data.getMana() < spell.manaCost()) {
			fail(caster, Component.translatable("message.celestium.spell.no_mana"));
			return false;
		}

		if (!spell.cast(caster, (ServerLevel) caster.level())) {
			return false;
		}

		data.setMana(data.getMana() - spell.manaCost());
		cooldownsOf(caster).put(spell.id(), now + spell.cooldownTicks());
		return true;
	}

	/** Oublie les recharges d'un joueur qui quitte le serveur. */
	public static void forget(ServerPlayer player) {
		COOLDOWNS.remove(player.getUUID());
	}

	private static Map<ResourceLocation, Long> cooldownsOf(ServerPlayer player) {
		return COOLDOWNS.computeIfAbsent(player.getUUID(), id -> new HashMap<>());
	}

	private static void fail(ServerPlayer caster, Component reason) {
		caster.displayClientMessage(reason.copy().withStyle(ChatFormatting.RED), true);
	}
}
