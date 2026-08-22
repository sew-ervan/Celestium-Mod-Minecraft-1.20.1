package net.celestium.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.celestium.feature.magie.Faction;
import net.celestium.server.data.ModCapabilities;
import net.celestium.server.data.PlayerData;
import net.celestium.server.data.PlayerDataSyncPacket;
import net.celestium.server.data.ServerData;
import net.celestium.server.teleport.Teleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

/**
 * {@code /celestium ...} : toutes les commandes d'administration sous une racine commune.
 *
 * <p>Le mod d'origine les eparpillait en commandes de premier niveau au nommage inconsistant :
 * {@code AdminSetServerSpawn}, {@code AdminSetRtpRayon}, {@code AdminReset},
 * {@code AdminMagieDebugEquipe} — avec des majuscules, ce qui obligeait a les taper exactement.
 */
public final class CelestiumAdminCommand {

	private static final int PERMISSION_LEVEL = 3;

	private static final SuggestionProvider<CommandSourceStack> FACTION_SUGGESTIONS =
			(context, builder) -> SharedSuggestionProvider.suggest(
					Arrays.stream(Faction.values()).map(Faction::getSerializedName), builder);

	private CelestiumAdminCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("celestium")
				.requires(source -> source.hasPermission(PERMISSION_LEVEL))

				.then(Commands.literal("setspawn")
						.executes(context -> setSpawn(context.getSource())))

				.then(Commands.literal("rtpradius")
						.then(Commands.argument("rayon", IntegerArgumentType.integer(1, 30_000_000))
								.executes(context -> setRtpRadius(context.getSource(),
										IntegerArgumentType.getInteger(context, "rayon")))))

				.then(Commands.literal("faction")
						.then(Commands.argument("joueur", EntityArgument.player())
								.then(Commands.argument("faction",
												StringArgumentType.word())
										.suggests(FACTION_SUGGESTIONS)
										.executes(context -> setFaction(context.getSource(),
												EntityArgument.getPlayer(context, "joueur"),
												StringArgumentType.getString(context, "faction"))))))

				.then(Commands.literal("resethome")
						.then(Commands.argument("joueur", EntityArgument.player())
								.executes(context -> resetHome(context.getSource(),
										EntityArgument.getPlayer(context, "joueur"))))));
	}

	private static int setSpawn(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		GlobalPos position = Teleporter.currentPosition(player);

		ServerData.get(source.getServer()).setSpawn(position);

		source.sendSuccess(() -> Component.translatable("message.celestium.admin.spawn_set",
				position.pos().getX(), position.pos().getY(), position.pos().getZ())
				.withStyle(ChatFormatting.GREEN), true);
		return 1;
	}

	private static int setRtpRadius(CommandSourceStack source, int radius) {
		ServerData.get(source.getServer()).setRtpRadius(radius);
		source.sendSuccess(() -> Component.translatable("message.celestium.admin.rtp_radius_set", radius), true);
		return 1;
	}

	private static int setFaction(CommandSourceStack source, ServerPlayer target, String factionName) {
		// byName retombe sur le camp neutre pour un nom inconnu : sans ce controle, une faute de
		// frappe rangerait silencieusement le joueur chez les neutres.
		Faction faction = Faction.find(factionName).orElse(null);
		if (faction == null) {
			source.sendFailure(Component.translatable("message.celestium.admin.unknown_faction", factionName));
			return 0;
		}

		PlayerData data = ModCapabilities.of(target);
		data.setFaction(faction);
		PlayerDataSyncPacket.sendTo(target, data);

		source.sendSuccess(() -> Component.translatable("message.celestium.admin.faction_set",
				target.getDisplayName(), faction.getDisplayName()), true);
		return 1;
	}

	private static int resetHome(CommandSourceStack source, ServerPlayer target) {
		PlayerData data = ModCapabilities.of(target);
		data.setHome(null);
		PlayerDataSyncPacket.sendTo(target, data);

		source.sendSuccess(() -> Component.translatable("message.celestium.admin.home_reset",
				target.getDisplayName()), true);
		return 1;
	}
}
