package net.celestium.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSourceStack;

import net.celestium.network.CelestiumModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class SetRtpRayonProcedureProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		CelestiumModVariables.MapVariables.get(world).RtpRayon = DoubleArgumentType.getDouble(arguments, "rayon");
		CelestiumModVariables.MapVariables.get(world).syncData(world);
	}
}
