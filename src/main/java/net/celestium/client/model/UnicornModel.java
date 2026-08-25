package net.celestium.client.model;

import net.celestium.CelestiumMod;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

/**
 * Le cheval du jeu de base, plus une corne.
 *
 * <p>Tout le reste est repris tel quel : la silhouette, les allures, le cabrage, la reduction du
 * poulain. Redessiner un cheval pour en changer un seul detail aurait coute cher et n'aurait rien
 * apporte — c'est la corne qui fait la licorne, pas la croupe.
 *
 * <p>La corne est greffee sur le crane et non sur l'ensemble de la tete : le crane est la partie qui
 * suit le regard, si bien que la corne pointe la ou la bete regarde. Greffee un cran plus haut, elle
 * serait restee droite pendant que la tete se baisse.
 */
public class UnicornModel<T extends AbstractHorse> extends HorseModel<T> {

	/** L'emplacement sous lequel le jeu retient cette geometrie. */
	public static final ModelLayerLocation LAYER =
			new ModelLayerLocation(CelestiumMod.id("unicorn"), "main");

	/**
	 * Coin de la texture ou la corne prend ses couleurs.
	 *
	 * <p>Il tombe volontairement dans la zone du corps, qui est d'un blanc uni : la corne y prend
	 * donc la couleur de la robe, ce qui est exactement ce qu'on veut. Une texture de cheval de
	 * soixante-quatre pixels de cote n'a aucun coin libre — mais rien n'interdit a deux morceaux de
	 * lire les memes pixels, et c'est ce qui evite d'avoir a agrandir la texture.
	 */
	private static final int HORN_U = 20;
	private static final int HORN_V = 40;

	public UnicornModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HorseModel.createBodyMesh(CubeDeformation.NONE);
		PartDefinition skull = mesh.getRoot().getChild("head_parts").getChild("head");

		// Le crane occupe les hauteurs -11 a -6 : la corne part de son sommet et monte de sept
		// crans, legerement en avant, la ou une corne pousse.
		skull.addOrReplaceChild("horn",
				CubeListBuilder.create()
						.texOffs(HORN_U, HORN_V)
						.addBox(-0.5F, -18.0F, -3.0F, 1.0F, 7.0F, 1.0F),
				PartPose.ZERO);

		return LayerDefinition.create(mesh, 64, 64);
	}
}
