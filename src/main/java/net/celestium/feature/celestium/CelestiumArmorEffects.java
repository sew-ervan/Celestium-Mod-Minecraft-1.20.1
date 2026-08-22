package net.celestium.feature.celestium;

import net.celestium.CelestiumMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Effets accordes par chaque piece de l'armure en Celestium.
 *
 * <p>Les quatre procedures generees par MCreator faisaient la meme chose dans quatre fichiers.
 * Trois d'entre elles reappliquaient leur effet a chaque tick, soit vingt fois par seconde pour un
 * effet de trois secondes. Seule celle du casque utilisait le bon reflexe : ne rafraichir que
 * lorsque la duree restante descend sous un seuil, ce qui evite au passage le clignotement de
 * l'ecran propre a la vision nocturne. Ce reflexe est ici applique aux quatre pieces.
 *
 * <p>Le parcours des pieces portees se fait sur un evenement de tick joueur plutot que par le
 * crochet {@code onArmorTick} de Forge, deprecie et marque pour suppression en 1.20.1.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class CelestiumArmorEffects {

	private static final int NIGHT_VISION_DURATION = 1000;
	private static final int SHORT_DURATION = 60;

	private CelestiumArmorEffects() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
			return;
		}

		for (ItemStack stack : event.player.getArmorSlots()) {
			if (stack.getItem() instanceof CelestiumArmorItem armor) {
				applyFor(armor.getType(), event.player);
			}
		}
	}

	/** Applique l'effet de la piece portee. Cote serveur uniquement. */
	public static void applyFor(ArmorItem.Type type, LivingEntity wearer) {
		switch (type) {
			case HELMET -> refresh(wearer, MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION, 1, true);
			case CHESTPLATE -> refresh(wearer, MobEffects.FIRE_RESISTANCE, SHORT_DURATION, 1, true);
			case LEGGINGS -> refresh(wearer, MobEffects.MOVEMENT_SPEED, SHORT_DURATION, 2, true);
			case BOOTS -> refresh(wearer, MobEffects.JUMP, SHORT_DURATION, 2, false);
		}
	}

	/**
	 * Reapplique l'effet seulement lorsqu'il approche de son terme.
	 *
	 * <p>Le seuil est fixe au tiers de la duree : assez tot pour que l'effet ne s'interrompe jamais
	 * tant que la piece est portee, assez tard pour ne pas reconstruire l'instance a chaque tick.
	 */
	private static void refresh(LivingEntity wearer, MobEffect effect, int duration, int amplifier, boolean ambient) {
		MobEffectInstance current = wearer.getEffect(effect);
		if (current != null && current.getAmplifier() >= amplifier && current.getDuration() > duration / 3) {
			return;
		}
		wearer.addEffect(new MobEffectInstance(effect, duration, amplifier, ambient, false));
	}
}
