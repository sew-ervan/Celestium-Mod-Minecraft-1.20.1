import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * Derive les textures du Demonium de celles du Celestium.
 *
 * <p>Le Demonium est le Celestium corrompu : meme dessin, meme silhouette, mais la teinte celeste
 * vire au rouge, l'ensemble s'assombrit, et une corruption pixel a pixel vient bruler la surface.
 * La parente entre les deux materiaux doit rester lisible d'un coup d'oeil.
 *
 * <p>Le bruit est deterministe : la graine derive du nom du fichier et de la position du pixel.
 * Relancer l'outil produit exactement les memes images, ce qui evite des differences parasites
 * dans le depot.
 */
public final class DemoniumTextures {

	/** Largeur de la bande rouge conservee, pour garder le relief du dessin d'origine. */
	private static final float HUE_BASE = 0.98F;
	private static final float HUE_SPREAD = 0.06F;

	private static final float MIN_SATURATION = 0.55F;
	private static final float DARKEN = 0.82F;

	/** Proportion de pixels brules, puis de braises. */
	private static final double BURNT_RATE = 0.12;
	private static final double EMBER_RATE = 0.06;

	private static final float BURNT_FACTOR = 0.45F;
	private static final float EMBER_FACTOR = 1.55F;

	private static final String[][] TEXTURES = {
			{"item", "fragment"}, {"item", "ingot"}, {"item", "stick"},
			{"item", "sword"}, {"item", "pickaxe"}, {"item", "axe"},
			{"item", "shovel"}, {"item", "hoe"},
			{"item", "helmet"}, {"item", "chestplate"}, {"item", "leggings"}, {"item", "boots"},
			{"block", "ore"}, {"block", "block"},
			{"models/armor", "layer_1"}, {"models/armor", "layer_2"},
	};

	private DemoniumTextures() {
	}

	public static void main(String[] args) throws IOException {
		Path root = Path.of(args[0]);
		int written = 0;

		for (String[] texture : TEXTURES) {
			Path source = root.resolve(texture[0]).resolve("celestium_" + texture[1] + ".png");
			Path target = root.resolve(texture[0]).resolve("demonium_" + texture[1] + ".png");

			if (!source.toFile().isFile()) {
				System.out.println("ABSENT  " + source);
				continue;
			}

			BufferedImage image = ImageIO.read(source.toFile());
			BufferedImage corrupted = corrupt(image, texture[1]);
			ImageIO.write(corrupted, "PNG", target.toFile());

			System.out.printf("OK      %-46s %dx%d%n", target, image.getWidth(), image.getHeight());
			written++;
		}

		partialCorruption(root, "block/celestium_block.png", "block/corrupted_celestium_block.png");
		partialCorruption(root, "item/celestium_ingot.png", "item/corrupted_celestium_ingot.png");
		partialCorruption(root, "item/celestium_helmet.png", "item/corrupted_celestium_helmet.png");
		partialCorruption(root, "item/celestium_chestplate.png", "item/corrupted_celestium_chestplate.png");
		partialCorruption(root, "item/celestium_leggings.png", "item/corrupted_celestium_leggings.png");
		partialCorruption(root, "item/celestium_boots.png", "item/corrupted_celestium_boots.png");
		partialCorruption(root, "models/armor/celestium_layer_1.png", "models/armor/corrupted_celestium_layer_1.png");
		partialCorruption(root, "models/armor/celestium_layer_2.png", "models/armor/corrupted_celestium_layer_2.png");
		derive(root, "entity/miniwarden_texture.png", "entity/parasite.png", "parasite");
		derive(root, "block/celestium_block.png", "block/summoning_altar.png", "autel");
		System.out.println((written + 10) + " textures ecrites");
	}

	/**
	 * Le Celestium corrompu : une corruption a mi-chemin.
	 *
	 * <p>Le bloc melange pixel a pixel la version saine et la version corrompue. Il ne ressemble
	 * donc ni a l'un ni a l'autre : c'est un bloc de Celestium que la corruption gagne, ce qui
	 * raconte visuellement a quoi sert le cadre du portail.
	 */
	/** Corrompt une texture quelconque vers une destination libre. */
	private static void derive(Path root, String from, String to, String seed) throws IOException {
		Path source = root.resolve(from);
		if (!source.toFile().isFile()) {
			System.out.println("ABSENT  " + source);
			return;
		}
		BufferedImage image = ImageIO.read(source.toFile());
		Path target = root.resolve(to);
		ImageIO.write(corrupt(image, seed), "PNG", target.toFile());
		System.out.printf("OK      %-46s %dx%d%n", target, image.getWidth(), image.getHeight());
	}

	private static void partialCorruption(Path root, String from, String to) throws IOException {
		Path source = root.resolve(from);
		if (!source.toFile().isFile()) {
			System.out.println("ABSENT  " + source);
			return;
		}
		BufferedImage clean = ImageIO.read(source.toFile());
		BufferedImage tainted = corrupt(clean, "corrupted_celestium");
		BufferedImage blend = new BufferedImage(clean.getWidth(), clean.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < clean.getHeight(); y++) {
			for (int x = 0; x < clean.getWidth(); x++) {
				Random noise = new Random(7L + x * 6151L + y * 45989L);
				blend.setRGB(x, y, noise.nextDouble() < 0.45 ? tainted.getRGB(x, y) : clean.getRGB(x, y));
			}
		}

		Path target = root.resolve(to);
		ImageIO.write(blend, "PNG", target.toFile());
		System.out.printf("OK      %-46s melange%n", target);
	}

	private static BufferedImage corrupt(BufferedImage source, String seedName) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = source.getRGB(x, y);
				int alpha = (argb >>> 24) & 0xFF;

				// Un pixel transparent le reste : la silhouette de l'objet ne change pas.
				if (alpha == 0) {
					result.setRGB(x, y, 0);
					continue;
				}

				result.setRGB(x, y, (alpha << 24) | (corruptPixel(argb, seedName, x, y) & 0x00FFFFFF));
			}
		}
		return result;
	}

	private static int corruptPixel(int argb, String seedName, int x, int y) {
		float[] hsb = Color.RGBtoHSB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, null);

		// La teinte d'origine est ecrasee dans une etroite bande rouge. Conserver une part de sa
		// variation evite un aplat uniforme et preserve le modele du dessin.
		float hue = (HUE_BASE + hsb[0] * HUE_SPREAD) % 1.0F;
		float saturation = Math.min(1.0F, Math.max(MIN_SATURATION, hsb[1] * 1.3F));
		float brightness = hsb[2] * DARKEN;

		Random noise = new Random(seedName.hashCode() * 31L + x * 7919L + y * 104729L);
		double roll = noise.nextDouble();

		if (roll < BURNT_RATE) {
			// Pixel brule : la corruption a mange la matiere.
			brightness *= BURNT_FACTOR;
			saturation = Math.min(1.0F, saturation * 1.2F);
		} else if (roll < BURNT_RATE + EMBER_RATE) {
			// Braise : un point incandescent qui perce la surface.
			brightness = Math.min(1.0F, brightness * EMBER_FACTOR);
			saturation = Math.max(0.35F, saturation * 0.75F);
		}

		return Color.HSBtoRGB(hue, saturation, Math.min(1.0F, brightness));
	}
}
