package net.celestium.feature.luckyblock;

import net.celestium.core.registry.ModTags;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Les trois paliers de blocs chance, et ce que chacun peut declencher.
 *
 * <p>Ils echangent la frequence contre l'intensite. Le bloc ordinaire est genereux mais sage : ses
 * bonnes issues sont frequentes et ses mauvaises sans gravite. Celui du demon est l'inverse — il
 * tourne mal la plupart du temps, et quand il tourne bien, il donne ce que le mod a de meilleur.
 * Le corrompu tient le milieu, avec des deux cotes de quoi marquer.
 *
 * <p>Les listes sont construites a la demande et non au chargement de la classe : elles referencent
 * des items et des creatures qui n'existent pas encore au moment ou les blocs s'enregistrent.
 */
public enum LuckyTier {

	ORDINARY(ModTags.Items.LUCKY_BLOCK_REWARDS, LuckyTier::ordinaryOutcomes),
	CORRUPTED(ModTags.Items.CORRUPTED_LUCKY_BLOCK_REWARDS, LuckyTier::corruptedOutcomes),
	DEMON(ModTags.Items.DEMON_LUCKY_BLOCK_REWARDS, LuckyTier::demonOutcomes);

	private final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> rewards;
	private final Supplier<List<LuckyOutcome>> builder;

	@Nullable
	private List<LuckyOutcome> outcomes;

	LuckyTier(net.minecraft.tags.TagKey<net.minecraft.world.item.Item> rewards,
			Supplier<List<LuckyOutcome>> builder) {
		this.rewards = rewards;
		this.builder = builder;
	}

	public List<LuckyOutcome> outcomes() {
		if (this.outcomes == null) {
			this.outcomes = this.builder.get();
		}
		return this.outcomes;
	}

	/**
	 * Tire une issue, en tenant compte de la chance du joueur.
	 *
	 * <p>Le tirage est pondere : chaque issue pese {@code poids + qualite x chance}. Une issue dont
	 * le poids tombe a zero ne sort plus du tout — c'est ce qui permet a une forte malchance de
	 * rendre les bonnes surprises inaccessibles, et inversement.
	 */
	public LuckyOutcome roll(RandomSource random, float luck) {
		List<LuckyOutcome> pool = this.outcomes();

		int total = 0;
		for (LuckyOutcome outcome : pool) {
			total += outcome.weightFor(luck);
		}
		if (total <= 0) {
			return pool.get(0);
		}

		int pick = random.nextInt(total);
		for (LuckyOutcome outcome : pool) {
			pick -= outcome.weightFor(luck);
			if (pick < 0) {
				return outcome;
			}
		}
		return pool.get(pool.size() - 1);
	}

	/** Proportion des issues favorables, pour une chance donnee. Sert au classement et aux tests. */
	public double fortuneRate(float luck) {
		int good = 0;
		int total = 0;
		for (LuckyOutcome outcome : this.outcomes()) {
			int weight = outcome.weightFor(luck);
			total += weight;
			if (outcome.fortune()) {
				good += weight;
			}
		}
		return total == 0 ? 0.0 : (double) good / total;
	}

	// --- Les trois tables ---

	/**
	 * Le bloc ordinaire : neuf fois sur dix quelque chose d'utile, et des deconvenues sans gravite.
	 */
	private static List<LuckyOutcome> ordinaryOutcomes() {
		return List.of(
				LuckyOutcome.good(LuckyEvents.gift(ModTags.Items.LUCKY_BLOCK_REWARDS, 1, 2), 40, 5,
						"message.celestium.lucky.gift"),
				LuckyOutcome.good(LuckyEvents.burst(ModTags.Items.LUCKY_BLOCK_REWARDS, 8), 18, 5,
						"message.celestium.lucky.burst"),
				LuckyOutcome.good(LuckyEvents.experience(120), 15, 4,
						"message.celestium.lucky.experience"),
				LuckyOutcome.good(LuckyEvents.blessing(90, MobEffects.MOVEMENT_SPEED,
						MobEffects.DIG_SPEED, MobEffects.REGENERATION), 12, 4,
						"message.celestium.lucky.blessing"),
				LuckyOutcome.good(LuckyEvents.vein(Blocks.IRON_BLOCK, 6), 8, 3,
						"message.celestium.lucky.vein"),
				LuckyOutcome.good(LuckyEvents.beaconGift(Blocks.IRON_BLOCK), 3, 2,
						"message.celestium.lucky.beacon"),

				LuckyOutcome.bad(LuckyEvents.horde(EntityType.ZOMBIE, 2, 4, false), 8, 4,
						"message.celestium.lucky.horde"),
				LuckyOutcome.bad(LuckyEvents.curse(20, MobEffects.MOVEMENT_SLOWDOWN,
						MobEffects.CONFUSION), 6, 3,
						"message.celestium.lucky.curse"),
				LuckyOutcome.bad(LuckyEvents.arrowVolley(6), 5, 3,
						"message.celestium.lucky.arrows"),
				LuckyOutcome.bad(LuckyEvents.anvilRain(3), 4, 2,
						"message.celestium.lucky.anvils"));
	}

	/** Le corrompu : une fois sur deux, mais des deux cotes ca commence a compter. */
	private static List<LuckyOutcome> corruptedOutcomes() {
		return List.of(
				LuckyOutcome.good(LuckyEvents.burst(ModTags.Items.CORRUPTED_LUCKY_BLOCK_REWARDS, 5), 20, 6,
						"message.celestium.lucky.burst"),
				LuckyOutcome.good(LuckyEvents.outfit(25,
						ModItems.CORRUPTED_CELESTIUM_HELMET.get(),
						ModItems.CORRUPTED_CELESTIUM_CHESTPLATE.get(),
						ModItems.CORRUPTED_CELESTIUM_LEGGINGS.get(),
						ModItems.CORRUPTED_CELESTIUM_BOOTS.get()), 10, 5,
						"message.celestium.lucky.outfit"),
				LuckyOutcome.good(LuckyEvents.vein(Blocks.DIAMOND_BLOCK, 5), 9, 4,
						"message.celestium.lucky.vein"),
				LuckyOutcome.good(LuckyEvents.experience(400), 8, 4,
						"message.celestium.lucky.experience"),
				LuckyOutcome.good(LuckyEvents.blessing(180, MobEffects.DAMAGE_BOOST,
						MobEffects.DAMAGE_RESISTANCE, MobEffects.FIRE_RESISTANCE), 8, 4,
						"message.celestium.lucky.blessing"),

				LuckyOutcome.bad(LuckyEvents.horde(EntityType.CREEPER, 3, 6, false), 14, 6,
						"message.celestium.lucky.creepers"),
				LuckyOutcome.bad(LuckyEvents.horde(EntityType.ZOMBIE, 6, 10, true), 12, 5,
						"message.celestium.lucky.invasion"),
				LuckyOutcome.bad(LuckyEvents.tntRing(5, 40), 10, 5,
						"message.celestium.lucky.tnt"),
				LuckyOutcome.bad(LuckyEvents.cage(Blocks.OBSIDIAN), 6, 3,
						"message.celestium.lucky.cage"),
				LuckyOutcome.bad(LuckyEvents.lavaPool(), 6, 3,
						"message.celestium.lucky.lava"),
				LuckyOutcome.bad(LuckyEvents.pitfall(8), 5, 3,
						"message.celestium.lucky.pitfall"));
	}

	/**
	 * Le bloc du demon : il tourne mal cinq fois sur six.
	 *
	 * <p>Ses bonnes issues sont a la mesure du risque — un equipement complet en Demonium, le coeur
	 * du demon sans avoir eu a l'affronter. Ses mauvaises aussi : c'est le seul palier qui puisse
	 * faire surgir le demon lui-meme.
	 */
	private static List<LuckyOutcome> demonOutcomes() {
		return List.of(
				LuckyOutcome.good(LuckyEvents.demonRelic(), 6, 5,
						"message.celestium.lucky.relic"),
				LuckyOutcome.good(LuckyEvents.outfit(35,
						ModItems.DEMONIUM_HELMET.get(),
						ModItems.DEMONIUM_CHESTPLATE.get(),
						ModItems.DEMONIUM_LEGGINGS.get(),
						ModItems.DEMONIUM_BOOTS.get(),
						ModItems.DEMONIUM_SWORD.get()), 5, 5,
						"message.celestium.lucky.outfit"),
				LuckyOutcome.good(LuckyEvents.burst(ModTags.Items.DEMON_LUCKY_BLOCK_REWARDS, 4), 5, 4,
						"message.celestium.lucky.burst"),
				LuckyOutcome.good(LuckyEvents.beaconGift(ModBlocks.DEMONIUM_BLOCK.get()), 3, 3,
						"message.celestium.lucky.beacon"),

				LuckyOutcome.bad(LuckyEvents.horde(ModEntities.PARASITE.get(), 8, 14, false), 22, 8,
						"message.celestium.lucky.parasites"),
				LuckyOutcome.bad(LuckyEvents.horde(ModEntities.CORRUPTED_VILLAGER.get(), 4, 7, false), 16, 6,
						"message.celestium.lucky.invasion"),
				LuckyOutcome.bad(LuckyEvents.demonAmbush(), 12, 5,
						"message.celestium.lucky.demon"),
				LuckyOutcome.bad(LuckyEvents.tntRing(9, 30), 12, 5,
						"message.celestium.lucky.tnt"),
				LuckyOutcome.bad(LuckyEvents.detonation(5.0F), 10, 4,
						"message.celestium.lucky.detonation"),
				LuckyOutcome.bad(LuckyEvents.lightningStorm(6), 9, 4,
						"message.celestium.lucky.lightning"),
				LuckyOutcome.bad(LuckyEvents.curse(30, MobEffects.WITHER, MobEffects.BLINDNESS,
						MobEffects.WEAKNESS), 8, 4,
						"message.celestium.lucky.curse"),
				LuckyOutcome.bad(LuckyEvents.pitfall(14), 6, 3,
						"message.celestium.lucky.pitfall"));
	}

	public net.minecraft.tags.TagKey<net.minecraft.world.item.Item> rewards() {
		return this.rewards;
	}
}
