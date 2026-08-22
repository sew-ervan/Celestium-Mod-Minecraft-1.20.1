package net.celestium.feature.magie.entity;

import net.celestium.feature.magie.Faction;
import net.celestium.server.data.ModCapabilities;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

/**
 * Eclair celeste : orbe lente qui poursuit les joueurs hostiles au camp celeste.
 *
 * <p>Le mod d'origine en faisait une creature de type {@code Monster}, dotee d'une intelligence
 * artificielle complete et de quatre procedures separees pour choisir sa cible, verifier qu'elle
 * etait attaquable et lui infliger des degats a la collision. Un projectile suffit, et le camp de
 * la cible se lit en un point unique.
 */
public class CelestialBoltEntity extends Projectile implements GeoEntity {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.BdfCeleste.idle");

	/** Duree de vie en ticks : l'orbe se dissipe au bout de quinze secondes. */
	private static final int MAX_LIFETIME = 300;

	/** Rayon de recherche d'une cible, en blocs. */
	private static final double SEEK_RADIUS = 24.0;

	/** Fraction de la trajectoire corrigee a chaque tick vers la cible. */
	private static final double STEERING = 0.08;

	private static final double SPEED = 0.35;
	private static final float DAMAGE = 6.0F;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public CelestialBoltEntity(EntityType<? extends CelestialBoltEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			return;
		}

		if (this.tickCount > MAX_LIFETIME) {
			this.discard();
			return;
		}

		LivingEntity target = findTarget();
		Vec3 motion = this.getDeltaMovement();

		if (target != null) {
			Vec3 desired = target.getEyePosition().subtract(this.position()).normalize().scale(SPEED);
			motion = motion.add(desired.subtract(motion).scale(STEERING));
		}

		this.setDeltaMovement(motion);
		this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);

		checkCollision();
	}

	/** Cible le joueur hostile le plus proche. */
	@Nullable
	private LivingEntity findTarget() {
		AABB search = this.getBoundingBox().inflate(SEEK_RADIUS);
		List<Player> candidates = this.level().getEntitiesOfClass(Player.class, search, this::isHostile);
		return candidates.stream()
				.min(Comparator.comparingDouble(this::distanceToSqr))
				.orElse(null);
	}

	/** Un joueur du camp celeste est ignore : c'est le sens de l'aura celeste. */
	private boolean isHostile(Player player) {
		if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
			return false;
		}
		Faction faction = ModCapabilities.of(player).getFaction();
		return faction.isTargetedByCelestialMagic();
	}

	private void checkCollision() {
		AABB reach = this.getBoundingBox().inflate(0.3);
		this.level().getEntitiesOfClass(Player.class, reach, this::isHostile).stream()
				.findFirst()
				.ifPresent(player -> onHitEntity(new EntityHitResult(player)));
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (this.level().isClientSide()) {
			return;
		}
		result.getEntity().hurt(this.damageSources().magic(), DAMAGE);
		this.discard();
	}

	@Override
	protected void defineSynchedData() {
		// L'orbe n'a aucun etat a synchroniser : son animation boucle sans condition.
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "flottement", 0, state -> state.setAndContinue(IDLE)));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
