import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * Dessine la texture du coeur du demon.
 *
 * <p>Les autres textures du mod se derivent de celles du Celestium par corruption. Celle-ci n'a
 * aucun modele dont partir : elle est donc dessinee, pixel par pixel, a partir d'un masque en
 * caracteres. C'est plus lisible qu'une suite de coordonnees, et le dessin se relit d'un coup
 * d'oeil dans le code.
 *
 * <p>Hors compilation du mod. Se lance a la main :
 * {@code java DemonHeartTexture.java <racine des textures>}
 */
public final class DemonHeartTexture {

	/**
	 * Le dessin. Un caractere par pixel, seize sur seize.
	 *
	 * <p>{@code .} vide, {@code o} contour, {@code h} chair, {@code d} veine sombre,
	 * {@code l} reflet.
	 */
	private static final String[] MASK = {
			"................",
			"...oo......oo...",
			"..ohhoo..oohho..",
			".ohllhhoohhhhho.",
			".ohlhhhhhhhhhho.",
			".ohhhhdhhdhhhho.",
			".ohhhdhhhhdhhho.",
			"..ohhhhhhhhhho..",
			"..ohhdhhhhdhho..",
			"...ohhhhhhhho...",
			"....ohhhhhho....",
			".....ohhhho.....",
			"......ohho......",
			".......oo.......",
			"................",
			"................",
	};

	/** Contour presque noir, teinte de rouge : il detache le coeur du fond de l'inventaire. */
	private static final int OUTLINE = 0xFF1A0305;

	/** Chair du coeur, dans le rouge sombre des Terres du demon. */
	private static final int FLESH = 0xFF8E1116;

	/** Veines, plus sombres que la chair. */
	private static final int VEIN = 0xFF56090D;

	/** Reflet, en haut a gauche, la ou la lumiere tombe sur les items du jeu. */
	private static final int SHEEN = 0xFFC4443F;

	/** Amplitude du grain applique a la chair, en pas de couleur. */
	private static final int GRAIN = 14;

	private DemonHeartTexture() {
	}

	public static void main(String[] args) throws IOException {
		Path root = Path.of(args[0]);
		Path target = root.resolve("item").resolve("demon_heart.png");

		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < 16; y++) {
			String row = MASK[y];
			if (row.length() != 16) {
				throw new IllegalStateException("Ligne " + y + " du masque : " + row.length() + " pixels");
			}
			for (int x = 0; x < 16; x++) {
				image.setRGB(x, y, colourOf(row.charAt(x), x, y));
			}
		}

		ImageIO.write(image, "PNG", target.toFile());
		System.out.println("OK\t" + target);
	}

	private static int colourOf(char symbol, int x, int y) {
		return switch (symbol) {
			case 'o' -> OUTLINE;
			case 'h' -> grain(FLESH, x, y);
			case 'd' -> grain(VEIN, x, y);
			case 'l' -> SHEEN;
			default -> 0x00000000;
		};
	}

	/**
	 * Fait varier legerement la couleur d'un pixel a l'autre.
	 *
	 * <p>Un aplat uniforme jure a cote des textures du jeu de base, qui sont toutes bruitees. Le
	 * tirage depend des coordonnees et non du hasard courant : la texture est la meme a chaque
	 * execution.
	 */
	private static int grain(int argb, int x, int y) {
		Random noise = new Random(x * 7919L + y * 104729L);
		int shift = noise.nextInt(GRAIN * 2 + 1) - GRAIN;

		int red = clamp(((argb >> 16) & 0xFF) + shift);
		int green = clamp(((argb >> 8) & 0xFF) + shift / 2);
		int blue = clamp((argb & 0xFF) + shift / 2);

		return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}
}
