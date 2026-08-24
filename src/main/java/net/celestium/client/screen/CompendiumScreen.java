package net.celestium.client.screen;

import net.celestium.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Le compendium : ce que le mod ajoute, et d'ou cela vient.
 *
 * <p>Ce n'est pas un visualiseur de recettes universel. Il ne connait que les objets du mod et ne
 * montre, pour chacun, que la premiere recette d'etabli qui le produise. C'est volontairement plus
 * etroit qu'un JEI : couvrir tout le jeu et tous les mods demanderait une indexation, une
 * recherche et des categories, la ou le besoin exprime etait de savoir ce que ce mod-ci apporte.
 *
 * <p>Ce qui ne se fabrique pas — les minerais, le coeur du demon, les blocs chance — le dit en
 * toutes lettres plutot que d'afficher une grille vide.
 */
public class CompendiumScreen extends Screen {

	private static final int WIDTH = 320;
	private static final int HEIGHT = 182;

	// La grille des objets, a gauche.
	private static final int GRID_LEFT = 10;
	private static final int GRID_TOP = 26;
	private static final int GRID_COLUMNS = 9;
	private static final int GRID_ROWS = 7;
	private static final int SLOT = 18;

	// La fiche, a droite.
	private static final int SHEET_LEFT = 190;

	private static final int BAR_X = 178;
	private static final int BAR_WIDTH = 8;
	private static final int THUMB_HEIGHT = 20;

	// Les gris du jeu de base, pour que le panneau ne jure pas a cote des siens.
	private static final int PANEL = 0xFFC6C6C6;
	private static final int PANEL_LIGHT = 0xFFFFFFFF;
	private static final int PANEL_DARK = 0xFF555555;
	private static final int INSET = 0xFF8B8B8B;
	private static final int INSET_DARK = 0xFF373737;
	private static final int TEXT = 0xFF404040;

	private final List<ItemStack> entries = new ArrayList<>();

	private int left;
	private int top;
	private int scrollRow;

	@Nullable
	private ItemStack selected;

	@Nullable
	private CraftingRecipe recipe;

	public CompendiumScreen() {
		super(Component.translatable("gui.celestium.compendium"));
	}

	@Override
	protected void init() {
		this.left = (this.width - WIDTH) / 2;
		this.top = (this.height - HEIGHT) / 2;

		if (this.entries.isEmpty()) {
			ModItems.ITEMS.getEntries().forEach(item -> this.entries.add(new ItemStack(item.get())));
			this.select(this.entries.isEmpty() ? null : this.entries.get(0));
		}
	}

	/** Retient l'objet choisi et cherche ce qui le fabrique. */
	private void select(@Nullable ItemStack stack) {
		this.selected = stack;
		this.recipe = stack == null ? null : craftingRecipeFor(stack);
	}

	/**
	 * La premiere recette d'etabli qui produise cet objet.
	 *
	 * <p>Les recettes sont lues sur le gestionnaire du client : le serveur les lui envoie a la
	 * connexion, donc elles sont deja la, a jour, et sans qu'il faille en tenir une copie.
	 */
	@Nullable
	private static CraftingRecipe craftingRecipeFor(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return null;
		}

		for (Recipe<?> candidate : minecraft.level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
			ItemStack result = candidate.getResultItem(minecraft.level.registryAccess());

			if (candidate instanceof CraftingRecipe crafting && ItemStack.isSameItem(result, stack)) {
				return crafting;
			}
		}
		return null;
	}

	private int maxScroll() {
		int rows = (this.entries.size() + GRID_COLUMNS - 1) / GRID_COLUMNS;
		return Math.max(0, rows - GRID_ROWS);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);

		panel(graphics, this.left, this.top, WIDTH, HEIGHT);
		graphics.drawString(this.font, this.title, this.left + 10, this.top + 10, TEXT, false);

		this.renderGrid(graphics, mouseX, mouseY);
		this.renderScrollBar(graphics);
		this.renderSheet(graphics);

		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderHoveredTooltip(graphics, mouseX, mouseY);
	}

	private void renderGrid(GuiGraphics graphics, int mouseX, int mouseY) {
		for (int row = 0; row < GRID_ROWS; row++) {
			for (int column = 0; column < GRID_COLUMNS; column++) {
				int index = (row + this.scrollRow) * GRID_COLUMNS + column;

				int x = this.left + GRID_LEFT + column * SLOT;
				int y = this.top + GRID_TOP + row * SLOT;
				inset(graphics, x, y, SLOT, SLOT);

				if (index >= this.entries.size()) {
					continue;
				}

				ItemStack stack = this.entries.get(index);
				graphics.renderItem(stack, x + 1, y + 1);

				if (this.selected != null && ItemStack.isSameItem(this.selected, stack)) {
					graphics.fill(x, y, x + SLOT, y + 1, 0xFFFFFFFF);
					graphics.fill(x, y + SLOT - 1, x + SLOT, y + SLOT, 0xFFFFFFFF);
					graphics.fill(x, y, x + 1, y + SLOT, 0xFFFFFFFF);
					graphics.fill(x + SLOT - 1, y, x + SLOT, y + SLOT, 0xFFFFFFFF);
				}
			}
		}
	}

	private void renderScrollBar(GuiGraphics graphics) {
		int x = this.left + BAR_X;
		int y = this.top + GRID_TOP;
		int height = GRID_ROWS * SLOT;

		inset(graphics, x, y, BAR_WIDTH, height);

		int range = this.maxScroll();
		int travel = height - THUMB_HEIGHT;
		int thumb = y + (range == 0 ? 0 : travel * this.scrollRow / range);

		graphics.fill(x + 1, thumb, x + BAR_WIDTH - 1, thumb + THUMB_HEIGHT, PANEL);
	}

	/** La fiche de l'objet choisi : son nom, et ce qui le fabrique. */
	private void renderSheet(GuiGraphics graphics) {
		if (this.selected == null) {
			return;
		}

		int x = this.left + SHEET_LEFT;
		int y = this.top + GRID_TOP;

		graphics.drawWordWrap(this.font, this.selected.getHoverName(), x, y, WIDTH - SHEET_LEFT - 12, TEXT);

		if (this.recipe == null) {
			graphics.drawWordWrap(this.font, Component.translatable("gui.celestium.compendium.no_recipe"),
					x, y + 24, WIDTH - SHEET_LEFT - 12, 0xFF707070);
			return;
		}

		this.renderRecipeGrid(graphics, x, y + 24);
	}

	/**
	 * La grille de fabrication, trois sur trois.
	 *
	 * <p>Une recette sans forme n'a pas de largeur declaree : ses ingredients sont poses a la suite,
	 * ce qui est exact — on peut les placer ou l'on veut.
	 */
	private void renderRecipeGrid(GuiGraphics graphics, int x, int y) {
		List<Ingredient> ingredients = this.recipe.getIngredients();
		int width = this.recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				int slotX = x + column * SLOT;
				int slotY = y + row * SLOT;
				inset(graphics, slotX, slotY, SLOT, SLOT);

				int index = row * width + column;
				if (column >= width || index >= ingredients.size()) {
					continue;
				}

				ItemStack[] options = ingredients.get(index).getItems();
				if (options.length == 0) {
					continue;
				}

				// Un ingredient declare par tag accepte plusieurs objets : on en montre un, qui
				// change au fil du temps, comme le fait le livre de recettes du jeu.
				int cycle = (int) (System.currentTimeMillis() / 1000L) % options.length;
				graphics.renderItem(options[cycle], slotX + 1, slotY + 1);
			}
		}

		int resultX = x + 3 * SLOT + 14;
		int resultY = y + SLOT;

		graphics.drawString(this.font, "->", x + 3 * SLOT + 2, resultY + 5, TEXT, false);
		inset(graphics, resultX, resultY, SLOT, SLOT);

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null) {
			ItemStack result = this.recipe.getResultItem(minecraft.level.registryAccess());
			graphics.renderItem(result, resultX + 1, resultY + 1);
			graphics.renderItemDecorations(this.font, result, resultX + 1, resultY + 1);
		}
	}

	private void renderHoveredTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
		ItemStack hovered = this.entryAt(mouseX, mouseY);
		if (hovered != null) {
			graphics.renderTooltip(this.font, hovered, mouseX, mouseY);
		}
	}

	/** L'objet sous le curseur dans la grille, s'il y en a un. */
	@Nullable
	private ItemStack entryAt(double mouseX, double mouseY) {
		int column = (int) ((mouseX - this.left - GRID_LEFT) / SLOT);
		int row = (int) ((mouseY - this.top - GRID_TOP) / SLOT);

		if (column < 0 || column >= GRID_COLUMNS || row < 0 || row >= GRID_ROWS) {
			return null;
		}

		int index = (row + this.scrollRow) * GRID_COLUMNS + column;
		return index >= 0 && index < this.entries.size() ? this.entries.get(index) : null;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		ItemStack clicked = this.entryAt(mouseX, mouseY);
		if (clicked != null) {
			this.select(clicked);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		this.scrollRow = Math.max(0, Math.min(this.maxScroll(), this.scrollRow - (int) Math.signum(delta)));
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	// --- Dessin ---

	private static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
		graphics.fill(x, y, x + width, y + height, PANEL);
		graphics.fill(x, y, x + width, y + 1, PANEL_LIGHT);
		graphics.fill(x, y, x + 1, y + height, PANEL_LIGHT);
		graphics.fill(x, y + height - 1, x + width, y + height, PANEL_DARK);
		graphics.fill(x + width - 1, y, x + width, y + height, PANEL_DARK);
	}

	private static void inset(GuiGraphics graphics, int x, int y, int width, int height) {
		graphics.fill(x, y, x + width, y + height, INSET);
		graphics.fill(x, y, x + width, y + 1, INSET_DARK);
		graphics.fill(x, y, x + 1, y + height, INSET_DARK);
		graphics.fill(x, y + height - 1, x + width, y + height, PANEL_LIGHT);
		graphics.fill(x + width - 1, y, x + width, y + height, PANEL_LIGHT);
	}
}
