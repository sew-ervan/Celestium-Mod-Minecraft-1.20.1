package net.celestium.feature.corruption;

import net.celestium.CelestiumMod;
import net.celestium.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * L'instabilite des terres corrompues.
 *
 * <p>Deux mondes s'y sont heurtes et le heurt n'est pas fini. Plus on s'attarde, plus la dimension
 * se derobe : des effets tombent sans raison, des creatures apparaissent d'un monde comme de
 * l'autre, et la roche elle-meme se met a changer de nature autour du visiteur.
 *
 * <p>La corruption est un compteur par joueur, de zero a cent. Il monte tant qu'on reste, redescend
 * des qu'on part, et se conserve entre deux sessions : sans cela, se deconnecter puis revenir
 * remettrait tout a zero, ce qui viderait la mecanique de son sens.
 *
 * <p>Elle ne tue pas directement, a la difference de la corruption des Terres du demon. Ce n'est pas
 * une sanction mais un climat : l'idee est qu'on puisse y travailler, en sachant que plus le sejour
 * dure, plus il devient imprevisible.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class DimensionInstability {

	/** Cle sous laquelle le compteur survit a la mort et a la deconnexion. */
	private static final String CORRUPTION_KEY = "CorruptedLandsExposure";

	/** Valeur maximale du compteur. */
	private static final int MAX_CORRUPTION = 100;

	/** Periode de reevaluation, en ticks. */
	private static final int CHECK_INTERVAL = 40;

	/** Ce que le compteur gagne a chaque reevaluation passee sur place. */
	private static final int RISE_PER_CHECK = 1;

	/** Ce qu'il perd a chaque reevaluation passee ailleurs. Partir soulage plus vite qu'arriver. */
	private static final int FALL_PER_CHECK = 3;

	/**
	 * Diviseur de la probabilite d'incident.
	 *
	 * <p>La chance qu'il se passe quelque chose vaut le compteur divise par ce nombre. A cent, un
	 * incident survient donc une reevaluation sur quatre, soit une fois toutes les huit secondes.
	 */
	private static final int INCIDENT_DIVISOR = 400;

	/** Seuil a partir duquel la roche elle-meme commence a changer. */
	private static final int SPREAD_THRESHOLD = 50;

	/** Nombre de blocs convertis a chaque poussee, et rayon de la zone touchee. */
	private static final int SPREAD_BLOCKS = 6;
	private static final int SPREAD_RADIUS = 6;

	/** Rayon d'apparition des creatures, en blocs. */
	private static final int SPAWN_RADIUS = 8;

	/**
	 * Ce qui peut surgir. Overworld et Nether melanges, comme le sol : ce sont les deux mondes qui
	 * se sont heurtes, et leurs habitants se retrouvent au meme endroit.
	 *
	 * <p>Rien des Terres du demon n'y figure. Cette dimension-la se merite ; elle ne deborde pas.
	 */
	private static final List<EntityType<? extends Mob>> WANDERERS = List.of(
			EntityType.ZOMBIE,
			EntityType.SKELETON,
			EntityType.CREEPER,
			EntityType.SPIDER,
			EntityType.WITCH,
			EntityType.ENDERMAN,
			EntityType.HUSK,
			EntityType.STRAY,
			EntityType.SILVERFISH,
			EntityType.ZOMBIFIED_PIGLIN,
			EntityType.MAGMA_CUBE,
			EntityType.BLAZE,
			EntityType.WITHER_SKELETON,
			EntityType.HOGLIN);

	/** Ce que la dimension peut accorder. */
	private static final List<MobEffect> BOONS = List.of(
			MobEffects.MOVEMENT_SPEED,
			MobEffects.DAMAGE_BOOST,
			MobEffects.REGENERATION,
			MobEffects.FIRE_RESISTANCE,
			MobEffects.NIGHT_VISION,
			MobEffects.JUMP,
			MobEffects.ABSORPTION,
			MobEffects.DIG_SPEED);

	/** Ce qu'elle peut infliger. */
	private static final List<MobEffect> BANES = List.of(
			MobEffects.POISON,
			MobEffects.BLINDNESS,
			MobEffects.CONFUSION,
			MobEffects.WEAKNESS,
			MobEffects.MOVEMENT_SLOWDOWN,
			MobEffects.HUNGER,
			MobEffects.DIG_SLOWDOWN,
			MobEffects.LEVITATION);

	/** Duree des effets accordes ou infliges, en ticks. */
	private static final int EFFECT_DURATION = 200;

	private DimensionInstability() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (player.tickCount % CHECK_INTERVAL != 0) {
			return;
		}

		boolean inside = player.level().dimension() == ModDimensions.CORRUPTED_LEVEL;
		int corruption = corruptionOf(player);

		if (!inside) {
			if (corruption > 0) {
				setCorruption(player, Math.max(0, corruption - FALL_PER_CHECK));
			}
			return;
		}

		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		corruption = Math.min(MAX_CORRUPTION, corruption + RISE_PER_CHECK);
		setCorruption(player, corruption);

		RandomSource random = player.getRandom();
		if (random.nextInt(INCIDENT_DIVISOR) < corruption) {
			incident((ServerLevel) player.level(), player, corruption, random);
		}
	}

	/**
	 * Un incident, tire au sort.
	 *
	 * <p>Les trois formes ne sont pas equiprobables : les effets dominent au debut, les apparitions
	 * prennent le dessus ensuite, et la roche ne se met a changer qu'a mi-parcours. C'est ce qui
	 * donne l'impression d'une degradation plutot que d'un desordre constant.
	 */
	private static void incident(ServerLevel level, ServerPlayer player, int corruption,
			RandomSource random) {

		int roll = random.nextInt(100);

		if (roll < 45) {
			randomEffect(player, corruption, random);
		} else if (roll < 85 || corruption < SPREAD_THRESHOLD) {
			randomSpawn(level, player, corruption, random);
		} else {
			spreadCorruption(level, player, random);
		}
	}

	/**
	 * Un effet au hasard, favorable ou non.
	 *
	 * <p>La part de mauvais monte avec le compteur : nulle part au debut, ecrasante a la fin. Une
	 * dimension qui n'accorderait que des malheurs se lirait comme une punition ; celle-ci commence
	 * par etre capricieuse et finit par etre hostile.
	 */
	private static void randomEffect(ServerPlayer player, int corruption, RandomSource random) {
		boolean bane = random.nextInt(MAX_CORRUPTION + 20) < corruption + 10;
		List<MobEffect> pool = bane ? BANES : BOONS;

		MobEffect effect = pool.get(random.nextInt(pool.size()));
		int amplifier = corruption >= 75 ? 1 : 0;

		player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, amplifier, false, true));
		announce(player, bane ? "message.celestium.corrupted.bane" : "message.celestium.corrupted.boon");
	}

	/** Des creatures surgies de l'un ou l'autre monde. */
	private static void randomSpawn(ServerLevel level, ServerPlayer player, int corruption,
			RandomSource random) {

		int count = 1 + random.nextInt(1 + corruption / 40);

		for (int i = 0; i < count; i++) {
			EntityType<? extends Mob> type = WANDERERS.get(random.nextInt(WANDERERS.size()));
			Mob mob = type.create(level);
			if (mob == null) {
				continue;
			}

			BlockPos where = player.blockPosition().offset(
					random.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS,
					0,
					random.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS);

			mob.moveTo(where.getX() + 0.5, where.getY(), where.getZ() + 0.5,
					random.nextFloat() * 360.0F, 0.0F);
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(where), MobSpawnType.EVENT, null, null);
			level.addFreshEntity(mob);
		}

		level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER,
				SoundSource.AMBIENT, 0.5F, 0.6F);
		announce(player, "message.celestium.corrupted.wanderers");
	}

	/**
	 * La roche change de nature autour du joueur.
	 *
	 * <p>Quelques blocs seulement, et uniquement de la pierre ordinaire : la conversion se voit sans
	 * defigurer le terrain, et ne touche jamais ce qu'un joueur aurait bati.
	 */
	private static void spreadCorruption(ServerLevel level, ServerPlayer player, RandomSource random) {
		BlockState netherrack = Blocks.NETHERRACK.defaultBlockState();
		int changed = 0;

		for (int attempt = 0; attempt < SPREAD_BLOCKS * 4 && changed < SPREAD_BLOCKS; attempt++) {
			BlockPos where = player.blockPosition().offset(
					random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS,
					random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS,
					random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS);

			BlockState state = level.getBlockState(where);
			if (state.is(Blocks.STONE) || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
				level.setBlockAndUpdate(where, netherrack);
				changed++;
			}
		}

		if (changed > 0) {
			announce(player, "message.celestium.corrupted.spreading");
		}
	}

	private static void announce(ServerPlayer player, String key) {
		player.displayClientMessage(Component.translatable(key), true);
	}

	// --- Le compteur ---

	/** Niveau de corruption de ce joueur, de zero a cent. */
	public static int corruptionOf(Player player) {
		return persisted(player).getInt(CORRUPTION_KEY);
	}

	private static void setCorruption(Player player, int value) {
		CompoundTag data = persisted(player);
		data.putInt(CORRUPTION_KEY, value);
		player.getPersistentData().put(Player.PERSISTED_NBT_TAG, data);
	}

	/**
	 * La partie des donnees du joueur qui survit a la mort.
	 *
	 * <p>Forge recopie ce sous-ensemble sur la nouvelle entite lors d'une reapparition. Le reste des
	 * donnees persistantes disparait avec le corps.
	 */
	private static CompoundTag persisted(Player player) {
		return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
	}
}
