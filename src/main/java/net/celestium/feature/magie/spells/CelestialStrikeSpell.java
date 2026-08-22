package net.celestium.feature.magie.spells;

import net.celestium.CelestiumMod;
import net.celestium.feature.magie.Faction;
import net.celestium.feature.magie.Spell;
import net.celestium.server.data.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Frappe celeste : inflige des degats magiques a la creature visee.
 *
 * <p>La version d'origine faisait trois choses fausses. Elle recalculait le rayon de visee — un
 * appel a {@code level.clip}, l'operation la plus couteuse du lot — plus de trente fois par
 * iteration, sur quinze iterations, soit plusieurs centaines de rayons par clic droit. Elle
 * infligeait des degats egaux a {@code getMaxDamage()} de l'objet tenu, c'est-a-dire sa durabilite
 * maximale : une epee en Celestium, qui encaisse cinq mille coups, tuait tout d'un seul geste.
 * Enfin, elle lisait la victime a l'indice 1 d'une liste qui n'en contenait souvent qu'une, ce qui
 * levait une exception.
 */
public class CelestialStrikeSpell implements Spell {

	private static final ResourceLocation ID = CelestiumMod.id("celestial_strike");

	/** Portee en blocs. */
	private static final double RANGE = 15.0;

	private static final float DAMAGE = 12.0F;
	private static final int MANA_COST = 20;
	private static final int COOLDOWN_TICKS = 100;

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
	public boolean cast(ServerPlayer caster, ServerLevel level) {
		LivingEntity target = findTarget(caster, level);

		if (target == null) {
			caster.displayClientMessage(Component.translatable("message.celestium.spell.no_target"), true);
			return false;
		}

		target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
		level.playSound(null, target.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
		return true;
	}

	/**
	 * Trouve la premiere creature visee, sans traverser les blocs.
	 *
	 * <p>Un seul rayon suffit : sa portee est d'abord raccourcie au premier bloc rencontre, puis
	 * les entites sont cherchees le long du segment restant.
	 */
	@Nullable
	private LivingEntity findTarget(ServerPlayer caster, ServerLevel level) {
		Vec3 eye = caster.getEyePosition();
		Vec3 direction = caster.getViewVector(1.0F);
		Vec3 far = eye.add(direction.scale(RANGE));

		// Le rayon s'arrete au premier bloc solide : on ne frappe pas a travers un mur.
		Vec3 end = level.clip(new ClipContext(eye, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster))
				.getLocation();

		AABB search = caster.getBoundingBox().expandTowards(direction.scale(RANGE)).inflate(1.0);
		EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, caster, eye, end, search,
				entity -> isValidTarget(caster, entity));

		return hit != null && hit.getEntity() instanceof LivingEntity living ? living : null;
	}

	/** Un lanceur ne se frappe pas lui-meme, ni un joueur de son propre camp. */
	private boolean isValidTarget(ServerPlayer caster, Entity candidate) {
		if (candidate == caster || !candidate.isAlive() || !(candidate instanceof LivingEntity)) {
			return false;
		}
		if (candidate instanceof Player targetPlayer) {
			Faction casterFaction = ModCapabilities.of(caster).getFaction();
			Faction targetFaction = ModCapabilities.of(targetPlayer).getFaction();
			return casterFaction != targetFaction;
		}
		return true;
	}
}
