package net.celestium.client.screen;

import net.celestium.CelestiumMod;
import net.celestium.feature.backpack.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Interface d'un sac celeste.
 *
 * <p>Une seule classe pour les quatre tailles. Les deux plus grandes ne tiennent pas a l'ecran —
 * vingt rangees font trois cent soixante pixels, davantage que la hauteur utile a l'echelle
 * courante — donc l'interface n'en montre que six et defile.
 *
 * <p>Le defilement deplace les emplacements eux-memes plutot que la vue. C'est ainsi que procede
 * l'inventaire creatif du jeu de base : les emplacements hors fenetre sont ecartes tres loin, ou
 * plus rien ne peut les survoler ni les cliquer, ce qui evite d'avoir a filtrer les clics.
 */
public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

	private static final ResourceLocation TEXTURE =
			CelestiumMod.id("textures/gui/backpack.png");

	/** Ligne de la texture ou commence le bloc d'inventaire du joueur. */
	private static final int PLAYER_SECTION_SOURCE_Y = 125;

	/** Ou sont envoyes les emplacements hors fenetre. */
	private static final int OFFSCREEN = -5000;

	// Barre de defilement, a droite de la grille.
	private static final int BAR_X = 186;
	private static final int BAR_WIDTH = 12;
	private static final int THUMB_HEIGHT = 15;

	private static final int GROOVE_COLOUR = 0xFF8B8B8B;
	private static final int GROOVE_SHADOW = 0xFF373737;
	private static final int THUMB_COLOUR = 0xFFC6C6C6;
	private static final int THUMB_SHADOW = 0xFF555555;

	private final int gridHeight;
	private final int barHeight;
	private final int scrollRange;

	/** Rangee affichee tout en haut. */
	private int scrollRow;

	private boolean draggingThumb;

	public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);

		this.imageWidth = BackpackMenu.IMAGE_WIDTH;
		this.imageHeight = BackpackMenu.imageHeight(menu.getTier());
		this.gridHeight = BackpackMenu.visibleRows(menu.getTier()) * BackpackMenu.SLOT_SIZE
				+ BackpackMenu.TOP_BORDER;
		this.barHeight = BackpackMenu.visibleRows(menu.getTier()) * BackpackMenu.SLOT_SIZE;
		this.scrollRange = BackpackMenu.scrollRange(menu.getTier());
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.placeSlots();
	}

	/**
	 * Replace les emplacements du sac selon le defilement.
	 *
	 * <p>Seuls les emplacements du sac bougent ; ceux de l'inventaire du joueur gardent la place que
	 * le menu leur a donnee.
	 */
	private void placeSlots() {
		int size = this.menu.getTier().size();
		int columns = this.menu.getTier().columns();

		for (int index = 0; index < size; index++) {
			Slot slot = this.menu.slots.get(index);
			int row = index / columns;
			int visibleRow = row - this.scrollRow;

			if (visibleRow < 0 || visibleRow >= BackpackMenu.visibleRows(this.menu.getTier())) {
				slot.y = OFFSCREEN;
			} else {
				slot.y = BackpackMenu.GRID_TOP + visibleRow * BackpackMenu.SLOT_SIZE;
			}
		}
	}

	private void setScroll(int row) {
		int clamped = Math.max(0, Math.min(this.scrollRange, row));
		if (clamped != this.scrollRow) {
			this.scrollRow = clamped;
			this.placeSlots();
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (this.scrollRange > 0) {
			this.setScroll(this.scrollRow - (int) Math.signum(delta));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.scrollRange > 0 && overBar(mouseX, mouseY)) {
			this.draggingThumb = true;
			this.scrollTo(mouseY);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (this.draggingThumb) {
			this.scrollTo(mouseY);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.draggingThumb = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private boolean overBar(double mouseX, double mouseY) {
		int left = this.leftPos + BAR_X;
		int top = this.topPos + BackpackMenu.GRID_TOP;

		return mouseX >= left && mouseX < left + BAR_WIDTH
				&& mouseY >= top && mouseY < top + this.barHeight;
	}

	/** Traduit une position verticale de souris en rangee de tete. */
	private void scrollTo(double mouseY) {
		int top = this.topPos + BackpackMenu.GRID_TOP;
		double travel = this.barHeight - THUMB_HEIGHT;

		double ratio = travel <= 0 ? 0.0 : (mouseY - top - THUMB_HEIGHT / 2.0) / travel;
		this.setScroll((int) Math.round(ratio * this.scrollRange));
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		// Partie haute : bordure et rangees visibles du sac.
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.gridHeight);
		// Partie basse : inventaire du joueur, pris tel quel dans la texture.
		graphics.blit(TEXTURE, this.leftPos, this.topPos + this.gridHeight, 0, PLAYER_SECTION_SOURCE_Y,
				this.imageWidth, BackpackMenu.PLAYER_SECTION_HEIGHT);

		if (this.scrollRange > 0) {
			this.renderScrollBar(graphics);
		}
	}

	/**
	 * La barre de defilement.
	 *
	 * <p>Dessinee ici et non dans la texture : elle ne doit apparaitre que pour les sacs qui
	 * defilent, ce qu'une image figee ne saurait dire.
	 */
	private void renderScrollBar(GuiGraphics graphics) {
		int left = this.leftPos + BAR_X;
		int top = this.topPos + BackpackMenu.GRID_TOP;

		graphics.fill(left, top, left + BAR_WIDTH, top + this.barHeight, GROOVE_COLOUR);
		graphics.fill(left, top, left + BAR_WIDTH, top + 1, GROOVE_SHADOW);
		graphics.fill(left, top, left + 1, top + this.barHeight, GROOVE_SHADOW);

		int travel = this.barHeight - THUMB_HEIGHT;
		int thumbTop = top + (this.scrollRange == 0 ? 0 : travel * this.scrollRow / this.scrollRange);

		graphics.fill(left + 1, thumbTop, left + BAR_WIDTH - 1, thumbTop + THUMB_HEIGHT, THUMB_COLOUR);
		graphics.fill(left + 1, thumbTop + THUMB_HEIGHT - 1, left + BAR_WIDTH - 1,
				thumbTop + THUMB_HEIGHT, THUMB_SHADOW);
		graphics.fill(left + BAR_WIDTH - 2, thumbTop, left + BAR_WIDTH - 1,
				thumbTop + THUMB_HEIGHT, THUMB_SHADOW);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
}
