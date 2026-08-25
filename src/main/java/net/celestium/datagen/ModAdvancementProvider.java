package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEnchantments;
import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.celestium.worldgen.ModDimensions;
import net.celestium.worldgen.ModStructures;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Les progres du mod.
 *
 * <p>Ils suivent la progression plutot qu'ils ne la commentent : chaque branche part de son verrou
 * et mene au suivant, si bien que l'arbre se lit comme le chemin a suivre. Un joueur qui ouvre
 * l'ecran des progres doit y trouver la reponse a « et maintenant ? ».
 *
 * <p>Le mod d'origine en avait un seul, ecrit a la main. Les declarer ici plutot qu'en JSON evite
 * de retaper vingt fois la meme structure et garantit que chaque parent existe.
 */
public class ModAdvancementProvider extends ForgeAdvancementProvider {

	public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
			ExistingFileHelper fileHelper) {
		super(output, registries, fileHelper, List.of(new Tree()));
	}

	private static class Tree implements ForgeAdvancementProvider.AdvancementGenerator {

		@Override
		public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver,
				ExistingFileHelper fileHelper) {

			// --- La racine et l'Overworld ---

			Advancement root = advancement(saver, fileHelper, null, "root",
					ModItems.CELESTIUM_FRAGMENT.get(), FrameType.TASK,
					new ResourceLocation("minecraft", "textures/block/deepslate.png"),
					hasItems(ModItems.CELESTIUM_FRAGMENT.get()));

			Advancement ingot = advancement(saver, fileHelper, root, "celestium_ingot",
					ModItems.CELESTIUM_INGOT.get(), FrameType.TASK, null,
					hasItems(ModItems.CELESTIUM_INGOT.get()));

			Advancement tools = advancement(saver, fileHelper, ingot, "celestium_tools",
					ModItems.CELESTIUM_PICKAXE.get(), FrameType.TASK, null,
					hasItems(ModItems.CELESTIUM_PICKAXE.get()));

			advancement(saver, fileHelper, tools, "celestium_armour",
					ModItems.CELESTIUM_CHESTPLATE.get(), FrameType.GOAL, null,
					hasItems(ModItems.CELESTIUM_CHESTPLATE.get()));

			Advancement backpack = advancement(saver, fileHelper, ingot, "backpack",
					ModItems.BACKPACK_SMALL.get(), FrameType.TASK, null,
					hasItems(ModItems.BACKPACK_SMALL.get()));

			advancement(saver, fileHelper, backpack, "huge_backpack",
					ModItems.BACKPACK_HUGE.get(), FrameType.GOAL, null,
					hasItems(ModItems.BACKPACK_HUGE.get()));

			Advancement lucky = advancement(saver, fileHelper, root, "lucky_block",
					ModBlocks.LUCKY_BLOCK.get(), FrameType.TASK, null,
					hasItems(ModBlocks.LUCKY_BLOCK.get()));

			// --- Les terres corrompues ---

			Advancement eye = advancement(saver, fileHelper, ingot, "corrupted_eye",
					ModItems.CORRUPTED_EYE.get(), FrameType.TASK, null,
					hasItems(ModItems.CORRUPTED_EYE.get()));

			Advancement frame = advancement(saver, fileHelper, eye, "corrupted_frame",
					ModBlocks.CORRUPTED_PORTAL_FRAME.get(), FrameType.TASK, null,
					hasItems(ModBlocks.CORRUPTED_PORTAL_FRAME.get()));

			Advancement entered = advancement(saver, fileHelper, frame, "enter_corrupted",
					ModBlocks.CORRUPTED_PORTAL_FRAME.get(), FrameType.GOAL, null,
					changedTo(ModDimensions.CORRUPTED_LEVEL));

			Advancement corruptedOre = advancement(saver, fileHelper, entered, "corrupted_ore",
					ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get(), FrameType.TASK, null,
					hasItems(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get()));

			advancement(saver, fileHelper, corruptedOre, "corrupted_armour",
					ModItems.CORRUPTED_CELESTIUM_CHESTPLATE.get(), FrameType.GOAL, null,
					hasItems(ModItems.CORRUPTED_CELESTIUM_CHESTPLATE.get()));

			advancement(saver, fileHelper, corruptedOre, "corrupted_tools",
					ModItems.CORRUPTED_CELESTIUM_PICKAXE.get(), FrameType.TASK, null,
					hasItems(ModItems.CORRUPTED_CELESTIUM_PICKAXE.get()));

			Advancement book = advancement(saver, fileHelper, corruptedOre, "corrupted_book",
					ModItems.CORRUPTED_BOOK.get(), FrameType.TASK, null,
					hasItems(ModItems.CORRUPTED_BOOK.get()));

			Advancement enchantingTable = advancement(saver, fileHelper, book, "enchanting_table",
					ModBlocks.CORRUPTED_ENCHANTING_TABLE.get(), FrameType.GOAL, null,
					hasItems(ModBlocks.CORRUPTED_ENCHANTING_TABLE.get()));

			// --- Les Terres du demon ---

			Advancement demonFrame = advancement(saver, fileHelper, corruptedOre, "demon_frame",
					ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get(), FrameType.TASK, null,
					hasItems(ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get()));

			Advancement enteredDemon = advancement(saver, fileHelper, demonFrame, "enter_demon",
					ModBlocks.DEMON_LUCKY_BLOCK.get(), FrameType.GOAL, null,
					changedTo(ModDimensions.DEMON_LEVEL));

			Advancement demonium = advancement(saver, fileHelper, enteredDemon, "demonium",
					ModItems.DEMONIUM_FRAGMENT.get(), FrameType.TASK, null,
					hasItems(ModItems.DEMONIUM_FRAGMENT.get()));

			advancement(saver, fileHelper, demonium, "demonium_armour",
					ModItems.DEMONIUM_CHESTPLATE.get(), FrameType.GOAL, null,
					hasItems(ModItems.DEMONIUM_CHESTPLATE.get()));

			advancement(saver, fileHelper, enteredDemon, "demon_wood",
					ModBlocks.BOIS_DU_DEMON.log.get(), FrameType.TASK, null,
					hasItems(ModBlocks.BOIS_DU_DEMON.log.get()));

			advancement(saver, fileHelper, demonium, "summoning_altar",
					ModBlocks.SUMMONING_ALTAR.get(), FrameType.TASK, null,
					hasItems(ModBlocks.SUMMONING_ALTAR.get()));

			advancement(saver, fileHelper, demonium, "demon_heart",
					ModItems.DEMON_HEART.get(), FrameType.CHALLENGE, null,
					hasItems(ModItems.DEMON_HEART.get()));

			// --- La matiere noire et son gardien ---

			Advancement darkMatter = advancement(saver, fileHelper, tools, "dark_matter",
					ModItems.DARK_MATTER.get(), FrameType.TASK, null,
					hasItems(ModItems.DARK_MATTER.get()));

			advancement(saver, fileHelper, darkMatter, "gravity_well",
					ModBlocks.GRAVITY_WELL.get(), FrameType.TASK, null,
					hasItems(ModBlocks.GRAVITY_WELL.get()));

			advancement(saver, fileHelper, darkMatter, "dark_matter_armour",
					ModItems.DARK_MATTER_CHESTPLATE.get(), FrameType.GOAL, null,
					hasItems(ModItems.DARK_MATTER_CHESTPLATE.get()));

			// Le dragon veille sur les terres corrompues : son progres se rattache a la dimension
			// ou on le rencontre, et non a la matiere noire, qu'on peut trouver sans lui.
			advancement(saver, fileHelper, entered, "celestial_dragon",
					ModItems.CELESTIAL_DRAGON_SPAWN_EGG.get(), FrameType.CHALLENGE, null,
					killed(ModEntities.CELESTIAL_DRAGON.get()));

			// --- L'equipement de voyage ---

			advancement(saver, fileHelper, darkMatter, "invisibility_cloak",
					ModItems.INVISIBILITY_CLOAK.get(), FrameType.GOAL, null,
					hasItems(ModItems.INVISIBILITY_CLOAK.get()));

			advancement(saver, fileHelper, root, "tandem_saddle",
					ModItems.TANDEM_SADDLE.get(), FrameType.TASK, null,
					hasItems(ModItems.TANDEM_SADDLE.get()));

			// --- Ce que l'Overworld reserve ---

			advancement(saver, fileHelper, ingot, "celestium_block",
					ModBlocks.CELESTIUM_BLOCK.get(), FrameType.TASK, null,
					hasItems(ModBlocks.CELESTIUM_BLOCK.get()));

			Advancement dust = advancement(saver, fileHelper, ingot, "celestial_dust",
					ModItems.CELESTIAL_DUST.get(), FrameType.TASK, null,
					hasItems(ModItems.CELESTIAL_DUST.get()));

			// La poussiere ne se possede pas, elle se boit : le progres suit l'usage et non le
			// stock, sans quoi il tomberait avant qu'on ait ose y gouter.
			advancement(saver, fileHelper, dust, "dust_trip",
					ModItems.CELESTIAL_DUST.get(), FrameType.GOAL, null,
					drank(ModItems.CELESTIAL_DUST.get()));

			advancement(saver, fileHelper, root, "cemetery",
					Blocks.MOSSY_COBBLESTONE, FrameType.TASK, null,
					found(ModStructures.CEMETERY));

			// --- Les blocs chance ---

			advancement(saver, fileHelper, lucky, "corrupted_lucky_block",
					ModBlocks.CORRUPTED_LUCKY_BLOCK.get(), FrameType.TASK, null,
					hasItems(ModBlocks.CORRUPTED_LUCKY_BLOCK.get()));

			advancement(saver, fileHelper, lucky, "demon_lucky_block",
					ModBlocks.DEMON_LUCKY_BLOCK.get(), FrameType.GOAL, null,
					hasItems(ModBlocks.DEMON_LUCKY_BLOCK.get()));

			// --- L'arc ---

			Advancement bow = advancement(saver, fileHelper, tools, "celestial_bow",
					ModItems.CELESTIAL_BOW.get(), FrameType.TASK, null,
					hasItems(ModItems.CELESTIAL_BOW.get()));

			// --- Les structures des deux dimensions ---

			advancement(saver, fileHelper, eye, "sanctum",
					Blocks.BLACKSTONE, FrameType.GOAL, null,
					found(ModStructures.CORRUPTED_SANCTUM));

			// Le tas se trouve avant de se prendre : ce progres tombe en arrivant dessus, celui du
			// dragon en repartant vivant.
			advancement(saver, fileHelper, entered, "celestial_hoard",
					Blocks.GOLD_BLOCK, FrameType.TASK, null,
					found(ModStructures.CELESTIAL_HOARD));

			advancement(saver, fileHelper, enteredDemon, "demon_village",
					ModBlocks.BOIS_DU_DEMON.planks.get(), FrameType.TASK, null,
					found(ModStructures.DEMON_VILLAGE));

			advancement(saver, fileHelper, enteredDemon, "parasite",
					ModItems.PARASITE_SPAWN_EGG.get(), FrameType.TASK, null,
					killed(ModEntities.PARASITE.get()));

			// --- Ce que la table corrompue accorde ---

			Advancement enchanted = advancement(saver, fileHelper, enchantingTable, "corrupted_enchant",
					ModItems.CORRUPTED_BOOK.get(), FrameType.TASK, null,
					EnchantedItemTrigger.TriggerInstance.enchantedItem());

			advancement(saver, fileHelper, enchanted, "timber",
					ModItems.CORRUPTED_CELESTIUM_AXE.get(), FrameType.TASK, null,
					carrying(ModEnchantments.TIMBER.get()));

			advancement(saver, fileHelper, enchanted, "vein_miner",
					ModItems.CORRUPTED_CELESTIUM_PICKAXE.get(), FrameType.TASK, null,
					carrying(ModEnchantments.VEIN_MINER.get()));

			advancement(saver, fileHelper, enchanted, "excavation",
					ModItems.CORRUPTED_CELESTIUM_SHOVEL.get(), FrameType.TASK, null,
					carrying(ModEnchantments.EXCAVATION.get()));

			advancement(saver, fileHelper, enchanted, "harvest",
					ModItems.CORRUPTED_CELESTIUM_HOE.get(), FrameType.TASK, null,
					carrying(ModEnchantments.HARVEST.get()));

			advancement(saver, fileHelper, enchanted, "smelting",
					Blocks.FURNACE, FrameType.TASK, null,
					carrying(ModEnchantments.SMELTING.get()));

			advancement(saver, fileHelper, enchanted, "magnetism",
					ModBlocks.GRAVITY_WELL.get(), FrameType.TASK, null,
					carrying(ModEnchantments.MAGNETISM.get()));

			advancement(saver, fileHelper, enchanted, "thunderstrike",
					ModItems.CORRUPTED_CELESTIUM_SWORD.get(), FrameType.GOAL, null,
					carrying(ModEnchantments.THUNDERSTRIKE.get()));

			advancement(saver, fileHelper, enchanted, "tamer",
					ModItems.CORRUPTED_CELESTIUM_HELMET.get(), FrameType.GOAL, null,
					carrying(ModEnchantments.TAMER.get()));

			// La malediction est un progres a part : on ne la prend pas par erreur, et l'avoir sur
			// sa pioche releve autant de l'accident assume que de la reussite.
			advancement(saver, fileHelper, enchanted, "midas_curse",
					Blocks.GOLD_BLOCK, FrameType.CHALLENGE, null,
					carrying(ModEnchantments.MIDAS_CURSE.get()));

			// --- Les quatre enchantements d'arc ---

			advancement(saver, fileHelper, bow, "volley",
					ModItems.CELESTIAL_BOW.get(), FrameType.TASK, null,
					carrying(ModEnchantments.VOLLEY.get()));

			advancement(saver, fileHelper, bow, "piercing_shot",
					ModItems.CELESTIAL_BOW.get(), FrameType.TASK, null,
					carrying(ModEnchantments.PIERCING_SHOT.get()));

			advancement(saver, fileHelper, bow, "seeker",
					ModItems.CELESTIAL_BOW.get(), FrameType.GOAL, null,
					carrying(ModEnchantments.SEEKER.get()));

			advancement(saver, fileHelper, bow, "collapse",
					ModItems.CELESTIAL_BOW.get(), FrameType.GOAL, null,
					carrying(ModEnchantments.COLLAPSE.get()));

			// Le bout du chemin de l'arc : les quatre enchantements sur la meme arme. Rien n'oblige
			// a les cumuler, ils ne se genent pas entre eux — seul le prix s'y oppose.
			advancement(saver, fileHelper, bow, "complete_bow",
					ModItems.CELESTIAL_BOW.get(), FrameType.CHALLENGE, null,
					hasItems(ItemPredicate.Builder.item()
							.of(ModItems.CELESTIAL_BOW.get())
							.hasEnchantment(atLeastOne(ModEnchantments.VOLLEY.get()))
							.hasEnchantment(atLeastOne(ModEnchantments.PIERCING_SHOT.get()))
							.hasEnchantment(atLeastOne(ModEnchantments.SEEKER.get()))
							.hasEnchantment(atLeastOne(ModEnchantments.COLLAPSE.get()))
							.build()));
		}

		/**
		 * Declare un progres.
		 *
		 * <p>Le nom du critere est celui du progres : chacun n'en a qu'un, et lui donner un autre nom
		 * n'ajouterait qu'une chose de plus a garder coherente.
		 */
		private static Advancement advancement(Consumer<Advancement> saver, ExistingFileHelper fileHelper,
				@Nullable Advancement parent, String name, ItemLike icon, FrameType frame,
				@Nullable ResourceLocation background, net.minecraft.advancements.CriterionTriggerInstance trigger) {

			return Advancement.Builder.advancement()
					.parent(parent)
					.display(new DisplayInfo(
							new ItemStack(icon),
							Component.translatable("advancements.celestium." + name + ".title"),
							Component.translatable("advancements.celestium." + name + ".descr"),
							background,
							frame,
							true,
							true,
							false))
					.rewards(AdvancementRewards.Builder.experience(frame == FrameType.CHALLENGE ? 100 : 20))
					.addCriterion(name, trigger)
					.save(saver, CelestiumMod.id(name), fileHelper);
		}

		private static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... items) {
			return InventoryChangeTrigger.TriggerInstance.hasItems(items);
		}

		/** Le meme critere, exprime sur des objets decrits plutot que nommes. */
		private static InventoryChangeTrigger.TriggerInstance hasItems(ItemPredicate... predicates) {
			return InventoryChangeTrigger.TriggerInstance.hasItems(predicates);
		}

		/** Le progres tombe quand le joueur a fini d'avaler cet objet. */
		private static ConsumeItemTrigger.TriggerInstance drank(ItemLike item) {
			return ConsumeItemTrigger.TriggerInstance.usedItem(item);
		}

		/**
		 * Le progres tombe quand le joueur se trouve a l'interieur de cette structure.
		 *
		 * <p>Le jeu verifie la position une fois par seconde : il n'y a donc rien a declencher a la
		 * main, et une structure trouvee compte des qu'on y met le pied.
		 */
		private static PlayerTrigger.TriggerInstance found(ResourceKey<Structure> structure) {
			return PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(structure));
		}

		/**
		 * Le progres tombe quand le joueur detient un objet portant cet enchantement.
		 *
		 * <p>L'objet n'est pas precise : ce qui compte est l'enchantement, et le limiter a un outil
		 * en particulier exclurait celui qui l'aurait pose sur un autre.
		 */
		private static InventoryChangeTrigger.TriggerInstance carrying(Enchantment enchantment) {
			return hasItems(ItemPredicate.Builder.item().hasEnchantment(atLeastOne(enchantment)).build());
		}

		private static EnchantmentPredicate atLeastOne(Enchantment enchantment) {
			return new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.atLeast(1));
		}

		/** Le progres se declenche a la mort de la creature, tuee par le joueur. */
		private static KilledTrigger.TriggerInstance killed(EntityType<?> type) {
			return KilledTrigger.TriggerInstance.playerKilledEntity(
					EntityPredicate.Builder.entity().of(type));
		}

		private static ChangeDimensionTrigger.TriggerInstance changedTo(ResourceKey<Level> dimension) {
			return ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(dimension);
		}
	}
}
