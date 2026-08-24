package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.celestium.worldgen.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
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

			advancement(saver, fileHelper, root, "lucky_block",
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

			advancement(saver, fileHelper, book, "enchanting_table",
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

		private static ChangeDimensionTrigger.TriggerInstance changedTo(ResourceKey<Level> dimension) {
			return ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(dimension);
		}
	}
}
