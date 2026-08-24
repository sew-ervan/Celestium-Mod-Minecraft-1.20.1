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
 * Materiaux d'armure du mod.
 *
 * <p>L'armure en Celestium redeclarait ce materiau dans une classe anonyme, au sein de sa propre
 * classe de base. Une enum partagee sert desormais les deux parures.
 *
 * <p>Le Celestium protege mieux et encaisse davantage ; le Demonium, plus leger, mise sur la
 * resistance a la poussee et se repare a bien meilleur compte.
 *
 * <p>En 1.20.1 les methodes de {@link ArmorMaterial} sont indexees par {@link ArmorItem.Type} et
 * non plus par {@code EquipmentSlot} : c'est l'un des points de rupture du portage depuis 1.19.2.
 */
public enum ModArmorMaterials implements ArmorMaterial {

	CELESTIUM("celestium", 25, new int[]{4, 7, 8, 4}, 9, 3.5F, 0.2F,
			SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ModItems.CELESTIUM_INGOT.get()),
			"celestium:textures/models/armor/celestium_layer_1.png",
			"celestium:textures/models/armor/celestium_layer_2.png"),

	/**
	 * Parure de transition : elle protege de la corruption des Terres du demon, ce qui est sa
	 * raison d'etre. Ses valeurs restent modestes, entre le fer et le diamant : on la porte pour
	 * survivre au voyage, pas pour se battre.
	 */
	CORRUPTED_CELESTIUM("corrupted_celestium", 18, new int[]{2, 5, 6, 2}, 10, 1.5F, 0.1F,
			SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(ModItems.CORRUPTED_CELESTIUM_INGOT.get()),
			"celestium:textures/models/armor/corrupted_celestium_layer_1.png",
			"celestium:textures/models/armor/corrupted_celestium_layer_2.png"),

	/**
	 * Parure de matiere noire : elle ne protege pas mieux, elle ancre.
	 *
	 * <p>Sa resistance a la poussee est la plus forte du mod — rien ne la deplace. Ce n'est pas une
	 * armure de combat mais une armure de position : on la porte pour tenir un endroit, pas pour en
	 * sortir vivant.
	 */
	DARK_MATTER("dark_matter", 30, new int[]{3, 6, 7, 3}, 6, 2.0F, 0.8F,
			SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(ModItems.DARK_MATTER.get()),
			"celestium:textures/models/armor/dark_matter_layer_1.png",
			"celestium:textures/models/armor/dark_matter_layer_2.png"),

	DEMONIUM("demonium", 20, new int[]{4, 6, 8, 4}, 12, 3.0F, 0.6F,
			SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(ModItems.DEMONIUM_INGOT.get()),
			"celestium:textures/models/armor/demonium_layer_1.png",
			"celestium:textures/models/armor/demonium_layer_2.png");

	/** Durabilite de base par emplacement, dans l'ordre bottes / jambieres / plastron / casque. */
	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};

	private final String name;
	private final int durabilityMultiplier;
	private final int[] defenceByType;
	private final int enchantmentValue;
	private final float toughness;
	private final float knockbackResistance;
	private final SoundEvent equipSound;
	private final Supplier<Ingredient> repairIngredient;
	private final String outerLayer;
	private final String innerLayer;

	ModArmorMaterials(String name, int durabilityMultiplier, int[] defenceByType, int enchantmentValue,
			float toughness, float knockbackResistance, SoundEvent equipSound,
			Supplier<Ingredient> repairIngredient, String outerLayer, String innerLayer) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.defenceByType = defenceByType;
		this.enchantmentValue = enchantmentValue;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.equipSound = equipSound;
		this.repairIngredient = repairIngredient;
		this.outerLayer = outerLayer;
		this.innerLayer = innerLayer;
	}

	/** Chemin de la couche de texture, les jambieres etant seules a utiliser la couche interne. */
	public String getLayerTexture(ArmorItem.Type type) {
		return type == ArmorItem.Type.LEGGINGS ? this.innerLayer : this.outerLayer;
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
		return this.equipSound;
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
