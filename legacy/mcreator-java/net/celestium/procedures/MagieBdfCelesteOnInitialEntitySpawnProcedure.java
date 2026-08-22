package net.celestium.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.celestium.network.CelestiumModVariables;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;

public class MagieBdfCelesteOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof Player && ((entityiterator.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).magie_equipe == 0
						|| (entityiterator.getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).magie_equipe == -1)) {
					if (entity instanceof Mob _entity && entityiterator instanceof LivingEntity _ent)
						_entity.setTarget(_ent);
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, new BlockPos(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.break")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.break")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					break;
				}
			}
		}
	}
}
