import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * Peaux de la licorne et des trois familiers.
 *
 * <p>Deux methodes, selon ce dont on part. Les deux familiers des dimensions reprennent la
 * geometrie de creatures qui existent deja : leur robe se derive de celle de leur grand frere, par
 * teinte et par eclaircissement, ce qui garde la parente visible. La licorne et le fennec, eux,
 * empruntent une geometrie du jeu de base dont il n'existe aucune peau qu'on ait le droit de
 * copier : la leur est donc peinte de zero, pave par pave, d'apres la decoupe du modele.
 *
 * <p>Hors compilation du mod. Se lance a la main :
 * {@code java FamiliarTextures.java <racine des textures>}
 */
public final class FamiliarTextures {

	/** Robe de la licorne : un blanc nacre, a peine bleute. */
	private static final int COAT = 0xFFF2F0F8;

	/** Criniere et queue, dans le bleu du Celestium. */
	private static final int MANE = 0xFF8FB8DE;

	/** Sabots et oeil, presque noirs. */
	private static final int DARK = 0xFF1B1622;

	/** Robe du fennec : le sable du desert. */
	private static final int SAND = 0xFFE0C08A;

	/** Ventre et bout de queue, plus pales. */
	private static final int PALE = 0xFFF5E9D0;

	/** Amplitude du grain, en pas de couleur. */
	private static final int GRAIN = 8;

	private FamiliarTextures() {
	}

	public static void main(String[] args) throws IOException {
		Path root = Path.of(args[0]);
		Path entity = root.resolve("entity");
		Path armor = root.resolve("models").resolve("armor");

		unicorn(entity.resolve("unicorn.png"));
		fennec(entity.resolve("fennec.png"));

		// Le petit gardien est plus pale que le gardien miniature : il n'a pas vecu sous terre.
		tint(entity.resolve("miniwarden_texture.png"), entity.resolve("mini_guardian.png"),
				1.25F, 0.55F, 0.10F);

		// Le petit demon est plus vif que le demon epeiste : le rouge d'une braise, pas d'un
		// charbon.
		tint(entity.resolve("demonepeiste.png"), entity.resolve("mini_demon.png"),
				1.35F, 0.75F, -0.02F);

		// Le couvre-chef en corne reprend la coupe du Celestium, blanchie et doree.
		tint(armor.resolve("celestium_layer_1.png"), armor.resolve("unicorn_horn_layer_1.png"),
				1.30F, 0.35F, 0.12F);
		tint(armor.resolve("celestium_layer_2.png"), armor.resolve("unicorn_horn_layer_2.png"),
				1.30F, 0.35F, 0.12F);
	}

	/**
	 * La peau de la licorne, sur la decoupe du cheval du jeu de base.
	 *
	 * <p>Presque tout est d'un blanc uni : c'est ce qui permet a la corne d'aller lire ses couleurs
	 * n'importe ou dans la zone du corps sans qu'on ait a lui reserver un coin. Seules la criniere
	 * et les yeux sont peints a leur place.
	 */
	private static void unicorn(Path target) throws IOException {
		BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		fill(image, COAT);

		// La criniere et la queue partagent le meme pave, en haut a droite : le modele les y
		// envoie toutes les deux.
		rect(image, 56, 36, 8, 18, MANE);

		// Les yeux tombent sur les deux flancs du crane. Le pave du crane commence en (0,13) pour
		// une boite de six sur cinq sur sept : le flanc droit occupe donc les colonnes 0 a 7 et le
		// flanc gauche les colonnes 13 a 20, l'avant de la tete etant du cote ou chaque flanc
		// touche la face de devant.
		rect(image, 5, 21, 1, 2, DARK);
		rect(image, 14, 21, 1, 2, DARK);

		write(image, target);
	}

	/**
	 * La peau du fennec, sur la decoupe de l'ocelot du jeu de base.
	 *
	 * <p>Sable dessus, plus pale dessous — la seule chose qui fasse vraiment un fennec, en dehors
	 * de sa taille, tient a ce contraste.
	 */
	private static void fennec(Path target) throws IOException {
		BufferedImage image = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
		fill(image, SAND);

		// Le dessous du corps, que le modele envoie en bas a gauche du pave du corps.
		rect(image, 20, 0, 4, 6, PALE);

		// Les oreilles, qui font tout le fennec : le modele leur reserve deux petits paves a partir
		// de la colonne zero et de la colonne six, sur la ligne dix.
		rect(image, 0, 10, 4, 2, PALE);
		rect(image, 6, 10, 4, 2, PALE);

		// Les yeux et le museau, sur la face avant de la tete : les colonnes 5 a 10, lignes 5 a 9.
		rect(image, 6, 6, 1, 1, DARK);
		rect(image, 8, 6, 1, 1, DARK);
		rect(image, 7, 7, 1, 1, DARK);

		write(image, target);
	}

	/**
	 * Reprend une peau existante en la deplacant vers une autre teinte.
	 *
	 * @param lift     facteur applique a la luminosite
	 * @param saturate saturation visee, entre zero et un
	 * @param hueShift decalage de teinte, en tours complets
	 */
	private static void tint(Path from, Path to, float lift, float saturate, float hueShift)
			throws IOException {

		BufferedImage source = ImageIO.read(from.toFile());
		BufferedImage result = new BufferedImage(
				source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				int argb = source.getRGB(x, y);
				int alpha = argb >>> 24;

				if (alpha == 0) {
					continue;
				}

				float[] hsb = java.awt.Color.RGBtoHSB(
						(argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, null);

				float hue = (hsb[0] + hueShift + 1.0F) % 1.0F;
				float brightness = Math.min(1.0F, hsb[2] * lift);

				int rgb = java.awt.Color.HSBtoRGB(hue, saturate, brightness);
				result.setRGB(x, y, (alpha << 24) | (rgb & 0xFFFFFF));
			}
		}

		write(result, to);
	}

	/** Remplit toute l'image, avec un grain pour ne pas jurer a cote des peaux du jeu de base. */
	private static void fill(BufferedImage image, int argb) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				image.setRGB(x, y, grain(argb, x, y));
			}
		}
	}

	/** Repeint un pave rectangulaire. */
	private static void rect(BufferedImage image, int x0, int y0, int width, int height, int argb) {
		for (int y = y0; y < y0 + height && y < image.getHeight(); y++) {
			for (int x = x0; x < x0 + width && x < image.getWidth(); x++) {
				image.setRGB(x, y, grain(argb, x, y));
			}
		}
	}

	/**
	 * Fait varier legerement la couleur d'un pixel a l'autre.
	 *
	 * <p>Le tirage depend des coordonnees et non du hasard courant : relancer l'outil reecrit
	 * exactement les memes images, donc sans difference parasite dans le depot.
	 */
	private static int grain(int argb, int x, int y) {
		Random noise = new Random(x * 7919L + y * 104729L);
		int shift = noise.nextInt(GRAIN * 2 + 1) - GRAIN;

		int red = clamp(((argb >> 16) & 0xFF) + shift);
		int green = clamp(((argb >> 8) & 0xFF) + shift);
		int blue = clamp((argb & 0xFF) + shift);

		return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}

	private static void write(BufferedImage image, Path target) throws IOException {
		ImageIO.write(image, "PNG", target.toFile());
		System.out.println("OK\t" + target);
	}
}
