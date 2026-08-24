import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * Dessine les textures que rien ne permet de deriver.
 *
 * <p>Les autres textures du mod se derivent de celles du Celestium par corruption. Celle-ci n'a
 * aucun modele dont partir : elle est donc dessinee, pixel par pixel, a partir d'un masque en
 * caracteres. C'est plus lisible qu'une suite de coordonnees, et le dessin se relit d'un coup
 * d'oeil dans le code.
 *
 * <p>Hors compilation du mod. Se lance a la main :
 * {@code java PixelArtTextures.java <racine des textures>}
 */
public final class PixelArtTextures {

	/**
	 * Le coeur du demon. Un caractere par pixel, seize sur seize.
	 *
	 * <p>{@code .} vide, {@code o} contour, {@code h} chair, {@code d} veine sombre,
	 * {@code l} reflet.
	 */
	private static final String[] HEART = {
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

	private PixelArtTextures() {
	}

	/**
	 * L'oeil corrompu, celui qui garnit un cadre.
	 *
	 * <p>Meme alphabet que le coeur : {@code o} contour, {@code h} sclerotique, {@code d} iris,
	 * {@code l} pupille et reflet.
	 */
	private static final String[] EYE = {
			"................",
			"................",
			"....oooooooo....",
			"..oohhhhhhhhoo..",
			".ohhhhhhhhhhhho.",
			".ohhhhddddhhhho.",
			"ohhhhddddddhhhho",
			"ohhhdddllddddhho",
			"ohhhdddlldddhhho",
			"ohhhhddddddhhhho",
			".ohhhhddddhhhho.",
			".ohhhhhhhhhhhho.",
			"..oohhhhhhhhoo..",
			"....oooooooo....",
			"................",
			"................",
	};

	/**
	 * Le livre corrompu : une tranche a gauche, des pages a droite, et une marque au milieu.
	 *
	 * <p>{@code o} contour, {@code S} tranche, {@code p} pages, {@code c} la marque.
	 */
	private static final String[] BOOK = {
			"................",
			"................",
			"..oooooooooooo..",
			"..oSSoppppppppo.",
			"..oSSoppppppppo.",
			"..oSSoppppppppo.",
			"..oSSoppppppppo.",
			"..oSSopppccpppo.",
			"..oSSopppccpppo.",
			"..oSSoppppppppo.",
			"..oSSoppppppppo.",
			"..oSSoppppppppo.",
			"..oSSoppppppppo.",
			"..oooooooooooo..",
			"................",
			"................",
	};

	/** Tranche du livre, dans le rouge du Celestium corrompu. */
	private static final int SPINE = 0xFF8E1116;

	/** Pages, d'un blanc use. */
	private static final int PAGES = 0xFFCFC6A8;

	/** La marque qui court sur les pages. */
	private static final int MARK = 0xFFB4331F;

	/**
	 * La fiole de poussiere celeste.
	 *
	 * <p>{@code o} verre, {@code d} contenu, {@code l} reflet sur le verre.
	 */
	private static final String[] FLASK = {
			"................",
			"................",
			"......oooo......",
			"......o..o......",
			"......o..o......",
			".....oo..oo.....",
			"....oo....oo....",
			"...oo......oo...",
			"...ol.dddd.oo...",
			"...ol.dddd..o...",
			"...o..dddd..o...",
			"...o..dddd..o...",
			"...oooooooooo...",
			"................",
			"................",
			"................",
	};

	/** Verre de la fiole, gris tres clair. */
	private static final int GLASS = 0xFFB6C4CE;

	/** Contenu de la fiole : le bleu du Celestium. */
	private static final int DUST = 0xFF5FA8D3;

	/** Blanc verdatre de l'oeil, du cote de l'Overworld. */
	private static final int SCLERA = 0xFFB9BE8E;

	/** Iris, du cote du Nether. */
	private static final int IRIS = 0xFF7A3018;

	/** Pupille, presque noire. */
	private static final int PUPIL = 0xFF14090A;

	public static void main(String[] args) throws IOException {
		Path items = Path.of(args[0]).resolve("item");

		draw(HEART, items.resolve("demon_heart.png"), PixelArtTextures::heartColour);
		draw(EYE, items.resolve("corrupted_eye.png"), PixelArtTextures::eyeColour);
		draw(BOOK, items.resolve("corrupted_book.png"), PixelArtTextures::bookColour);
		draw(FLASK, items.resolve("celestial_dust.png"), PixelArtTextures::flaskColour);
	}

	/** Applique un masque et ecrit l'image. */
	private static void draw(String[] mask, Path target, Palette palette) throws IOException {
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < 16; y++) {
			String row = mask[y];
			if (row.length() != 16) {
				throw new IllegalStateException("Ligne " + y + " du masque : " + row.length() + " pixels");
			}
			for (int x = 0; x < 16; x++) {
				image.setRGB(x, y, palette.colourOf(row.charAt(x), x, y));
			}
		}

		ImageIO.write(image, "PNG", target.toFile());
		System.out.println("OK\t" + target);
	}

	/** Traduit un caractere du masque en couleur. */
	@FunctionalInterface
	private interface Palette {
		int colourOf(char symbol, int x, int y);
	}

	private static int heartColour(char symbol, int x, int y) {
		return colourOf(symbol, x, y);
	}

	private static int flaskColour(char symbol, int x, int y) {
		return switch (symbol) {
			case 'o' -> grain(GLASS, x, y);
			case 'd' -> grain(DUST, x, y);
			case 'l' -> 0xFFFFFFFF;
			default -> 0x00000000;
		};
	}

	private static int bookColour(char symbol, int x, int y) {
		return switch (symbol) {
			case 'o' -> OUTLINE;
			case 'S' -> grain(SPINE, x, y);
			case 'p' -> grain(PAGES, x, y);
			case 'c' -> grain(MARK, x, y);
			default -> 0x00000000;
		};
	}

	private static int eyeColour(char symbol, int x, int y) {
		return switch (symbol) {
			case 'o' -> OUTLINE;
			case 'h' -> grain(SCLERA, x, y);
			case 'd' -> grain(IRIS, x, y);
			case 'l' -> PUPIL;
			default -> 0x00000000;
		};
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
