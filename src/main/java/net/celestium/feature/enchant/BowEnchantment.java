package net.celestium.feature.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Ce que les quatre enchantements d'arc ont en commun.
 *
 * <p>Ils partagent quatre reponses, toujours les memes : ils vont sur un arc, et nulle part
 * ailleurs ; ils ne s'obtiennent qu'a la table corrompue, donc ni par une table ordinaire, ni par
 * un coffre, ni chez un villageois, ni sur un livre. Ecrire ces quatre reponses quatre fois
 * inviterait a en oublier une, et un seul oubli suffit a faire fuir un enchantement dans le jeu de
 * base.
 *
 * <p>Ils s'appliquent tous a un arc quelconque, et pas seulement a l'arc celeste. Un enchantement
 * qui exigerait une arme precise se lirait comme une propriete de cette arme ; celui-ci reste un
 * enchantement, et l'arc celeste garde ses avantages a lui.
 */
public abstract class BowEnchantment extends Enchantment {

	protected BowEnchantment(Rarity rarity) {
		super(rarity, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof BowItem;
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}
}
