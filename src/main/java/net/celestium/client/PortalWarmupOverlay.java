package net.celestium.client;

import net.celestium.CelestiumMod;
import net.celestium.feature.portal.DemonPortalTravel;
import net.celestium.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ce qu'on voit pendant qu'on franchit le portail.
 *
 * <p>Sans retour visuel, l'attente de quatre secondes passe pour une panne : on entre, rien ne
 * bouge, on ressort. L'ecran se teinte donc de rouge a mesure que le passage approche, et la vue
 * se resserre — de quoi comprendre que quelque chose est en cours, et a quel point c'est avance.
 *
 * <p>Le compte est refait ici plutot que recu du serveur. Il n'y a rien a synchroniser : le client
 * connait la position du joueur et les blocs autour de lui, donc il sait tout ce qu'il faut pour
 * arriver au meme resultat. Un paquet par tick pour un nombre que les deux cotes peuvent deduire
 * seuls serait du gaspillage.
 *
 * <p>Le compteur ne retombe pas d'un coup quand on sort : il redescend plus vite qu'il n'est monte,
 * ce qui evite un a-coup brutal si l'on frole le bord du portail pendant un tick.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID, value = Dist.CLIENT)
public final class PortalWarmupOverlay {

	/** Vitesse de retour a zero une fois sorti du portail, en crans par tick. */
	private static final int FADE_PER_TICK = 4;

	/** Opacite maximale de la teinte, juste avant le passage. */
	private static final float MAX_OPACITY = 0.72F;

	/** Resserrement maximal du champ de vision, en proportion. */
	private static final float MAX_FOV_PULL = 0.25F;

	/** Rouge sombre des Terres du demon, sans composante alpha. */
	private static final int TINT = 0x6B0A0E;

	private static int progress;

	private PortalWarmupOverlay() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			progress = 0;
			return;
		}

		if (insidePortal(player)) {
			if (progress == 0) {
				// Le declic d'entree, joue une seule fois : c'est lui qui annonce que le compte part.
				player.playSound(SoundEvents.PORTAL_TRIGGER, 0.4F, 1.4F);
			}
			progress = Math.min(progress + 1, DemonPortalTravel.WARMUP_TICKS);
		} else {
			progress = Math.max(progress - FADE_PER_TICK, 0);
		}
	}

	/** Vrai si le joueur touche au moins un bloc de portail. */
	private static boolean insidePortal(LocalPlayer player) {
		return BlockPos.betweenClosedStream(player.getBoundingBox().deflate(0.001))
				.anyMatch(pos -> player.level().getBlockState(pos).is(ModBlocks.DEMON_PORTAL.get()));
	}

	/** La vue se resserre a mesure que le passage approche. */
	@SubscribeEvent
	public static void onComputeFov(ComputeFovModifierEvent event) {
		float ratio = ratio();
		if (ratio > 0.0F) {
			event.setNewFovModifier(event.getNewFovModifier() * (1.0F - MAX_FOV_PULL * ratio));
		}
	}

	/** La teinte, posee par-dessus tout le reste de l'interface. */
	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		float ratio = ratio();
		if (ratio <= 0.0F) {
			return;
		}

		GuiGraphics graphics = event.getGuiGraphics();
		int alpha = Mth.ceil(MAX_OPACITY * ratio * 255.0F) << 24;

		graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), alpha | TINT);
	}

	/**
	 * Avancement du passage, entre zero et un.
	 *
	 * <p>Eleve au carre : la teinte reste discrete pendant la premiere moitie de l'attente et se
	 * referme surtout sur la fin, la ou il devient utile de savoir que le passage est imminent.
	 */
	private static float ratio() {
		float linear = (float) progress / DemonPortalTravel.WARMUP_TICKS;
		return linear * linear;
	}
}
