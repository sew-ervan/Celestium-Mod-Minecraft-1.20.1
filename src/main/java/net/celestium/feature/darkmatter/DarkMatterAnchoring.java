package net.celestium.feature.darkmatter;

import net.celestium.CelestiumMod;
import net.celestium.core.material.ModArmorMaterials;
import net.celestium.feature.celestium.ModArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ce que la parure complete de matiere noire ajoute : on ne se blesse plus en tombant.
 *
 * <p>La matiere noire n'a pas de poids au sens ou l'on tombe avec — elle a de la masse sans avoir
 * de prise. Une chute qui ne fait plus mal en est la traduction la plus simple, et elle complete la
 * resistance a la poussee que porte deja le materiau : rien ne vous deplace, et rien ne vous casse
 * en arrivant.
 *
 * <p>Il faut les quatre pieces. Une seule ne suffit pas, sans quoi les bottes seules rendraient
 * inutile tout ce que le jeu a prevu contre les chutes.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class DarkMatterAnchoring {

	private static final int FULL_SET = 4;

	private DarkMatterAnchoring() {
	}

	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}
		if (worn(player) < FULL_SET) {
			return;
		}

		event.setCanceled(true);
	}

	/** Nombre de pieces de matiere noire portees. */
	public static int worn(Player player) {
		int count = 0;
		for (ItemStack stack : player.getArmorSlots()) {
			if (stack.getItem() instanceof ModArmorItem armor
					&& armor.getArmorMaterial() == ModArmorMaterials.DARK_MATTER) {
				count++;
			}
		}
		return count;
	}
}
