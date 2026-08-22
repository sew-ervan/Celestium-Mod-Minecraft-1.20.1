package net.celestium.core.material;

import net.celestium.init.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;
import java.util.function.Supplier;

/**
 * Palier d'outil du Celestium, situe juste au-dessus du netherite.
 *
 * <p>Les cinq outils declaraient chacun leur propre classe anonyme {@code Tier}, avec des valeurs
 * incoherentes entre elles : la pelle minait plus vite que la pioche, la houe et la hache avaient
 * une valeur d'enchantement dix fois superieure a celle de l'epee, et la pioche annoncait un niveau
 * de recolte 5 qui ne correspond a rien en 1.20.1. Un palier unique les remplace ; ce qui distingue
 * legitimement un outil d'un autre (degats et vitesse d'attaque) passe par le constructeur de
 * chaque outil, comme en vanilla.
 */
public enum CelestiumTier implements Tier {

	CELESTIUM(4, 5000, 12.0F, 5.0F, 18, () -> Ingredient.of(ModItems.FRAGMENT_CELESTE.get()));

	private final int level;
	private final int uses;
	private final float speed;
	private final float attackDamageBonus;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;

	CelestiumTier(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue,
			Supplier<Ingredient> repairIngredient) {
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
	}

	/**
	 * Declare le palier aupres de Forge pour qu'il s'insere apres le netherite dans l'ordre de
	 * recolte. Sans cet appel, les tags {@code needs_*_tool} ignorent le palier et les outils en
	 * Celestium ne minent pas ce que mine le netherite.
	 */
	public static void registerSorting() {
		TierSortingRegistry.registerTier(
				CELESTIUM,
				net.celestium.CelestiumMod.id("celestium"),
				List.of(Tiers.NETHERITE),
				List.of());
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
