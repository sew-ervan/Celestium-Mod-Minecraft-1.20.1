package net.celestium.datagen;

import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

/**
 * Genere les tables de butin des blocs.
 *
 * <p>Le mod d'origine n'en avait aucune : il surchargeait {@code getDrops} dans les classes de
 * blocs pour rendre un objet en dur, ce qui ignorait la Fortune, la Toucher de soie et la
 * deterioration par explosion.
 */
public class ModBlockLootTables extends BlockLootSubProvider {

	public ModBlockLootTables() {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	protected void generate() {
		WoodSet demon = ModBlocks.BOIS_DU_DEMON;

		// Le minerai rend des fragments, en quantite accrue par la Fortune, et le bloc lui-meme
		// sous Toucher de soie.
		this.add(ModBlocks.CELESTIUM_ORE.get(), block -> createSilkTouchDispatchTable(block,
				this.applyExplosionDecay(block,
						LootItem.lootTableItem(ModItems.CELESTIUM_FRAGMENT.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
								.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));

		this.dropSelf(ModBlocks.CELESTIUM_BLOCK.get());
		this.dropSelf(ModBlocks.DEMONIUM_BLOCK.get());
		this.dropSelf(ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		this.dropSelf(ModBlocks.SUMMONING_ALTAR.get());

		// Le minerai des terres corrompues. Genereux : c'est une etape de passage, et la dimension
		// se charge deja de faire payer le sejour.
		this.add(ModBlocks.CORRUPTED_CELESTIUM_ORE.get(), block -> createSilkTouchDispatchTable(block,
				this.applyExplosionDecay(block,
						LootItem.lootTableItem(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
								.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));

		this.dropSelf(ModBlocks.CORRUPTED_PORTAL_FRAME.get());
		this.dropSelf(ModBlocks.CORRUPTED_ENCHANTING_TABLE.get());

		this.add(ModBlocks.DEMONIUM_ORE.get(), block -> createSilkTouchDispatchTable(block,
				this.applyExplosionDecay(block,
						LootItem.lootTableItem(ModItems.DEMONIUM_FRAGMENT.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
								.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));

		// Les trois blocs chance ne rendent rien par eux-memes : tout ce qu'ils donnent passe par
		// leurs evenements, y compris les cadeaux. Une table de butin ne saurait de toute facon pas
		// faire surgir une horde ni ouvrir le sol. Les tags de recompenses restent utilises, mais
		// c'est le code qui y pioche.
		this.add(ModBlocks.LUCKY_BLOCK.get(), noDrop());
		this.add(ModBlocks.CORRUPTED_LUCKY_BLOCK.get(), noDrop());
		this.add(ModBlocks.DEMON_LUCKY_BLOCK.get(), noDrop());

		this.dropSelf(demon.log.get());
		this.dropSelf(demon.wood.get());
		this.dropSelf(demon.planks.get());
		this.dropSelf(demon.stairs.get());
		this.dropSelf(demon.fence.get());
		this.dropSelf(demon.fenceGate.get());
		this.dropSelf(demon.pressurePlate.get());
		this.dropSelf(demon.button.get());

		// Une dalle cassee alors qu'elle est doublee rend deux dalles.
		this.add(demon.slab.get(), this::createSlabItemTable);

		// L'essence n'a pas de pousse : les feuilles ne se recuperent qu'a la cisaille. Ajouter un
		// jeune arbre plus tard permettra de passer a createLeavesDrops.
		this.add(demon.leaves.get(), BlockLootSubProvider::createShearsOnlyDrop);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).map(Block.class::cast).toList();
	}
}
