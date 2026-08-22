package net.celestium.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;

import net.celestium.network.CelestiumModVariables;
import net.celestium.CelestiumMod;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;

public class HomeProcedure1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		if ((StringArgumentType.getString(arguments, "name")).equals("set")) {
			{
				double _setval = Math.round(x);
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeX = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = Math.round(y);
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeY = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = Math.round(z);
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeZ = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				boolean _setval = true;
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeHasBeenSet = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			if ((entity.level.dimension()) == Level.OVERWORLD) {
				{
					String _setval = "Surface";
					entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.HomeDimension = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
			} else if ((entity.level.dimension()) == Level.NETHER) {
				{
					String _setval = "Nether";
					entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.HomeDimension = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
			} else if ((entity.level.dimension()) == Level.END) {
				{
					String _setval = "End";
					entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.HomeDimension = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
			}
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("Home set \u00E0: "), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal(("Dimension:" + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeDimension)), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal(("X: " + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX)), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal(("Y: " + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY)), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal(("Z: " + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ)), false);
		} else if ((StringArgumentType.getString(arguments, "name")).equals("tp")) {
			if ((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeHasBeenSet == true) {
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
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("Ne bouge pas pendant les prochaines 5 secondes pour effectuer le tp au home"), false);
				CelestiumMod.queueServerWork(100, () -> {
					if ((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer1 == entity.getX()
							&& (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer2 == entity.getY()
							&& (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer3 == entity.getZ()) {
						if (((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeDimension).equals("Surface")) {
							if (entity instanceof ServerPlayer _player && !_player.level.isClientSide()) {
								ResourceKey<Level> destinationType = Level.OVERWORLD;
								if (_player.level.dimension() == destinationType)
									return;
								ServerLevel nextLevel = _player.server.getLevel(destinationType);
								if (nextLevel != null) {
									_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
									_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
									_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
									for (MobEffectInstance _effectinstance : _player.getActiveEffects())
										_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance));
									_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
								}
							}
							{
								Entity _ent = entity;
								_ent.teleportTo(((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX),
										((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY),
										((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ));
								if (_ent instanceof ServerPlayer _serverPlayer)
									_serverPlayer.connection.teleport(((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX),
											((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY),
											((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ), _ent.getYRot(), _ent.getXRot());
							}
							if (entity instanceof Player _player && !_player.level.isClientSide())
								_player.displayClientMessage(Component.literal("Tu a \u00E9t\u00E9 tp at ton home."), false);
						} else if (((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeDimension).equals("Nether")) {
							if (entity instanceof ServerPlayer _player && !_player.level.isClientSide()) {
								ResourceKey<Level> destinationType = Level.NETHER;
								if (_player.level.dimension() == destinationType)
									return;
								ServerLevel nextLevel = _player.server.getLevel(destinationType);
								if (nextLevel != null) {
									_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
									_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
									_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
									for (MobEffectInstance _effectinstance : _player.getActiveEffects())
										_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance));
									_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
								}
							}
							{
								Entity _ent = entity;
								_ent.teleportTo(((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX),
										((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY),
										((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ));
								if (_ent instanceof ServerPlayer _serverPlayer)
									_serverPlayer.connection.teleport(((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX),
											((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY),
											((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ), _ent.getYRot(), _ent.getXRot());
							}
							if (entity instanceof Player _player && !_player.level.isClientSide())
								_player.displayClientMessage(Component.literal("Tu a \u00E9t\u00E9 tp at ton home."), false);
						} else if (((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeDimension).equals("End")) {
							if (entity instanceof ServerPlayer _player && !_player.level.isClientSide()) {
								ResourceKey<Level> destinationType = Level.END;
								if (_player.level.dimension() == destinationType)
									return;
								ServerLevel nextLevel = _player.server.getLevel(destinationType);
								if (nextLevel != null) {
									_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
									_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
									_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
									for (MobEffectInstance _effectinstance : _player.getActiveEffects())
										_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance));
									_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
								}
							}
							{
								Entity _ent = entity;
								_ent.teleportTo(((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX),
										((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY),
										((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ));
								if (_ent instanceof ServerPlayer _serverPlayer)
									_serverPlayer.connection.teleport(((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX),
											((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY),
											((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ), _ent.getYRot(), _ent.getXRot());
							}
							if (entity instanceof Player _player && !_player.level.isClientSide())
								_player.displayClientMessage(Component.literal("Tu a \u00E9t\u00E9 tp at ton home."), false);
						}
					} else {
						if (entity instanceof Player _player && !_player.level.isClientSide())
							_player.displayClientMessage(Component.literal("\u00A7cTu a boug\u00E9. R\u00E9-essaye."), false);
					}
				});
			} else {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("\u00A7cT'a vraiment voulu te tp a ton home alors que t'a pas set ton home?"), false);
			}
		} else if ((StringArgumentType.getString(arguments, "name")).equals("info")) {
			if ((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeHasBeenSet == true) {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("Ton home est \u00E0:"), false);
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal(("Dimension:" + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeDimension)), false);
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal(("X: " + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeX)), false);
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal(("Y: " + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeY)), false);
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal(("Z: " + (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).HomeZ)), false);
			} else {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("Tu n'a pas de home. "), false);
			}
		} else if ((StringArgumentType.getString(arguments, "name")).equals("help")) {
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("Les commandes sont:"), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("-set: met ton home a tes coordon\u00E9es"), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("-tp: te tp a ton home"), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("-info: te donne les coordon\u00E9es de ton home"), false);
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("-reset: reset ton home"), false);
		} else if ((StringArgumentType.getString(arguments, "name")).equals("reset")) {
			{
				boolean _setval = false;
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeHasBeenSet = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = 0;
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeX = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = 0;
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeY = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			{
				double _setval = 0;
				entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.HomeZ = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("Ton Home a \u00E9t\u00E9 reset."), false);
		} else {
			if (entity instanceof Player _player && !_player.level.isClientSide())
				_player.displayClientMessage(Component.literal("\u00A7cParam\u00E8tre inconnu. Texte rouge mena\u00E7ant."), false);
		}
	}
}
