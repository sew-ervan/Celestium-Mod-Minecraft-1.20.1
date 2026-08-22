package net.celestium.feature.celestium;

import net.celestium.core.material.CelestiumTier;
import net.celestium.feature.magie.SpellCaster;
import net.celestium.init.ModSpells;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

/**
 * Epee en Celestium : lance la frappe celeste au clic droit.
 *
 * <p>Le mod d'origine appelait directement une procedure de deux cents lignes depuis cette methode.
 * L'epee ne connait plus que le sort a lancer ; tout le reste — cout, recharge, camp — est du
 * ressort de {@link SpellCaster}.
 */
public class CelestiumSwordItem extends SwordItem {

	public CelestiumSwordItem() {
		super(CelestiumTier.CELESTIUM, 3, -2.4F, new Item.Properties().fireResistant());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);

		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			SpellCaster.tryCast(serverPlayer, ModSpells.CELESTIAL_STRIKE);
		}

		return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
	}
}
