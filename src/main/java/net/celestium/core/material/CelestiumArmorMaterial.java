package net.celestium.core.material;

import net.celestium.CelestiumMod;
import net.celestium.init.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Materiau d'armure du Celestium.
 *
 * <p>Reprend les valeurs de l'armure generee par MCreator (defense 4/7/8/4, resistance 3.5,
 * repoussee 0.2, durabilite x25) mais sous forme d'enum partagee au lieu d'une classe anonyme
 * redeclaree dans la classe de base de l'armure.
 *
 * <p>En 1.20.1 les methodes de {@link ArmorMaterial} sont indexees par {@link ArmorItem.Type} et
 * non plus par {@code EquipmentSlot} : c'est l'un des points de rupture du portage depuis 1.19.2.
 */
public enum CelestiumArmorMaterial implements ArmorMaterial {

	CELESTIUM("celestium", 25, new int[]{4, 7, 8, 4}, 9, 3.5F, 0.2F,
			() -> Ingredient.of(ModItems.MINERAI_CELESTE.get()));

	/** Durabilite de base par emplacement, dans l'ordre bottes / jambieres / plastron / casque. */
	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};

	private final String name;
	private final int durabilityMultiplier;
	private final int[] defenceByType;
	private final int enchantmentValue;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> repairIngredient;

	CelestiumArmorMaterial(String name, int durabilityMultiplier, int[] defenceByType, int enchantmentValue,
			float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.defenceByType = defenceByType;
		this.enchantmentValue = enchantmentValue;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredient = repairIngredient;
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return BASE_DURABILITY[type.getSlot().getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return this.defenceByType[type.getSlot().getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_DIAMOND;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String getName() {
		return CelestiumMod.MOD_ID + ":" + this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
