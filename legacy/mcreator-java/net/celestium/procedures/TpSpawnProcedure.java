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

import net.celestium.network.CelestiumModVariables;
import net.celestium.CelestiumMod;

public class TpSpawnProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double CoX = 0;
		double CoY = 0;
		double CoZ = 0;
		{
			double _setval = Math.round(x);
			entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.TempPlayer1 = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
		{
			double _setval = Math.round(y);
			entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.TempPlayer2 = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
		{
			double _setval = Math.round(z);
			entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.TempPlayer3 = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
		if (entity instanceof Player _player && !_player.level.isClientSide())
			_player.displayClientMessage(Component.literal("Tu sera t\u00E9l\u00E9port\u00E9 dans 5 secondes, ne bouge pas."), false);
		CelestiumMod.queueServerWork(100, () -> {
			if ((entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer1 == Math.round(entity.getX())
					&& (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer2 == Math.round(entity.getY())
					&& (entity.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).TempPlayer3 == Math.round(entity.getZ())
					&& CelestiumModVariables.MapVariables.get(world).SpawnHasBeenSet == true) {
				if (!((entity.level.dimension()) == Level.OVERWORLD)) {
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
				}
				{
					Entity _ent = entity;
					_ent.teleportTo(CelestiumModVariables.MapVariables.get(world).SpawnX, CelestiumModVariables.MapVariables.get(world).SpawnY, CelestiumModVariables.MapVariables.get(world).SpawnZ);
					if (_ent instanceof ServerPlayer _serverPlayer)
						_serverPlayer.connection.teleport(CelestiumModVariables.MapVariables.get(world).SpawnX, CelestiumModVariables.MapVariables.get(world).SpawnY, CelestiumModVariables.MapVariables.get(world).SpawnZ, _ent.getYRot(),
								_ent.getXRot());
				}
			} else if (CelestiumModVariables.MapVariables.get(world).SpawnHasBeenSet == false) {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("\u00A7cLe spawn n'a pas encore \u00E9t\u00E9 d\u00E9fini, harc\u00E8le un admin pour qu'il le fasse."), false);
			} else {
				if (entity instanceof Player _player && !_player.level.isClientSide())
					_player.displayClientMessage(Component.literal("\u00A7cTu a boug\u00E9. R\u00E9-essaye."), false);
			}
		});
	}
}
