package net.celestium.feature.enchant;

import net.celestium.CelestiumMod;
import net.celestium.init.ModEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ce que les quatre enchantements d'arc font, au moment ou ils le font.
 *
 * <p>Trois d'entre eux agissent au depart de la fleche, un a son arrivee. Les trois premiers
 * partagent donc le meme point d'accroche — l'instant ou la fleche entre dans le monde — et l'ordre
 * dans lequel ils s'y appliquent n'est pas indifferent : le Traqueur corrige d'abord la trajectoire,
 * la Salve celeste s'ecarte ensuite de la trajectoire corrigee. L'inverse ferait converger toute la
 * salve sur une seule cible, ce qui reviendrait a n'avoir tire qu'une fleche.
 *
 * <p>L'enchantement est lu sur l'arc au moment du tir, et non sur la fleche : c'est l'arc qui le
 * porte. Ce qui doit survivre au vol — l'Effondrement, qui ne se declenche qu'a l'impact — est
 * recopie sur la fleche elle-meme, car l'arc peut avoir change de main entre-temps.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class BowEnchantments {

	/** Marque des fleches nees d'une salve : elles ne doivent pas en declencher une a leur tour. */
	private static final String VOLLEY_MARK = "CelestiumVolleyArrow";

	/** Marque d'une fleche qui portera un effondrement la ou elle se plante. */
	private static final String COLLAPSE_MARK = "CelestiumCollapse";

	private BowEnchantments() {
	}

	/**
	 * Le depart de la fleche.
	 *
	 * <p>Le test sur l'age de la fleche ecarte celles qui reviennent d'une sauvegarde : une fleche
	 * en vol au moment ou le monde s'arrete repasse par cet evenement au chargement suivant, et
	 * sans ce garde-fou elle relancerait sa salve a chaque redemarrage.
	 */
	@SubscribeEvent
	public static void onArrowShot(EntityJoinLevelEvent event) {
		if (!(event.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if (!(event.getEntity() instanceof AbstractArrow arrow) || arrow.tickCount > 0) {
			return;
		}
		if (arrow.getPersistentData().getBoolean(VOLLEY_MARK)) {
			return;
		}
		if (!(arrow.getOwner() instanceof LivingEntity shooter)) {
			return;
		}

		ItemStack bow = bowOf(shooter);
		if (bow.isEmpty()) {
			return;
		}

		int pierce = levelOn(ModEnchantments.PIERCING_SHOT.get(), bow);
		if (pierce > arrow.getPierceLevel()) {
			arrow.setPierceLevel((byte) pierce);
		}

		if (levelOn(ModEnchantments.COLLAPSE.get(), bow) > 0) {
			arrow.getPersistentData().putBoolean(COLLAPSE_MARK, true);
		}

		int seeker = levelOn(ModEnchantments.SEEKER.get(), bow);
		if (seeker > 0) {
			aim(arrow, shooter, seeker);
		}

		int extra = VolleyEnchantment.extraArrows(levelOn(ModEnchantments.VOLLEY.get(), bow));
		if (extra > 0) {
			volley(arrow, shooter, extra, level);
		}
	}

	/**
	 * Corrige la trajectoire vers la creature la mieux alignee.
	 *
	 * <p>La mieux alignee, et non la plus proche : une creature a deux pas mais a quarante degres
	 * n'est pas celle qu'on visait, alors qu'une creature loin droit devant l'est.
	 */
	private static void aim(AbstractArrow arrow, LivingEntity shooter, int level) {
		Vec3 flight = arrow.getDeltaMovement();
		double speed = flight.length();
		if (speed < 1.0E-4) {
			return;
		}

		Vec3 heading = flight.scale(1.0 / speed);
		double reach = SeekerEnchantment.reachFor(level);
		double narrowest = Math.cos(Math.toRadians(SeekerEnchantment.coneFor(level)));

		LivingEntity chosen = null;
		double bestAlignment = narrowest;

		for (LivingEntity candidate : arrow.level().getEntitiesOfClass(
				LivingEntity.class, arrow.getBoundingBox().inflate(reach))) {

			if (candidate == shooter || candidate instanceof Player || !candidate.isAlive()) {
				continue;
			}

			Vec3 toTarget = candidate.getEyePosition().subtract(arrow.position());
			double distance = toTarget.length();
			if (distance < 1.0E-4 || distance > reach || !shooter.hasLineOfSight(candidate)) {
				continue;
			}

			double alignment = toTarget.scale(1.0 / distance).dot(heading);
			if (alignment > bestAlignment) {
				bestAlignment = alignment;
				chosen = candidate;
			}
		}

		if (chosen == null) {
			return;
		}

		Vec3 corrected = chosen.getEyePosition().subtract(arrow.position()).normalize().scale(speed);
		arrow.setDeltaMovement(corrected);
		orient(arrow, corrected);
	}

	/** Tire les fleches supplementaires, ecartees de part et d'autre de la premiere. */
	private static void volley(AbstractArrow arrow, LivingEntity shooter, int extra, ServerLevel level) {
		Vec3 flight = arrow.getDeltaMovement();

		for (int i = 0; i < extra; i++) {
			AbstractArrow copy = (AbstractArrow) arrow.getType().create(level);
			if (copy == null) {
				return;
			}

			// La fleche ajoutee est une copie de celle qu'on a tiree : memes degats, meme effet si
			// la fleche etait une fleche d'effet. Une fleche ordinaire tiree a la place aurait fait
			// de la salve une facon de diluer les fleches speciales.
			copy.restoreFrom(arrow);
			copy.setUUID(Mth.createInsecureUUID(level.getRandom()));
			copy.setOwner(shooter);
			copy.getPersistentData().putBoolean(VOLLEY_MARK, true);

			// Rien de ce que la salve ajoute ne se ramasse : sans cela, une fleche tiree en
			// rendrait quatre.
			copy.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			copy.setPos(arrow.getX(), arrow.getY(), arrow.getZ());

			// Les ecarts alternent d'un cote et de l'autre, en s'eloignant : la salve reste centree
			// sur la ou l'on visait.
			double degrees = VolleyEnchantment.SPREAD * (i / 2 + 1) * (i % 2 == 0 ? 1 : -1);
			Vec3 aside = turn(flight, degrees);

			copy.setDeltaMovement(aside);
			orient(copy, aside);

			level.addFreshEntity(copy);
		}
	}

	/**
	 * L'arrivee de la fleche, pour l'Effondrement.
	 *
	 * <p>La marque est effacee au premier impact. Une fleche transpercante en touche plusieurs, et
	 * un effondrement par creature traversee irait bien au-dela de ce que l'enchantement annonce.
	 */
	@SubscribeEvent
	public static void onArrowLands(ProjectileImpactEvent event) {
		if (!(event.getProjectile() instanceof AbstractArrow arrow)) {
			return;
		}
		if (!(arrow.level() instanceof ServerLevel level)) {
			return;
		}
		if (!arrow.getPersistentData().getBoolean(COLLAPSE_MARK)) {
			return;
		}

		arrow.getPersistentData().putBoolean(COLLAPSE_MARK, false);

		Vec3 centre = event.getRayTraceResult().getLocation();
		double radius = CollapseEnchantment.RADIUS;

		for (LivingEntity caught : level.getEntitiesOfClass(
				LivingEntity.class, new AABB(centre, centre).inflate(radius))) {

			if (caught == arrow.getOwner()) {
				continue;
			}

			Vec3 toCentre = centre.subtract(caught.position());
			double distance = toCentre.length();
			if (distance < 0.1 || distance > radius) {
				continue;
			}

			double strength = CollapseEnchantment.PULL * (1.0 - distance / radius);
			caught.setDeltaMovement(caught.getDeltaMovement()
					.add(toCentre.scale(1.0 / distance).scale(strength)));

			// Sans cette marque, un joueur tire ne bougerait que sur le serveur : c'est elle qui
			// declenche l'envoi de sa nouvelle vitesse.
			caught.hurtMarked = true;
		}

		level.sendParticles(ParticleTypes.PORTAL, centre.x, centre.y, centre.z, 30, 0.6, 0.6, 0.6, 0.4);
		level.playSound(null, centre.x, centre.y, centre.z,
				SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.5F);
	}

	/** Fait tourner une trajectoire autour de la verticale. */
	private static Vec3 turn(Vec3 flight, double degrees) {
		double radians = Math.toRadians(degrees);
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);

		return new Vec3(flight.x * cos - flight.z * sin, flight.y, flight.x * sin + flight.z * cos);
	}

	/** Aligne la fleche sur sa trajectoire, faute de quoi elle volerait de travers. */
	private static void orient(AbstractArrow arrow, Vec3 flight) {
		arrow.setYRot((float) (Mth.atan2(flight.x, flight.z) * (180.0 / Math.PI)));
		arrow.setXRot((float) (Mth.atan2(flight.y, flight.horizontalDistance()) * (180.0 / Math.PI)));
		arrow.yRotO = arrow.getYRot();
		arrow.xRotO = arrow.getXRot();
	}

	/**
	 * L'arc d'ou part la fleche.
	 *
	 * <p>L'objet en cours d'usage d'abord, car c'est celui qu'on vient de relacher ; les deux mains
	 * ensuite, au cas ou le tir ne viendrait pas d'un joueur.
	 */
	private static ItemStack bowOf(LivingEntity shooter) {
		ItemStack using = shooter.getUseItem();
		if (using.getItem() instanceof BowItem) {
			return using;
		}

		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = shooter.getItemInHand(hand);
			if (held.getItem() instanceof BowItem) {
				return held;
			}
		}

		return ItemStack.EMPTY;
	}

	private static int levelOn(Enchantment enchantment, ItemStack bow) {
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, bow);
	}
}
