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
 * <p>Les deux materiaux de fin de course s'opposent au lieu de se repeter. Le Celestium dure et
 * mine vite ; le Demonium frappe plus fort mais s'use plus vite et accepte moins d'enchantements.
 * Chacun a son usage plutot qu'un successeur.
 *
 * <p>Le Celestium corrompu se tient a l'ecart de ces deux-la. C'est l'outillage de voyage : il
 * ouvre les Terres du demon et permet d'y creuser, sans rivaliser avec ce qu'on en rapporte. Ses
 * valeurs tiennent entre le fer et le diamant, et son niveau de recolte est celui du diamant —
 * assez pour extraire le Demonium, qui est tout ce qu'il a besoin de miner la-bas.
 */
public enum ModTiers implements Tier {

	CELESTIUM(4, 5000, 12.0F, 5.0F, 18, () -> Ingredient.of(ModItems.CELESTIUM_FRAGMENT.get()),
			List.of(Tiers.NETHERITE), List.of()),
	DEMONIUM(4, 3200, 9.0F, 8.0F, 12, () -> Ingredient.of(ModItems.DEMONIUM_FRAGMENT.get()),
			List.of(Tiers.NETHERITE), List.of()),
	CORRUPTED_CELESTIUM(3, 900, 7.0F, 2.5F, 12,
			() -> Ingredient.of(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get()),
			List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE)),

	/**
	 * La matiere noire ne s'use presque pas, et ne coupe presque rien.
	 *
	 * <p>Elle n'a pas de tranchant : ce n'est pas du metal, c'est de la masse. Ses outils durent
	 * plus longtemps que tous les autres et minent plus lentement que le fer — on ne les prend pas
	 * pour aller vite, on les prend pour ne jamais avoir a les remplacer.
	 */
	DARK_MATTER(4, 9000, 5.0F, 3.0F, 8, () -> Ingredient.of(ModItems.DARK_MATTER.get()),
			List.of(Tiers.NETHERITE), List.of());

	private final int level;
	private final int uses;
	private final float speed;
	private final float attackDamageBonus;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;

	/** Paliers que celui-ci suit, et paliers qu'il precede, dans l'ordre de recolte. */
	private final List<Object> after;
	private final List<Object> before;

	ModTiers(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue,
			Supplier<Ingredient> repairIngredient, List<Object> after, List<Object> before) {
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
		this.after = after;
		this.before = before;
	}

	/**
	 * Declare les paliers aupres de Forge pour qu'ils prennent leur place dans l'ordre de recolte.
	 * Sans cet appel, les tags {@code needs_*_tool} les ignorent et les outils ne minent pas ce que
	 * mine le palier qu'ils sont censes suivre.
	 */
	public static void registerSorting() {
		for (ModTiers tier : values()) {
			TierSortingRegistry.registerTier(
					tier,
					CelestiumMod.id(tier.name().toLowerCase(java.util.Locale.ROOT)),
					tier.after,
					tier.before);
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
