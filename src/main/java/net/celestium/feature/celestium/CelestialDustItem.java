package net.celestium.feature.celestium;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * La poussiere celeste : on l'avale sans savoir ce qu'elle fera.
 *
 * <p>Un effet tire au sort, favorable ou non, pour une duree tiree au sort elle aussi — d'une
 * demi-minute a cinq. Ni l'un ni l'autre ne se devine avant d'avoir avale.
 *
 * <p>Aucun effet instantane ne figure dans les listes, et c'est essentiel : le soin et les degats
 * immediats ignorent la duree. Les inclure aurait rendu la moitie des tirages insensibles au
 * hasard sur lequel repose tout l'objet — et un tirage malheureux aurait pu tuer sur-le-champ, sans
 * que rien ne l'annonce.
 */
public class CelestialDustItem extends Item {

	/** Duree minimale et maximale, en secondes. */
	private static final int MIN_SECONDS = 30;
	private static final int MAX_SECONDS = 300;

	/**
	 * Ce que la poussiere peut accorder.
	 *
	 * <p>Que des effets a duree. La saturation en est absente au meme titre que le soin : elle agit
	 * a l'instant ou on la recoit.
	 */
	private static final List<MobEffect> BOONS = List.of(
			MobEffects.MOVEMENT_SPEED,
			MobEffects.DIG_SPEED,
			MobEffects.DAMAGE_BOOST,
			MobEffects.JUMP,
			MobEffects.REGENERATION,
			MobEffects.DAMAGE_RESISTANCE,
			MobEffects.FIRE_RESISTANCE,
			MobEffects.WATER_BREATHING,
			MobEffects.NIGHT_VISION,
			MobEffects.ABSORPTION,
			MobEffects.HEALTH_BOOST,
			MobEffects.LUCK,
			MobEffects.SLOW_FALLING,
			MobEffects.CONDUIT_POWER,
			MobEffects.DOLPHINS_GRACE);

	/** Ce qu'elle peut infliger. Meme regle : rien d'instantane. */
	private static final List<MobEffect> BANES = List.of(
			MobEffects.MOVEMENT_SLOWDOWN,
			MobEffects.DIG_SLOWDOWN,
			MobEffects.CONFUSION,
			MobEffects.BLINDNESS,
			MobEffects.HUNGER,
			MobEffects.WEAKNESS,
			MobEffects.POISON,
			MobEffects.WITHER,
			MobEffects.LEVITATION,
			MobEffects.UNLUCK,
			MobEffects.GLOWING,
			MobEffects.DARKNESS);

	public CelestialDustItem() {
		super(new Item.Properties()
				.rarity(Rarity.UNCOMMON)
				.stacksTo(16)
				.food(new FoodProperties.Builder()
						.nutrition(0)
						.saturationMod(0.0F)
						// Elle s'avale meme le ventre plein : ce n'est pas de la nourriture, et
						// devoir avoir faim pour s'en servir n'aurait aucun sens.
						.alwaysEat()
						.build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
		ItemStack remainder = super.finishUsingItem(stack, level, consumer);

		if (!level.isClientSide()) {
			apply(level, consumer);
		}
		return remainder;
	}

	/** Tire un effet et une duree, et les applique. */
	private static void apply(Level level, LivingEntity consumer) {
		RandomSource random = consumer.getRandom();

		boolean favourable = random.nextBoolean();
		List<MobEffect> pool = favourable ? BOONS : BANES;

		MobEffect effect = pool.get(random.nextInt(pool.size()));
		int seconds = MIN_SECONDS + random.nextInt(MAX_SECONDS - MIN_SECONDS + 1);

		consumer.addEffect(new MobEffectInstance(effect, seconds * 20, 0, false, true));

		level.playSound(null, consumer.blockPosition(),
				favourable ? SoundEvents.BEACON_ACTIVATE : SoundEvents.ENDERMAN_SCREAM,
				SoundSource.PLAYERS, 0.4F, favourable ? 1.6F : 0.7F);

		if (consumer instanceof Player player) {
			player.displayClientMessage(Component.translatable(
					favourable ? "message.celestium.dust.boon" : "message.celestium.dust.bane",
					Component.translatable(effect.getDescriptionId()), seconds), true);
		}
	}

	/** L'infobulle annonce le pari sans en devoiler l'issue. */
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
			TooltipFlag flag) {

		tooltip.add(Component.translatable("tooltip.celestium.celestial_dust")
				.withStyle(net.minecraft.ChatFormatting.GRAY));
	}
}
