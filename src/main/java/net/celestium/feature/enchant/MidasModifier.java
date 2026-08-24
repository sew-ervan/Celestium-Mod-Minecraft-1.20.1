package net.celestium.feature.enchant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.celestium.init.ModEnchantments;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

/**
 * La Malediction de Midas, a l'endroit ou le butin se decide.
 *
 * <p>Elle passe par un modificateur global comme la Fonte, et pour la meme raison : ce que rend un
 * bloc n'existe pas encore au moment ou l'on casse.
 *
 * <p>« Plus rare que le fer » se lit sur les tags. Sont epargnes le charbon, le cuivre et le fer,
 * dont la valeur ne depasse pas celle de l'or ; tout le reste — redstone, lapis, diamant, emeraude,
 * quartz, debris antiques, et les minerais du mod — se change en lingots.
 */
public class MidasModifier extends LootModifier {

	public static final Codec<MidasModifier> CODEC = RecordCodecBuilder.create(
			instance -> codecStart(instance).apply(instance, MidasModifier::new));

	public MidasModifier(LootItemCondition[] conditions) {
		super(conditions);
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
		ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
		BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);

		if (tool == null || state == null || !isPrecious(state)) {
			return loot;
		}
		if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MIDAS_CURSE.get(), tool) <= 0) {
			return loot;
		}

		// Un lingot par unite perdue : la malediction transforme, elle ne fait pas disparaitre.
		int total = loot.stream().mapToInt(ItemStack::getCount).sum();
		if (total <= 0) {
			return loot;
		}

		ObjectArrayList<ItemStack> gold = new ObjectArrayList<>(1);
		gold.add(new ItemStack(Items.GOLD_INGOT, total));
		return gold;
	}

	/** Vrai pour un minerai qui vaut mieux que le fer. */
	private static boolean isPrecious(BlockState state) {
		if (!state.is(Tags.Blocks.ORES)) {
			return false;
		}
		return !state.is(Tags.Blocks.ORES_COAL)
				&& !state.is(Tags.Blocks.ORES_COPPER)
				&& !state.is(Tags.Blocks.ORES_IRON)
				&& !state.is(BlockTags.GOLD_ORES);
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
