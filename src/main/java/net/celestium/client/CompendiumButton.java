package net.celestium.client;

import net.celestium.CelestiumMod;
import net.celestium.client.screen.CompendiumScreen;
import net.celestium.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Le bouton qui ouvre le compendium, greffe sur l'inventaire.
 *
 * <p>Il se pose a cote du panneau plutot que dedans : l'inventaire vanilla n'a pas un pixel de libre,
 * et empieter dessus masquerait quelque chose. A cote, il se voit sans rien cacher.
 *
 * <p>Le bouton porte un lingot de Celestium plutot qu'une lettre. Une interface qui ajoute un bouton
 * a un ecran qu'elle ne possede pas doit dire d'ou il vient, et l'objet le dit mieux qu'un mot.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID, value = Dist.CLIENT)
public final class CompendiumButton {

	private static final int SIZE = 20;

	/** Ecart entre le bord droit de l'inventaire et le bouton. */
	private static final int GAP = 4;

	private CompendiumButton() {
	}

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		if (!(event.getScreen() instanceof InventoryScreen screen)) {
			return;
		}

		int x = screen.getGuiLeft() + screen.getXSize() + GAP;
		int y = screen.getGuiTop() + GAP;

		event.addListener(new IconButton(x, y));
	}

	/** Un bouton qui montre un objet au lieu d'un texte. */
	private static class IconButton extends Button {

		private IconButton(int x, int y) {
			super(x, y, SIZE, SIZE, Component.translatable("gui.celestium.compendium"),
					button -> Minecraft.getInstance().setScreen(new CompendiumScreen()),
					DEFAULT_NARRATION);
		}

		@Override
		public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
			super.renderWidget(graphics, mouseX, mouseY, partialTick);

			// L'objet est pose par-dessus le bouton deja dessine, et le libelle reste pour la
			// lecture d'ecran : le bouton s'annonce, mais ne s'ecrit pas.
			graphics.renderItem(new ItemStack(ModItems.CELESTIUM_INGOT.get()),
					this.getX() + 2, this.getY() + 2);
		}

		@Override
		public void renderString(GuiGraphics graphics, net.minecraft.client.gui.Font font, int colour) {
			// Rien : l'icone tient lieu de libelle.
		}
	}
}
