package net.celestium.feature.celestium;

import net.celestium.CelestiumMod;
import net.celestium.core.material.ModArmorMaterials;
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
 * Effets accordes par chaque piece d'armure portee.
 *
 * <p>Les quatre procedures generees par MCreator faisaient la meme chose dans quatre fichiers.
 * Trois d'entre elles reappliquaient leur effet a chaque tick, soit vingt fois par seconde pour un
 * effet de trois secondes. Seule celle du casque utilisait le bon reflexe : ne rafraichir que
 * lorsque la duree restante descend sous un seuil, ce qui evite au passage le clignotement de
 * l'ecran propre a la vision nocturne. Ce reflexe vaut ici pour toutes les pieces.
 *
 * <p>Les deux parures ont des roles distincts. Le Celestium sert l'exploration : voir dans le noir,
 * se deplacer vite, sauter haut, ne pas bruler. Le Demonium sert le combat : frapper plus fort,
 * encaisser davantage, creuser plus vite.
 *
 * <p>Le parcours des pieces portees se fait sur un evenement de tick joueur plutot que par le
 * crochet {@code onArmorTick} de Forge, deprecie et marque pour suppression en 1.20.1.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class ArmorSetEffects {

	private static final int LONG_DURATION = 1000;
	private static final int SHORT_DURATION = 60;

	private ArmorSetEffects() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
			return;
		}

		for (ItemStack stack : event.player.getArmorSlots()) {
			if (stack.getItem() instanceof ModArmorItem armor) {
				applyFor(armor.getArmorMaterial(), armor.getType(), event.player);
			}
		}
	}

	/** Applique l'effet de la piece portee. Cote serveur uniquement. */
	public static void applyFor(ModArmorMaterials material, ArmorItem.Type type, LivingEntity wearer) {
		switch (material) {
			case CELESTIUM -> celestium(type, wearer);
			case CORRUPTED_CELESTIUM -> corruptedCelestium(type, wearer);
			case DEMONIUM -> demonium(type, wearer);
			case UNICORN_HORN -> unicornHorn(type, wearer);
		}
	}

	private static void celestium(ArmorItem.Type type, LivingEntity wearer) {
		switch (type) {
			case HELMET -> refresh(wearer, MobEffects.NIGHT_VISION, LONG_DURATION, 1, true);
			case CHESTPLATE -> refresh(wearer, MobEffects.FIRE_RESISTANCE, SHORT_DURATION, 1, true);
			case LEGGINGS -> refresh(wearer, MobEffects.MOVEMENT_SPEED, SHORT_DURATION, 2, true);
			case BOOTS -> refresh(wearer, MobEffects.JUMP, SHORT_DURATION, 2, false);
		}
	}

	/**
	 * La parure corrompue n'accorde qu'une resistance au feu : sa valeur tient a la protection
	 * qu'elle offre contre la corruption des Terres du demon, pas a ses effets.
	 */
	private static void corruptedCelestium(ArmorItem.Type type, LivingEntity wearer) {
		if (type == ArmorItem.Type.CHESTPLATE) {
			refresh(wearer, MobEffects.FIRE_RESISTANCE, SHORT_DURATION, 0, true);
		}
	}

	private static void demonium(ArmorItem.Type type, LivingEntity wearer) {
		switch (type) {
			case HELMET -> refresh(wearer, MobEffects.FIRE_RESISTANCE, SHORT_DURATION, 0, true);
			case CHESTPLATE -> refresh(wearer, MobEffects.DAMAGE_BOOST, SHORT_DURATION, 0, true);
			case LEGGINGS -> refresh(wearer, MobEffects.DAMAGE_RESISTANCE, SHORT_DURATION, 0, true);
			case BOOTS -> refresh(wearer, MobEffects.DIG_SPEED, SHORT_DURATION, 1, true);
		}
	}

	/**
	 * Le couvre-chef en corne de licorne : de la vitesse, et rien d'autre.
	 *
	 * <p>Il tient de la bete dont il vient. Ce n'est pas une piece de parure — il n'en existe qu'une
	 * — et son effet doit donc se suffire a lui-meme : porte seul, il rend un peu de ce qui rendait
	 * la licorne impossible a rattraper.
	 */
	private static void unicornHorn(ArmorItem.Type type, LivingEntity wearer) {
		if (type == ArmorItem.Type.HELMET) {
			refresh(wearer, MobEffects.MOVEMENT_SPEED, SHORT_DURATION, 0, true);
		}
	}

	/**
	 * Reapplique l'effet seulement lorsqu'il approche de son terme.
	 *
	 * <p>Le seuil est fixe au tiers de la duree : assez tot pour que l'effet ne s'interrompe jamais
	 * tant que la piece est portee, assez tard pour ne pas reconstruire l'instance a chaque tick.
	 */
	private static void refresh(LivingEntity wearer, MobEffect effect, int duration, int amplifier,
			boolean ambient) {
		MobEffectInstance current = wearer.getEffect(effect);
		if (current != null && current.getAmplifier() >= amplifier && current.getDuration() > duration / 3) {
			return;
		}
		wearer.addEffect(new MobEffectInstance(effect, duration, amplifier, ambient, false));
	}
}
