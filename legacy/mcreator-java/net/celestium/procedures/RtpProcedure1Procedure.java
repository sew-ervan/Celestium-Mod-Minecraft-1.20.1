package net.celestium.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.celestium.network.CelestiumModVariables;
import net.celestium.CelestiumMod;

import java.util.Calendar;

public class RtpProcedure1Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!(new java.text.SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()))
				.equals((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).JourRTP)) {
			{
				double _setval = entity.getX();
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.TempPlayer1 = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = entity.getY();
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.TempPlayer2 = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = entity.getZ();
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.TempPlayer3 = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			if ((entity.level.dimension()) == Level.OVERWORLD) {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("Tu sera t\u00E9l\u00E9port\u00E9 dans 5 secondes, ne bouge pas."), true);
				CelestiumMod.queueServerWork(100, () -> {
					if (entity.getX() == (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer1
							&& entity.getY() == (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer2
							&& entity.getZ() == (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer3) {
						{
							Entity _ent = entity;
							if (!_ent.level.isClientSide() && _ent.getServer() != null) {
								_ent.getServer().getCommands().performPrefixedCommand(
										new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level instanceof ServerLevel ? (ServerLevel) _ent.level : null, 4, _ent.getName().getString(), _ent.getDisplayName(),
												_ent.level.getServer(), _ent),
										("spreadplayers " + CelestiumModVariables.MapVariables.get(world).SpawnX + " " + CelestiumModVariables.MapVariables.get(world).SpawnZ + " 1000 " + CelestiumModVariables.MapVariables.get(world).RtpRayon
												+ " false " + entity.getDisplayName().getString()));
							}
						}
						if (entity instanceof Player _player && !_player.level.isClientSide())
							_player.displayClientMessage(Component.literal(("spreadplayers " + CelestiumModVariables.MapVariables.get(world).SpawnX + " " + CelestiumModVariables.MapVariables.get(world).SpawnZ + " 1000 "
									+ CelestiumModVariables.MapVariables.get(world).RtpRayon + " false " + entity.getDisplayName().getString())), false);
						if (entity instanceof Player _player && !_player.level.isClientSide())
							_player.displayClientMessage(Component.literal("T\u00E9l\u00E9portation r\u00E9ussi"), true);
						{
							String _setval = new java.text.SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
							entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
								capability.JourRTP = _setval;
								capability.syncPlayerVariables(entity);
							});
						}
					} else {
						if (entity instanceof Player _player && !_player.level.isClientSide())
							_player.displayClientMessage(Component.literal("\u00A7cTu a boug\u00E9. R\u00E9-essaye."), false);
					}
				});
			} else {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("Billy Boy, j'en ai vu des centaines comme toi. Tu ne peut pas te /rtp dans une autre dimension que l'overworld."), false);
			}
		} else {
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("\u00A7cTu a d\u00E9j\u00E0 utilis\u00E9 le /rtp aujourd'hui. Va toucher de l'herbe en attendant."), false);
		}
	}
}
