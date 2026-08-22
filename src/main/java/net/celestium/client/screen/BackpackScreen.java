package net.celestium.client.screen;

import net.celestium.feature.backpack.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Interface d'un sac celeste.
 *
 * <p>Le fond reutilise la texture de coffre vanilla, decoupee selon le nombre de rangees : la
 * meme classe sert donc aux trois tailles. Le mod d'origine avait trois ecrans et trois images,
 * chacune figeant sa disposition.
 */
public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

	/** Ligne de la texture ou commence le bloc d'inventaire du joueur. */
	private static final int PLAYER_SECTION_SOURCE_Y = 126;

	private final int gridHeight;

	public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = BackpackMenu.IMAGE_WIDTH;
		this.imageHeight = BackpackMenu.imageHeight(menu.getTier());
		this.gridHeight = menu.getTier().rows() * BackpackMenu.SLOT_SIZE + BackpackMenu.TOP_BORDER;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		// Partie haute : bordure et rangees du sac.
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.gridHeight);
		// Partie basse : inventaire du joueur, pris tel quel dans la texture.
		graphics.blit(TEXTURE, this.leftPos, this.topPos + this.gridHeight, 0, PLAYER_SECTION_SOURCE_Y,
				this.imageWidth, BackpackMenu.PLAYER_SECTION_HEIGHT);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
}
