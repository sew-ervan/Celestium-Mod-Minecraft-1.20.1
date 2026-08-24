import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Dessine les fonds d'interface du mod.
 *
 * <p>Les interfaces du jeu de base sont faites de deux formes seulement : un panneau en relief et
 * des emplacements en creux. Les redessiner ici plutot que de decouper une texture existante permet
 * de choisir librement les dimensions — le sac enorme demande un panneau plus large que le coffre
 * vanilla, pour loger sa barre de defilement.
 *
 * <p>Hors compilation du mod. Se lance a la main :
 * {@code java GuiTextures.java <racine des textures>}
 */
public final class GuiTextures {

	/** Toute texture d'interface fait 256 sur 256, quelle que soit la taille du panneau dedans. */
	private static final int SHEET = 256;

	// Les quatre gris du jeu de base. Les respecter est ce qui fait qu'une interface ajoutee ne
	// jure pas a cote de celles du jeu.
	private static final int PANEL = 0xFFC6C6C6;
	private static final int PANEL_LIGHT = 0xFFFFFFFF;
	private static final int PANEL_DARK = 0xFF555555;
	private static final int SLOT = 0xFF8B8B8B;
	private static final int SLOT_DARK = 0xFF373737;

	/** Cote d'un emplacement, bordure comprise. */
	private static final int SLOT_SIZE = 18;

	private GuiTextures() {
	}

	public static void main(String[] args) throws IOException {
		Path gui = Path.of(args[0]).resolve("gui");
		gui.toFile().mkdirs();

		enchantingTable(gui.resolve("corrupted_enchanting_table.png"));
		backpack(gui.resolve("backpack.png"));
	}

	/**
	 * La table corrompue : un emplacement pour l'outil, et de la place pour quatre propositions.
	 *
	 * <p>Quatre, parce que c'est ce qu'une pioche peut recevoir — excavation, filon, fonte et
	 * aimant. En montrer trois obligerait a en cacher un jusqu'a ce qu'un autre soit au maximum,
	 * ce qui donnerait l'impression que la table change d'avis.
	 *
	 * <p>Les propositions elles-memes sont dessinees par l'ecran et non ici : leur apparence depend
	 * de ce que le joueur peut se payer, ce qu'une image figee ne saurait dire.
	 */
	private static void enchantingTable(Path target) throws IOException {
		BufferedImage image = blank();

		panel(image, 0, 0, 176, 202);
		slot(image, 16, 49);

		playerInventory(image, 8, 202);

		ImageIO.write(image, "PNG", target.toFile());
		System.out.println("OK\t" + target + "\t176x202");
	}

	/**
	 * Le sac : six rangees visibles et une gouttiere de defilement.
	 *
	 * <p>Six est la limite du confort — c'est la hauteur d'un grand coffre, et au-dela l'interface
	 * deborde de l'ecran aux echelles courantes. Les sacs plus grands ne montrent donc pas tout d'un
	 * coup : ils defilent.
	 *
	 * <p>Le panneau est plus large que celui d'un coffre pour loger la barre de defilement sans
	 * mordre sur la derniere colonne. La barre elle-meme est dessinee par l'ecran : elle ne doit
	 * apparaitre que pour les sacs qui defilent, ce qu'une image figee ne saurait dire.
	 */
	private static void backpack(Path target) throws IOException {
		BufferedImage image = blank();

		panel(image, 0, 0, 200, 221);

		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 9; column++) {
				slot(image, 19 + column * SLOT_SIZE, 17 + row * SLOT_SIZE);
			}
		}

		playerInventory(image, 19, 221);

		ImageIO.write(image, "PNG", target.toFile());
		System.out.println("OK\t" + target + "\t200x221");
	}

	/**
	 * Les trois rangees et la barre d'acces rapide, aux reperes du jeu de base.
	 *
	 * <p>Quatre-vingt-deux pixels au-dessus du bas pour la premiere rangee, vingt-quatre pour la
	 * barre : ce sont les valeurs de tous les conteneurs vanilla, et les respecter evite d'avoir a
	 * recalculer les positions des emplacements dans le menu.
	 */
	private static void playerInventory(BufferedImage image, int left, int panelHeight) {
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				slot(image, left + column * SLOT_SIZE, panelHeight - 82 + row * SLOT_SIZE);
			}
		}
		for (int column = 0; column < 9; column++) {
			slot(image, left + column * SLOT_SIZE, panelHeight - 24);
		}
	}

	private static BufferedImage blank() {
		return new BufferedImage(SHEET, SHEET, BufferedImage.TYPE_INT_ARGB);
	}

	/** Un panneau en relief : clair en haut a gauche, sombre en bas a droite. */
	private static void panel(BufferedImage image, int x, int y, int width, int height) {
		fill(image, x, y, width, height, PANEL);

		line(image, x, y, width, 1, PANEL_LIGHT);
		line(image, x, y, 1, height, PANEL_LIGHT);
		line(image, x, y + height - 1, width, 1, PANEL_DARK);
		line(image, x + width - 1, y, 1, height, PANEL_DARK);
	}

	/** Un emplacement : un creux de seize pixels, borde d'un pixel. */
	private static void slot(BufferedImage image, int x, int y) {
		inset(image, x - 1, y - 1, SLOT_SIZE, SLOT_SIZE);
	}

	/** Un creux : sombre en haut a gauche, clair en bas a droite — l'inverse du relief. */
	private static void inset(BufferedImage image, int x, int y, int width, int height) {
		fill(image, x, y, width, height, SLOT);

		line(image, x, y, width, 1, SLOT_DARK);
		line(image, x, y, 1, height, SLOT_DARK);
		line(image, x, y + height - 1, width, 1, PANEL_LIGHT);
		line(image, x + width - 1, y, 1, height, PANEL_LIGHT);
	}

	private static void fill(BufferedImage image, int x, int y, int width, int height, int colour) {
		line(image, x, y, width, height, colour);
	}

	private static void line(BufferedImage image, int x, int y, int width, int height, int colour) {
		for (int dy = 0; dy < height; dy++) {
			for (int dx = 0; dx < width; dx++) {
				int px = x + dx;
				int py = y + dy;
				if (px >= 0 && px < SHEET && py >= 0 && py < SHEET) {
					image.setRGB(px, py, colour);
				}
			}
		}
	}
}
