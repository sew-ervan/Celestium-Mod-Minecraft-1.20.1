package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

/**
 * Types de degats propres au mod.
 *
 * <p>Depuis la 1.19.4 les types de degats sont des donnees et non plus des constantes Java. En
 * declarer un plutot que de recycler les degats magiques donne au joueur un message de mort qui
 * dit ce qui l'a tue.
 */
public final class ModDamageTypes {

	public static final ResourceKey<DamageType> CORRUPTION =
			ResourceKey.create(Registries.DAMAGE_TYPE, CelestiumMod.id("corruption"));

	private ModDamageTypes() {
	}

	public static void bootstrap(BootstapContext<DamageType> context) {
		// exhaustion nulle : la corruption ronge, elle n'affame pas.
		context.register(CORRUPTION, new DamageType(
				"corruption", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F, DamageEffects.HURT));
	}
}
