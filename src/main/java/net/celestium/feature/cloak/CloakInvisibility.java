package net.celestium.feature.cloak;

import net.celestium.CelestiumMod;
import net.celestium.init.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ce que la cape fait porter a qui la porte.
 *
 * <p>Elle n'applique pas l'effet d'invisibilite : elle leve directement le drapeau que cet effet
 * lui-meme finit par lever. C'est toute la difference demandee — les volutes grises que le jeu
 * dessine autour d'une creature sous potion viennent de l'effet, pas du drapeau. Sans effet, pas de
 * volutes, et le reste continue comme avant : la poussiere sous les pas d'un joueur qui court, les
 * gerbes d'eau, les etincelles d'une chute. On disparait sans cesser de laisser des traces.
 *
 * <p>Le drapeau est repose a chaque tick parce que le jeu l'efface a chaque tick : la methode qui
 * l'entretient le remet a « invisible si et seulement si l'effet de potion est actif ». Il faut
 * donc passer apres elle, ce que garantit la phase de fin du tick du joueur.
 *
 * <p>Les deux cotes font le meme calcul. Ne le faire que sur le serveur ne suffirait pas : le
 * client rejouerait la remise a zero dans son coin, et comme la valeur cote serveur n'aurait pas
 * change, rien ne serait renvoye pour le corriger. Le porteur clignoterait.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class CloakInvisibility {

	private CloakInvisibility() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!worn(event.player)) {
			// Rien a defaire : la methode d'entretien du jeu remet le drapeau a sa valeur normale
			// des le tick suivant, donc retirer la cape suffit a redevenir visible.
			return;
		}

		event.player.setInvisible(true);
	}

	/** Vrai si la creature porte la cape sur le torse. */
	public static boolean worn(LivingEntity entity) {
		return entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.INVISIBILITY_CLOAK.get());
	}
}
