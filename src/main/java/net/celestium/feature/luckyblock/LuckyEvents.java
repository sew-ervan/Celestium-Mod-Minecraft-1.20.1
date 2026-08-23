package net.celestium.feature.luckyblock;

import net.celestium.init.ModBlocks;
import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;

/**
 * Le catalogue de ce que peuvent faire les blocs chance.
 *
 * <p>Chaque methode publique fabrique un effet. Les rendre paramétrables plutot que de les figer
 * permet de reutiliser le meme mecanisme d'un palier a l'autre : la horde du bloc ordinaire et
 * celle du bloc du demon sont le meme evenement, avec d'autres creatures et un autre nombre.
 *
 * <p>Tous s'executent cote serveur. Les effets qui touchent au joueur — pieges, cages, sorts — se
 * placent a partir de sa position et non de celle du bloc : on casse souvent un bloc chance de
 * loin, et un piege qui se referme a trois metres ne pieges personne.
 */
public final class LuckyEvents {

	/** Portee des effets qui se deploient autour d'un point. */
	private static final int SCATTER = 3;

	private LuckyEvents() {
	}

	// --- Cadeaux ---

	/** Un objet tire dans une liste, pose au sol. */
	public static LuckyEvent gift(TagKey<net.minecraft.world.item.Item> pool, int min, int max) {
		return (level, pos, player, random) -> {
			int count = min + random.nextInt(max - min + 1);
			for (int i = 0; i < count; i++) {
				ItemStack stack = drawFrom(pool, random);
				if (!stack.isEmpty()) {
					Block.popResource(level, pos, stack);
				}
			}
			cheer(level, pos, SoundEvents.PLAYER_LEVELUP, 1.2F);
		};
	}

	/**
	 * Une gerbe d'objets projetes en l'air.
	 *
	 * <p>C'est le meme contenu qu'un cadeau, mais la mise en scene compte : des objets qui jaillissent
	 * se lisent comme une recompense, des objets poses au sol comme un butin ordinaire.
	 */
	public static LuckyEvent burst(TagKey<net.minecraft.world.item.Item> pool, int count) {
		return (level, pos, player, random) -> {
			for (int i = 0; i < count; i++) {
				ItemStack stack = drawFrom(pool, random);
				if (stack.isEmpty()) {
					continue;
				}
				net.minecraft.world.entity.item.ItemEntity item =
						new net.minecraft.world.entity.item.ItemEntity(level,
								pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, stack);
				item.setDeltaMovement(
						(random.nextDouble() - 0.5) * 0.4,
						0.35 + random.nextDouble() * 0.35,
						(random.nextDouble() - 0.5) * 0.4);
				level.addFreshEntity(item);
			}
			cheer(level, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0F);
		};
	}

	/** Une pluie d'experience. */
	public static LuckyEvent experience(int amount) {
		return (level, pos, player, random) -> {
			for (int left = amount; left > 0; ) {
				int orb = Math.min(left, 30);
				ExperienceOrb.award(level, net.minecraft.world.phys.Vec3.atCenterOf(pos.above()), orb);
				left -= orb;
			}
			cheer(level, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F);
		};
	}

	/** Un equipement complet, enchante. */
	public static LuckyEvent outfit(int enchantLevel, net.minecraft.world.item.Item... pieces) {
		return (level, pos, player, random) -> {
			for (net.minecraft.world.item.Item piece : pieces) {
				ItemStack stack = new ItemStack(piece);
				if (enchantLevel > 0) {
					stack = EnchantmentHelper.enchantItem(random, stack, enchantLevel, true);
				}
				Block.popResource(level, pos, stack);
			}
			cheer(level, pos, SoundEvents.ANVIL_USE, 1.2F);
		};
	}

	/** Des effets favorables, longuement. */
	public static LuckyEvent blessing(int seconds, MobEffect... effects) {
		return (level, pos, player, random) -> {
			for (MobEffect effect : effects) {
				player.addEffect(new MobEffectInstance(effect, seconds * 20, 1, false, true));
			}
			cheer(level, pos, SoundEvents.BEACON_ACTIVATE, 1.4F);
		};
	}

	// --- Constructions ---

	/**
	 * Une tour de blocs surmontee d'un phare.
	 *
	 * <p>La base est posee sous le bloc casse pour que le phare ait de quoi porter : un phare sans
	 * pyramide ne s'allume pas, et une recompense qui ne fonctionne pas n'en est pas une.
	 */
	public static LuckyEvent beaconGift(Block base) {
		return (level, pos, player, random) -> {
			BlockState floor = base.defaultBlockState();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					level.setBlockAndUpdate(pos.offset(dx, -1, dz), floor);
				}
			}
			level.setBlockAndUpdate(pos, Blocks.BEACON.defaultBlockState());
			cheer(level, pos, SoundEvents.BEACON_POWER_SELECT, 1.0F);
		};
	}

	/** Un amas de blocs precieux sous les pieds. */
	public static LuckyEvent vein(Block ore, int size) {
		return (level, pos, player, random) -> {
			BlockState state = ore.defaultBlockState();
			for (int i = 0; i < size; i++) {
				BlockPos target = pos.offset(
						random.nextInt(3) - 1,
						-1 - random.nextInt(2),
						random.nextInt(3) - 1);
				level.setBlockAndUpdate(target, state);
			}
			cheer(level, pos, SoundEvents.AMETHYST_BLOCK_CHIME, 0.8F);
		};
	}

	// --- Mauvais coups ---

	/**
	 * Une horde qui surgit autour du joueur.
	 *
	 * <p>C'est l'evenement le plus caracteristique du genre, et il sert aux trois paliers : seules
	 * changent l'espece et la quantite.
	 */
	public static LuckyEvent horde(EntityType<? extends Mob> type, int min, int max, boolean armed) {
		return (level, pos, player, random) -> {
			int count = min + random.nextInt(max - min + 1);
			for (int i = 0; i < count; i++) {
				Mob mob = type.create(level);
				if (mob == null) {
					continue;
				}
				BlockPos where = scatter(pos, random);
				mob.moveTo(where.getX() + 0.5, where.getY(), where.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
				mob.finalizeSpawn(level, level.getCurrentDifficultyAt(where), MobSpawnType.EVENT, null, null);

				if (armed) {
					mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
					mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
					mob.setDropChance(EquipmentSlot.MAINHAND, 0.15F);
					mob.setDropChance(EquipmentSlot.HEAD, 0.15F);
				}
				mob.setTarget(player);
				level.addFreshEntity(mob);
			}
			cheer(level, pos, SoundEvents.WITHER_SPAWN, 0.8F);
		};
	}

	/** Une couronne de TNT amorcee. */
	public static LuckyEvent tntRing(int count, int fuse) {
		return (level, pos, player, random) -> {
			for (int i = 0; i < count; i++) {
				BlockPos where = scatter(pos, random);
				PrimedTnt tnt = new PrimedTnt(level, where.getX() + 0.5, where.getY(), where.getZ() + 0.5, null);
				tnt.setFuse(fuse + random.nextInt(20));
				level.addFreshEntity(tnt);
			}
			cheer(level, pos, SoundEvents.TNT_PRIMED, 1.0F);
		};
	}

	/** Une pluie d'enclumes au-dessus du joueur. */
	public static LuckyEvent anvilRain(int count) {
		return (level, pos, player, random) -> {
			BlockPos above = player.blockPosition().above(12);
			for (int i = 0; i < count; i++) {
				BlockPos where = above.offset(random.nextInt(5) - 2, i, random.nextInt(5) - 2);
				FallingBlockEntity.fall(level, where, Blocks.ANVIL.defaultBlockState());
			}
			cheer(level, pos, SoundEvents.ANVIL_LAND, 0.7F);
		};
	}

	/** Une volee de fleches tiree du ciel. */
	public static LuckyEvent arrowVolley(int count) {
		return (level, pos, player, random) -> {
			for (int i = 0; i < count; i++) {
				Arrow arrow = new Arrow(level,
						player.getX() + (random.nextDouble() - 0.5) * 4.0,
						player.getY() + 12.0,
						player.getZ() + (random.nextDouble() - 0.5) * 4.0);
				arrow.setDeltaMovement(0.0, -1.2, 0.0);
				arrow.setCritArrow(true);
				level.addFreshEntity(arrow);
			}
			cheer(level, pos, SoundEvents.ARROW_SHOOT, 0.9F);
		};
	}

	/** Le sol s'ouvre sous les pieds. */
	public static LuckyEvent pitfall(int depth) {
		return (level, pos, player, random) -> {
			BlockPos under = player.blockPosition();
			for (int dy = 1; dy <= depth; dy++) {
				for (int dx = -1; dx <= 1; dx++) {
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos target = under.offset(dx, -dy, dz);
						if (level.getBlockState(target).getDestroySpeed(level, target) >= 0.0F) {
							level.removeBlock(target, false);
						}
					}
				}
			}
			cheer(level, pos, SoundEvents.GRAVEL_BREAK, 0.6F);
		};
	}

	/** Une cage refermee autour du joueur. */
	public static LuckyEvent cage(Block bars) {
		return (level, pos, player, random) -> {
			BlockPos centre = player.blockPosition();
			BlockState wall = bars.defaultBlockState();

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 2; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						boolean shell = Math.abs(dx) == 1 || Math.abs(dz) == 1 || dy == -1 || dy == 2;
						BlockPos target = centre.offset(dx, dy, dz);
						if (shell && level.getBlockState(target).canBeReplaced()) {
							level.setBlockAndUpdate(target, wall);
						}
					}
				}
			}
			cheer(level, pos, SoundEvents.IRON_DOOR_CLOSE, 0.8F);
		};
	}

	/** Une flaque de lave. */
	public static LuckyEvent lavaPool() {
		return (level, pos, player, random) -> {
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					BlockPos target = pos.offset(dx, 0, dz);
					if (level.getBlockState(target).canBeReplaced()) {
						level.setBlockAndUpdate(target, Blocks.LAVA.defaultBlockState());
					}
				}
			}
			cheer(level, pos, SoundEvents.LAVA_POP, 1.0F);
		};
	}

	/** La foudre, plusieurs fois. */
	public static LuckyEvent lightningStorm(int strikes) {
		return (level, pos, player, random) -> {
			for (int i = 0; i < strikes; i++) {
				BlockPos where = scatter(pos, random);
				Entity bolt = EntityType.LIGHTNING_BOLT.create(level);
				if (bolt != null) {
					bolt.moveTo(where.getX() + 0.5, where.getY(), where.getZ() + 0.5);
					level.addFreshEntity(bolt);
				}
			}
		};
	}

	/** Une explosion, sans prevenir. */
	public static LuckyEvent detonation(float power) {
		return (level, pos, player, random) -> level.explode(null,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				power, Level.ExplosionInteraction.BLOCK);
	}

	/** Des effets nefastes. */
	public static LuckyEvent curse(int seconds, MobEffect... effects) {
		return (level, pos, player, random) -> {
			for (MobEffect effect : effects) {
				player.addEffect(new MobEffectInstance(effect, seconds * 20, 0, false, true));
			}
			cheer(level, pos, SoundEvents.ENDERMAN_SCREAM, 0.7F);
		};
	}

	// --- Le demon ---

	/** Le demon epeiste en personne. */
	public static LuckyEvent demonAmbush() {
		return (level, pos, player, random) -> {
			Mob demon = ModEntities.DEMON_SWORDSMAN.get().create(level);
			if (demon == null) {
				return;
			}
			demon.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
			demon.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
			demon.setTarget(player);
			level.addFreshEntity(demon);

			cheer(level, pos, SoundEvents.WITHER_SPAWN, 0.6F);
		};
	}

	/** Le trophee du demon, sans avoir eu a l'affronter. */
	public static LuckyEvent demonRelic() {
		return (level, pos, player, random) -> {
			Block.popResource(level, pos, new ItemStack(ModItems.DEMON_HEART.get()));
			Block.popResource(level, pos, new ItemStack(ModItems.DEMONIUM_INGOT.get(), 4 + random.nextInt(5)));
			Block.popResource(level, pos, new ItemStack(ModBlocks.DEMONIUM_BLOCK.get(), 1));
			cheer(level, pos, SoundEvents.BEACON_ACTIVATE, 0.7F);
		};
	}

	// --- Outillage commun ---

	/** Un point au hasard dans le voisinage immediat. */
	private static BlockPos scatter(BlockPos pos, RandomSource random) {
		return pos.offset(
				random.nextInt(SCATTER * 2 + 1) - SCATTER,
				0,
				random.nextInt(SCATTER * 2 + 1) - SCATTER);
	}

	/** Tire un objet dans une liste declaree par tag. */
	private static ItemStack drawFrom(TagKey<net.minecraft.world.item.Item> pool, RandomSource random) {
		ITag<net.minecraft.world.item.Item> tag = ForgeRegistries.ITEMS.tags().getTag(pool);
		if (tag.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return tag.getRandomElement(random).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	/** Le son qui accompagne l'evenement. */
	private static void cheer(ServerLevel level, BlockPos pos, net.minecraft.sounds.SoundEvent sound,
			float pitch) {
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);
	}
}
