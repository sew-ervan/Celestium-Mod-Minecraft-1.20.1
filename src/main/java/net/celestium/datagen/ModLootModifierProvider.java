package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.feature.enchant.SmeltingModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

/**
 * Declare les modificateurs de butin du mod.
 *
 * <p>Un seul pour l'instant : celui de la Fonte. Ses conditions sont vides parce que le tri se fait
 * dans le code — il faut lire l'enchantement de l'outil, ce qu'aucune condition du jeu de base ne
 * sait faire.
 */
public class ModLootModifierProvider extends GlobalLootModifierProvider {

	public ModLootModifierProvider(PackOutput output) {
		super(output, CelestiumMod.MOD_ID);
	}

	@Override
	protected void start() {
		this.add("smelting", new SmeltingModifier(new LootItemCondition[0]));
	}
}
