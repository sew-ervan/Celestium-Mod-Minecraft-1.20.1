package net.celestium.core.material;

import net.celestium.CelestiumMod;
import net.celestium.init.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;
import java.util.function.Supplier;

/**
 * Paliers d'outils du mod, tous deux situes au-dessus du netherite.
 *
 * <p>Les cinq outils en Celestium declaraient chacun leur propre classe anonyme {@code Tier}, avec
 * des valeurs incoherentes entre elles : la pelle minait plus vite que la pioche, la houe et la
 * hache avaient une valeur d'enchantement dix fois superieure a celle de l'epee, et la pioche
 * annoncait un niveau de recolte 5 qui ne correspond a rien en 1.20.1.
 *
 * <p>Les deux materiaux s'opposent au lieu de se repeter. Le Celestium dure et mine vite ; le
 * Demonium frappe plus fort mais s'use plus vite et accepte moins d'enchantements. Chacun a son
 * usage plutot qu'un successeur.
 */
public enum ModTiers implements Tier {

	CELESTIUM(4, 5000, 12.0F, 5.0F, 18, () -> Ingredient.of(ModItems.CELESTIUM_FRAGMENT.get())),
	DEMONIUM(4, 3200, 9.0F, 8.0F, 12, () -> Ingredient.of(ModItems.DEMONIUM_FRAGMENT.get()));

	private final int level;
	private final int uses;
	private final float speed;
	private final float attackDamageBonus;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;

	ModTiers(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue,
			Supplier<Ingredient> repairIngredient) {
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
	}

	/**
	 * Declare les paliers aupres de Forge pour qu'ils s'inserent apres le netherite dans l'ordre
	 * de recolte. Sans cet appel, les tags {@code needs_*_tool} les ignorent et les outils ne
	 * minent pas ce que mine le netherite.
	 */
	public static void registerSorting() {
		for (ModTiers tier : values()) {
			TierSortingRegistry.registerTier(
					tier,
					CelestiumMod.id(tier.name().toLowerCase(java.util.Locale.ROOT)),
					List.of(Tiers.NETHERITE),
					List.of());
		}
	}

	@Override
	public int getUses() {
		return this.uses;
	}

	@Override
	public float getSpeed() {
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.attackDamageBonus;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}
