package net.celestium.datagen;

import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

/**
 * Butin des creatures.
 *
 * <p>Le demon epeiste est la seule source de Demonium accessible sans avoir deja franchi le
 * portail : c'est ce qui verrouille l'acces a sa dimension derriere sa propre defaite. Le mod
 * d'origine ne lui faisait rien lacher du tout.
 */
public class ModEntityLootTables extends EntityLootSubProvider {

	public ModEntityLootTables() {
		super(FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	public void generate() {
		// De quoi corrompre plusieurs blocs de Celestium, mais pas de quoi s'equiper : le
		// materiau en quantite reste de l'autre cote du portail.
		this.add(ModEntities.DEMON_SWORDSMAN.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(ModItems.DEMONIUM_FRAGMENT.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 10.0F))))));

		this.add(ModEntities.MINI_WARDEN.get(), LootTable.lootTable());
	}

	/**
	 * Seules ces deux creatures ont un butin a declarer. L'eclair celeste appartient a la
	 * categorie {@code MISC} et n'en attend pas.
	 */
	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return Stream.of(ModEntities.DEMON_SWORDSMAN.get(), ModEntities.MINI_WARDEN.get());
	}
}
