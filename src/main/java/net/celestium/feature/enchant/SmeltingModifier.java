package net.celestium.feature.enchant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.celestium.init.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

/**
 * Passe le butin au four quand l'outil porte la Fonte.
 *
 * <p>Modifier ce que rend un bloc casse ne peut pas se faire depuis un evenement de cassage : celui
 * -ci se declenche avant que le butin n'existe. Forge prevoit pour cela les modificateurs globaux
 * de butin, qui s'intercalent entre la table et le sol. C'est le mecanisme prevu, et le seul qui
 * fonctionne aussi pour un bloc casse par une explosion ou par un autre mod.
 *
 * <p>La recherche de recette se fait par le gestionnaire du monde : ce qui fond, ce sont exactement
 * les objets qu'un four saurait fondre, sans liste a tenir a jour.
 */
public class SmeltingModifier extends LootModifier {

	public static final Codec<SmeltingModifier> CODEC = RecordCodecBuilder.create(
			instance -> codecStart(instance).apply(instance, SmeltingModifier::new));

	public SmeltingModifier(LootItemCondition[] conditions) {
		super(conditions);
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
		ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

		if (tool == null
				|| EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SMELTING.get(), tool) <= 0) {
			return loot;
		}

		ObjectArrayList<ItemStack> smelted = new ObjectArrayList<>(loot.size());
		for (ItemStack stack : loot) {
			smelted.add(smelt(stack, context.getLevel()));
		}
		return smelted;
	}

	/**
	 * Rend la version fondue d'une pile, ou la pile telle quelle si rien ne la fond.
	 *
	 * <p>La quantite est reportee : huit minerais donnent huit lingots, comme au four. Le rendu de
	 * la recette peut valoir plusieurs unites — c'est rare mais possible — d'ou la multiplication
	 * plutot qu'une simple copie du compte.
	 */
	private static ItemStack smelt(ItemStack stack, ServerLevel level) {
		return level.getRecipeManager()
				.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level)
				.map(recipe -> recipe.getResultItem(level.registryAccess()))
				.filter(result -> !result.isEmpty())
				.map(result -> new ItemStack(result.getItem(), stack.getCount() * result.getCount()))
				.orElse(stack);
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
