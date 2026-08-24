import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * Dessine la peau du dragon celeste.
 *
 * <p>Elle n'emprunte rien : ni decoupe ni retouche d'une texture existante. Le modele du phantasme
 * plaque sa peau sur toute la bete sans zone distincte, ce qui permet de la peindre uniformement —
 * un bleu de nuit, module par un bruit doux, seme d'etoiles.
 *
 * <p>La modulation compte autant que la couleur. Un aplat parfait donnerait une bete en plastique ;
 * le degrade vertical et le grain lui rendent du volume la ou le modele n'en a pas.
 *
 * <p>Hors compilation du mod. Se lance a la main :
 * {@code java DragonTexture.java <racine des textures>}
 */
public final class DragonTexture {

	/** Le modele du phantasme lit une feuille de soixante-quatre sur trente-deux. */
	private static final int WIDTH = 64;
	private static final int HEIGHT = 32;

	/** Bleu de nuit du dos, et bleu plus clair du ventre. */
	private static final int TOP = 0xFF1B2A5E;
	private static final int BOTTOM = 0xFF3E6BB8;

	/** Proportion de pixels d'etoile. */
	private static final double STAR_RATE = 0.05;

	/** Amplitude du grain, en pas de couleur. */
	private static final int GRAIN = 12;

	private DragonTexture() {
	}

	public static void main(String[] args) throws IOException {
		Path target = Path.of(args[0]).resolve("entity").resolve("celestial_dragon.png");
		target.getParent().toFile().mkdirs();

		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				image.setRGB(x, y, pixel(x, y));
			}
		}

		ImageIO.write(image, "PNG", target.toFile());
		System.out.printf("OK\t%s\t%dx%d%n", target, WIDTH, HEIGHT);
	}

	private static int pixel(int x, int y) {
		Random noise = new Random(x * 7919L + y * 104729L);

		if (noise.nextDouble() < STAR_RATE) {
			return 0xFFE6ECFF;
		}

		// Degrade du dos vers le ventre, puis grain.
		float ratio = (float) y / (HEIGHT - 1);
		int base = blend(TOP, BOTTOM, ratio);
		int shift = noise.nextInt(GRAIN * 2 + 1) - GRAIN;

		return 0xFF000000
				| (clamp(((base >> 16) & 0xFF) + shift) << 16)
				| (clamp(((base >> 8) & 0xFF) + shift) << 8)
				| clamp((base & 0xFF) + shift);
	}

	private static int blend(int from, int to, float ratio) {
		int red = Math.round(((from >> 16) & 0xFF) * (1 - ratio) + ((to >> 16) & 0xFF) * ratio);
		int green = Math.round(((from >> 8) & 0xFF) * (1 - ratio) + ((to >> 8) & 0xFF) * ratio);
		int blue = Math.round((from & 0xFF) * (1 - ratio) + (to & 0xFF) * ratio);

		return (red << 16) | (green << 8) | blue;
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}
}
