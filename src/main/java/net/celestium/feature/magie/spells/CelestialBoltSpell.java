package net.celestium.feature.magie.spells;

import net.celestium.CelestiumMod;
import net.celestium.feature.magie.Faction;
import net.celestium.feature.magie.Spell;
import net.celestium.feature.magie.entity.CelestialBoltEntity;
import net.celestium.init.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/**
 * Invoque un eclair celeste au-dessus du lanceur, qui part chercher les joueurs hostiles.
 *
 * <p>Reserve au camp celeste, ce que le mod d'origine n'imposait pas : n'importe qui pouvait
 * declencher l'invocation, l'orbe se contentant ensuite d'ignorer les cibles du bon camp.
 */
public class CelestialBoltSpell implements Spell {

	private static final ResourceLocation ID = CelestiumMod.id("celestial_bolt");

	private static final int MANA_COST = 35;
	private static final int COOLDOWN_TICKS = 200;

	/** Hauteur d'apparition au-dessus des yeux du lanceur. */
	private static final double SPAWN_HEIGHT = 2.6;

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public int manaCost() {
		return MANA_COST;
	}

	@Override
	public int cooldownTicks() {
		return COOLDOWN_TICKS;
	}

	@Override
	public Faction requiredFaction() {
		return Faction.CELESTE;
	}

	@Override
	public boolean cast(ServerPlayer caster, ServerLevel level) {
		CelestialBoltEntity bolt = ModEntities.CELESTIAL_BOLT.get().create(level);
		if (bolt == null) {
			return false;
		}

		Vec3 origin = caster.position().add(0.0, SPAWN_HEIGHT, 0.0);
		bolt.setPos(origin);
		bolt.setOwner(caster);
		bolt.setDeltaMovement(0.0, 0.1, 0.0);

		level.addFreshEntity(bolt);
		level.playSound(null, caster.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.7F, 1.6F);
		return true;
	}
}
