package net.celestium.feature.enchant;

import net.celestium.CelestiumMod;
import net.celestium.init.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Les deux enchantements qui se declenchent au combat.
 *
 * <p>Ils partagent ce fichier parce qu'ils partagent leur nature : ni l'un ni l'autre ne modifie un
 * objet ou un bloc, tous deux se greffent sur un evenement de combat et n'existent que le temps
 * d'une reaction.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class CombatEnchantments {

	/** Rayon ou chercher une cible de rechange pour le Dompteur, en blocs. */
	private static final double DIVERSION_RANGE = 12.0;

	private CombatEnchantments() {
	}

	/**
	 * L'Eclair fulgurant, au moment de la frappe.
	 *
	 * <p>La foudre est appelee apres coup et non a la place des degats : elle s'ajoute au coup
	 * d'epee au lieu de le remplacer, ce qui correspond a ce qu'annonce l'enchantement.
	 */
	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof Player attacker)) {
			return;
		}
		if (!(attacker.level() instanceof ServerLevel level)) {
			return;
		}

		ItemStack weapon = attacker.getItemBySlot(EquipmentSlot.MAINHAND);
		int enchantLevel =
				EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.THUNDERSTRIKE.get(), weapon);

		if (enchantLevel <= 0) {
			return;
		}

		LivingEntity victim = event.getEntity();
		int chance = ThunderstrikeEnchantment.chanceFor(enchantLevel, victim instanceof Player);

		if (attacker.getRandom().nextInt(100) >= chance) {
			return;
		}

		strike(level, victim, attacker);
	}

	private static void strike(ServerLevel level, LivingEntity victim, Player attacker) {
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt == null) {
			return;
		}

		bolt.moveTo(victim.getX(), victim.getY(), victim.getZ());
		if (attacker instanceof ServerPlayer server) {
			bolt.setCause(server);
		}
		level.addFreshEntity(bolt);
	}

	/**
	 * Le Dompteur, au moment ou une creature choisit sa cible.
	 *
	 * <p>Elle n'est pas simplement dissuadee : on lui donne quelqu'un d'autre a viser. Se contenter
	 * d'annuler le ciblage la ferait recommencer au tick suivant, et l'enchantement se lirait comme
	 * une creature indecise plutot que detournee.
	 */
	@SubscribeEvent
	public static void onChangeTarget(LivingChangeTargetEvent event) {
		if (!(event.getNewTarget() instanceof Player protectedPlayer)) {
			return;
		}
		if (!(event.getEntity() instanceof Mob hunter) || hunter.level().isClientSide()) {
			return;
		}

		ItemStack helmet = protectedPlayer.getItemBySlot(EquipmentSlot.HEAD);
		int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TAMER.get(), helmet);

		if (enchantLevel <= 0) {
			return;
		}

		int chance = TamerEnchantment.chanceFor(enchantLevel, hunter.getMaxHealth());
		if (chance <= 0 || hunter.getRandom().nextInt(100) >= chance) {
			return;
		}

		LivingEntity elsewhere = otherPrey(hunter, protectedPlayer);
		if (elsewhere == null) {
			return;
		}

		event.setNewTarget(elsewhere);
	}

	/**
	 * Une autre creature a viser, dans le voisinage.
	 *
	 * <p>Les joueurs sont ecartes : detourner une creature vers un camarade ferait du Dompteur une
	 * arme, ce qui n'est pas ce qu'il promet.
	 */
	@Nullable
	private static LivingEntity otherPrey(Mob hunter, Player protectedPlayer) {
		AABB around = hunter.getBoundingBox().inflate(DIVERSION_RANGE);

		List<LivingEntity> candidates = hunter.level().getEntitiesOfClass(LivingEntity.class, around,
				other -> other != hunter
						&& other != protectedPlayer
						&& !(other instanceof Player)
						&& other.isAlive()
						&& hunter.hasLineOfSight(other));

		return candidates.isEmpty()
				? null
				: candidates.get(hunter.getRandom().nextInt(candidates.size()));
	}
}
