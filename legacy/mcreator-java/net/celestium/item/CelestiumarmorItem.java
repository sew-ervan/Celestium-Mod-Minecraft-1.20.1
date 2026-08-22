
package net.celestium.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.celestium.procedures.CelestiumarmorEvenementDeTickDuCasqueProcedure;
import net.celestium.init.CelestiumModTabs;
import net.celestium.init.CelestiumModItems;

public abstract class CelestiumarmorItem extends ArmorItem {
	public CelestiumarmorItem(EquipmentSlot slot, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForSlot(EquipmentSlot slot) {
				return new int[]{13, 15, 16, 11}[slot.getIndex()] * 25;
			}

			@Override
			public int getDefenseForSlot(EquipmentSlot slot) {
				return new int[]{4, 7, 8, 4}[slot.getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 9;
			}

			@Override
			public SoundEvent getEquipSound() {
				return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.armor.equip_diamond"));
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CelestiumModItems.MINERAI_CELESTE.get()));
			}

			@Override
			public String getName() {
				return "celestiumarmor";
			}

			@Override
			public float getToughness() {
				return 3.5f;
			}

			@Override
			public float getKnockbackResistance() {
				return 0.2f;
			}
		}, slot, properties);
	}

	public static class Helmet extends CelestiumarmorItem {
		public Helmet() {
			super(EquipmentSlot.HEAD, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "celestium:textures/models/armor/diamond__layer_1.png";
		}

		@Override
		public void onArmorTick(ItemStack itemstack, Level world, Player entity) {
			CelestiumarmorEvenementDeTickDuCasqueProcedure.execute(entity);
		}
	}

	public static class Chestplate extends CelestiumarmorItem {
		public Chestplate() {
			super(EquipmentSlot.CHEST, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "celestium:textures/models/armor/diamond__layer_1.png";
		}
	}

	public static class Leggings extends CelestiumarmorItem {
		public Leggings() {
			super(EquipmentSlot.LEGS, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "celestium:textures/models/armor/diamond__layer_2.png";
		}
	}

	public static class Boots extends CelestiumarmorItem {
		public Boots() {
			super(EquipmentSlot.FEET, new Item.Properties().tab(CelestiumModTabs.TAB_CELESTIAL_UNIVERS));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "celestium:textures/models/armor/diamond__layer_1.png";
		}
	}
}
