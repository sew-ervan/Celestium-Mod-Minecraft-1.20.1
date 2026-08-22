package net.celestium.procedures;

import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.celestium.network.CelestiumModVariables;

public class SetServerSpawnProcedureProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		CelestiumModVariables.MapVariables.get(world).SpawnX = x;
		CelestiumModVariables.MapVariables.get(world).syncData(world);
		CelestiumModVariables.MapVariables.get(world).SpawnY = y;
		CelestiumModVariables.MapVariables.get(world).syncData(world);
		CelestiumModVariables.MapVariables.get(world).SpawnZ = z;
		CelestiumModVariables.MapVariables.get(world).syncData(world);
		if (world.getLevelData() instanceof WritableLevelData _levelData)
			_levelData.setSpawn(new BlockPos(x, y, z), 0);
		CelestiumModVariables.MapVariables.get(world).SpawnHasBeenSet = true;
		CelestiumModVariables.MapVariables.get(world).syncData(world);
		if (entity instanceof Player _player && !_player.level.isClientSide())
			_player.displayClientMessage(Component.literal("Le spawn a bien \u00E9t\u00E9 set!"), false);
	}
}
