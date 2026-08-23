package net.celestium.datagen;

import net.celestium.core.registry.ModTags;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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

	/**
	 * Amplitude de l'influence de la chance sur un tirage de bloc chance.
	 *
	 * <p>Le tresor gagne ce nombre de points de poids par cran de chance, le rebut en perd autant.
	 */
	private static final int LUCK_SWING = 5;

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

		this.add(ModBlocks.DEMONIUM_ORE.get(), block -> createSilkTouchDispatchTable(block,
				this.applyExplosionDecay(block,
						LootItem.lootTableItem(ModItems.DEMONIUM_FRAGMENT.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
								.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));

		// Les trois blocs chance. Voir luckyBlock ci-dessous pour l'echelle.
		this.add(ModBlocks.LUCKY_BLOCK.get(),
				luckyBlock(ModTags.Items.LUCKY_BLOCK_REWARDS, 20, 10));
		this.add(ModBlocks.CORRUPTED_LUCKY_BLOCK.get(),
				luckyBlock(ModTags.Items.CORRUPTED_LUCKY_BLOCK_REWARDS, 15, 25));
		this.add(ModBlocks.DEMON_LUCKY_BLOCK.get(),
				luckyBlock(ModTags.Items.DEMON_LUCKY_BLOCK_REWARDS, 6, 40));

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

	/**
	 * Une table de bloc chance : un tirage unique entre un tresor et un rebut.
	 *
	 * <p>Les trois blocs echangent la frequence contre la valeur. Le bloc chance ordinaire donne
	 * presque toujours quelque chose, mais du courant ; celui du demon ne donne presque jamais
	 * rien, mais ce qu'il donne ne se trouve pas ailleurs. Le corrompu tient le milieu.
	 *
	 * <p>Le reglage passe par les poids, et une entree de tag en {@code expand} transmet son poids
	 * et sa qualite a chacun des items du tag. Le rapport final depend donc du nombre d'items dans
	 * chaque tag autant que des poids : {@code poids x nombre d'items} d'un cote contre l'autre.
	 * Les valeurs ci-dessous sont choisies pour les tailles de tags actuelles — vingt recompenses
	 * ordinaires, dix corrompues, huit demoniaques, six rebuts.
	 *
	 * <p>La qualite est ce qui rend la chance jouable. Le jeu calcule le poids reel d'une entree
	 * par {@code poids + qualite x chance}, ou la chance est l'attribut du joueur : une potion de
	 * chance l'augmente d'un cran, l'effet de malchance la descend d'autant. En donnant au tresor
	 * une qualite positive et au rebut la negative, les deux jouent en sens contraire et les deux
	 * se ressentent. Sur le bloc du demon, boire une potion de chance fait passer d'a peu pres un
	 * tirage sur six a un sur trois ; la malchance le fait tomber a un sur trente.
	 */
	private LootTable.Builder luckyBlock(TagKey<Item> rewards, int rewardWeight, int junkWeight) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(TagEntry.expandTag(rewards)
						.setWeight(rewardWeight)
						.setQuality(LUCK_SWING))
				.add(TagEntry.expandTag(ModTags.Items.LUCKY_BLOCK_JUNK)
						.setWeight(junkWeight)
						.setQuality(-LUCK_SWING)));
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).map(Block.class::cast).toList();
	}
}
