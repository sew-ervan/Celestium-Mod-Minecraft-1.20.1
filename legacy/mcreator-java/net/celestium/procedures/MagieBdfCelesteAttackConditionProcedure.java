package net.celestium.procedures;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

import net.celestium.network.CelestiumModVariables;

public class MagieBdfCelesteAttackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getCapability(CelestiumModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CelestiumModVariables.PlayerVariables())).magie_equipe < 1) {
			return true;
		}
		return false;
	}
}
