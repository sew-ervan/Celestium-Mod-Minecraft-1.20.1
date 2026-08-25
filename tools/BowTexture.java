import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Dessine l'arc celeste et ses trois etapes de bande.
 *
 * <p>Un arc ne se dessine pas au masque de caracteres comme les autres textures du mod : ses quatre
 * images ne different que par la position de l'encoche, et les recopier a la main quatre fois
 * inviterait la faute. Le trait est donc calcule — la branche suit une arche, la corde relie les
 * deux pointes en passant par l'encoche, et l'encoche recule d'une etape a l'autre.
 *
 * <p>Hors compilation du mod. Se lance a la main :
 * {@code java BowTexture.java <racine des textures>}
 */
public final class BowTexture {

	private static final int SIZE = 16;

	/** Abscisse des deux pointes de la branche. */
	private static final double TIP_X = 11.0;

	/** Abscisse du ventre de la branche, la ou la main tient l'arc. */
	private static final double BELLY_X = 3.0;

	/** Hauteur de l'encoche, au milieu de la corde. */
	private static final int NOCK_Y = 7;

	/**
	 * Recul de l'encoche, par image.
	 *
	 * <p>La premiere vaut la corde au repos : l'encoche est alors sur la droite qui joint les deux
	 * pointes, et la corde est tendue. Les trois suivantes la tirent vers l'archer.
	 */
	private static final double[] NOCK_X = {11.0, 12.6, 13.8, 15.0};

	private static final String[] NAMES = {
			"celestial_bow.png",
			"celestial_bow_pulling_0.png",
			"celestial_bow_pulling_1.png",
			"celestial_bow_pulling_2.png",
	};

	/** Bois de la branche, dans le bleu du Celestium. */
	private static final int LIMB = 0xFF6FA8D8;

	/** Revers de la branche, du cote ou la lumiere ne tombe pas. */
	private static final int LIMB_DARK = 0xFF2E5A80;

	/** La poignee, en or : le seul endroit ou l'oeil s'arrete. */
	private static final int GRIP = 0xFFC9A227;

	/** Corde, d'un gris presque blanc. */
	private static final int STRING = 0xFFD8DCE4;

	/** Hampe de la fleche encochee. */
	private static final int SHAFT = 0xFF8A6A3B;

	/** Pointe de la fleche. */
	private static final int HEAD = 0xFFB9BEC6;

	/** Empennage. */
	private static final int FLETCHING = 0xFFE8ECFF;

	private BowTexture() {
	}

	public static void main(String[] args) throws IOException {
		Path items = Path.of(args[0]).resolve("item");

		for (int stage = 0; stage < NAMES.length; stage++) {
			BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

			limb(image);
			string(image, NOCK_X[stage]);
			if (stage > 0) {
				arrow(image, NOCK_X[stage]);
			}

			Path target = items.resolve(NAMES[stage]);
			ImageIO.write(image, "PNG", target.toFile());
			System.out.println("OK\t" + target);
		}
	}

	/**
	 * La branche : une arche qui part d'une pointe, passe par le ventre et rejoint l'autre pointe.
	 *
	 * <p>Le pas est fin devant le nombre de pixels a couvrir — mieux vaut repasser plusieurs fois
	 * sur le meme pixel que laisser un trou dans le trait.
	 */
	private static void limb(BufferedImage image) {
		for (double t = 0.0; t <= 1.0; t += 0.002) {
			double x = TIP_X - (TIP_X - BELLY_X) * Math.sin(Math.PI * t);
			int px = (int) Math.round(x);
			int py = (int) Math.round(t * (SIZE - 1));

			// Le tiers central est la poignee : c'est la que la main se pose, et la seule partie
			// qui ne plie pas.
			boolean onGrip = t > 0.42 && t < 0.58;

			set(image, px, py, onGrip ? GRIP : LIMB);
			set(image, px + 1, py, onGrip ? GRIP : LIMB_DARK);
		}
	}

	/** La corde : deux segments, d'une pointe a l'encoche et de l'encoche a l'autre pointe. */
	private static void string(BufferedImage image, double nockX) {
		line(image, TIP_X, 0, nockX, NOCK_Y, STRING);
		line(image, nockX, NOCK_Y, TIP_X, SIZE - 1, STRING);
	}

	/** La fleche encochee, couchee sur l'horizontale de l'encoche. */
	private static void arrow(BufferedImage image, double nockX) {
		int end = (int) Math.round(nockX);

		for (int x = 1; x <= end; x++) {
			set(image, x, NOCK_Y, SHAFT);
		}

		set(image, 0, NOCK_Y, HEAD);
		set(image, 1, NOCK_Y, HEAD);

		// L'empennage se voit de part et d'autre de la hampe, juste devant l'encoche.
		set(image, end - 1, NOCK_Y - 1, FLETCHING);
		set(image, end - 1, NOCK_Y + 1, FLETCHING);
	}

	/** Trace un segment, pixel par pixel. */
	private static void line(BufferedImage image, double x0, double y0, double x1, double y1, int argb) {
		double dx = x1 - x0;
		double dy = y1 - y0;
		int steps = (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy))) * 4;

		for (int i = 0; i <= steps; i++) {
			double t = (double) i / steps;
			set(image, (int) Math.round(x0 + dx * t), (int) Math.round(y0 + dy * t), argb);
		}
	}

	/** Pose un pixel, en ignorant ce qui sort du cadre. */
	private static void set(BufferedImage image, int x, int y, int argb) {
		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return;
		}
		image.setRGB(x, y, argb);
	}
}
