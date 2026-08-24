package net.celestium.feature.mount;

import net.celestium.CelestiumMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * La seconde place, du montage a la position assise.
 *
 * <p>Le jeu de base n'autorise qu'un passager par monture : {@code canAddPassenger} refuse le
 * second, et la methode qui place les passagers les pose tous au meme endroit. Aucun de ces deux
 * points n'est ouvert a l'extension, mais aucun n'est infranchissable.
 *
 * <p>Pour le premier, monter de force contourne le refus — c'est exactement ce que fait le jeu
 * lui-meme quand il recharge une monture et ses passagers depuis une sauvegarde. Pour le second, le
 * passager de derriere est repositionne apres coup, une fois par tick, a la fin du tick du monde :
 * a ce moment la, la monture a fini de bouger et de placer ses passagers, et rien ne viendra plus
 * defaire le calcul.
 *
 * <p>Les deux cotes font le meme calcul, a partir des memes donnees — la position de la monture et
 * l'orientation de son corps. Il n'y a donc rien a transmettre : aucun paquet n'est ajoute pour
 * cette place.
 *
 * <p>Si le conducteur descend alors que quelqu'un est assis derriere, celui de derriere devient le
 * premier passager et prend les renes. C'est la consequence directe de la regle du jeu — c'est le
 * premier passager qui conduit — et elle tombe juste.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class TandemRiding {

	/**
	 * Marque portee par la monture elle-meme.
	 *
	 * <p>Elle vit dans les donnees libres que Forge attache a chaque entite, donc elle est
	 * sauvegardee avec la monture et la suit partout. Le nom est prefixe : ces donnees sont
	 * partagees par tous les mods installes.
	 */
	private static final String TAG = "CelestiumTandemSaddle";

	/** Recul du second siege, en blocs, mesure depuis le premier. */
	private static final double PILLION = 0.55;

	private TandemRiding() {
	}

	/** Vrai si la monture porte une selle deux places. */
	public static boolean fitted(Entity mount) {
		return mount.getPersistentData().getBoolean(TAG);
	}

	/** Pose la selle deux places. */
	public static void fit(Entity mount) {
		mount.getPersistentData().putBoolean(TAG, true);
	}

	/**
	 * Une monture peut recevoir la selle si elle en porte deja une.
	 *
	 * <p>Deux places supposent une premiere : la selle du mod ne remplace pas celle du jeu, elle
	 * s'ajoute derriere. Cela vaut pour tout ce que le jeu declare sellable — chevaux, anes, mulets,
	 * cochons, striders — sans qu'aucune liste soit a tenir a jour.
	 */
	public static boolean canFit(Entity mount) {
		return mount instanceof Saddleable saddleable && saddleable.isSaddled() && !fitted(mount);
	}

	/**
	 * Le clic droit d'un second cavalier sur une monture deja occupee.
	 *
	 * <p>Sans selle deux places, ce geste ne fait rien du tout dans le jeu de base : l'intercepter
	 * ne prive donc personne de quoi que ce soit.
	 */
	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getHand() != InteractionHand.MAIN_HAND) {
			return;
		}

		Player player = event.getEntity();
		Entity mount = event.getTarget();

		if (player.isPassenger() || player.isSecondaryUseActive() || !event.getItemStack().isEmpty()) {
			return;
		}
		if (!fitted(mount) || mount.getPassengers().size() != 1) {
			return;
		}

		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));

		if (!player.level().isClientSide()) {
			player.startRiding(mount, true);
		}
	}

	/**
	 * Replace ceux qui sont assis derriere.
	 *
	 * <p>Le parcours se fait sur les joueurs du monde, qui sont peu nombreux, et non sur les
	 * entites : chercher les montures parmi elles couterait un balayage complet a chaque tick pour
	 * un resultat presque toujours vide.
	 */
	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		for (Player player : event.level.players()) {
			Entity mount = player.getVehicle();
			if (mount == null || !fitted(mount)) {
				continue;
			}

			List<Entity> riders = mount.getPassengers();
			if (riders.indexOf(player) < 1) {
				continue;
			}

			seat(mount, player);
		}
	}

	/**
	 * Ou se trouve le second siege.
	 *
	 * <p>Isole du deplacement lui-meme parce que c'est la seule chose a verifier : le reste n'est
	 * qu'un appel a la methode qui pose une entite quelque part.
	 */
	public static Vec3 pillionSeat(Entity mount, Entity rider) {
		double radians = Math.toRadians(bodyYaw(mount));

		// La direction du regard vaut (-sin, cos) ; le siege arriere est a l'oppose.
		return new Vec3(
				mount.getX() + Math.sin(radians) * PILLION,
				mount.getY() + mount.getPassengersRidingOffset() + rider.getMyRidingOffset(),
				mount.getZ() - Math.cos(radians) * PILLION);
	}

	/** Assied un passager derriere le conducteur, dans l'axe du corps de la monture. */
	private static void seat(Entity mount, Entity rider) {
		Vec3 at = pillionSeat(mount, rider);
		rider.setPos(at.x, at.y, at.z);

		if (rider instanceof LivingEntity living) {
			// Le jeu aligne le corps du cavalier sur celui de la monture ; le passager suit la meme
			// regle, sans quoi il resterait assis de travers.
			living.yBodyRot = bodyYaw(mount);
		}
	}

	/** L'orientation du corps de la monture, qui n'est pas celle de sa tete. */
	private static float bodyYaw(Entity mount) {
		return mount instanceof LivingEntity living ? living.yBodyRot : mount.getYRot();
	}
}
