package net.celestium.gametest;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.core.material.ModArmorMaterials;
import net.celestium.feature.celestium.ArmorSetEffects;
import net.celestium.feature.magie.Faction;
import net.celestium.feature.mob.DemonSwordsmanEntity;
import net.celestium.feature.portal.CelestialPortalShape;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/**
 * Tests en jeu du mod.
 *
 * <p>Se lancent avec {@code gradlew runGameTestServer}, qui echoue si un test echoue. Ils
 * s'executent tous dans une arene vide de trois blocs de cote, decrite par
 * {@code data/celestium/structures/gametest/empty.nbt}.
 */
@GameTestHolder(CelestiumMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class CelestiumGameTests {

	/**
	 * Forge prefixe automatiquement l'espace de noms declare par {@code @GameTestHolder} :
	 * ce nom devient {@code celestium:empty} et pointe sur
	 * {@code data/celestium/structures/empty.nbt}. L'ecrire qualifie ici donnerait
	 * {@code celestium:celestium:empty}.
	 */
	private static final String ARENA = "empty";

	/**
	 * Le minerai rend des fragments, jamais rien.
	 *
	 * <p>Le mod d'origine n'avait pas de table de butin et rendait ses objets depuis
	 * {@code getDrops} : ce test aurait alors porte sur du code, pas sur une donnee.
	 */
	@GameTest(template = ARENA)
	public static void celestiumOreDropsFragments(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, ModBlocks.CELESTIUM_ORE.get());

		List<ItemStack> drops = dropsOf(helper, pos);

		helper.assertTrue(!drops.isEmpty(), "Le minerai de Celestium ne rend rien");
		helper.assertTrue(
				drops.stream().allMatch(stack -> stack.is(ModItems.CELESTIUM_FRAGMENT.get())),
				"Le minerai de Celestium rend autre chose que des fragments");
		helper.assertTrue(
				drops.stream().mapToInt(ItemStack::getCount).sum() >= 2,
				"Le minerai de Celestium rend moins de deux fragments");

		helper.succeed();
	}

	/** Le bloc chance rend exactement un objet, tire dans le tag des recompenses. */
	@GameTest(template = ARENA)
	public static void luckyBlockDropsOneReward(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, ModBlocks.LUCKY_BLOCK.get());

		List<ItemStack> drops = dropsOf(helper, pos);

		helper.assertTrue(drops.size() == 1,
				"Le bloc chance a rendu " + drops.size() + " objets au lieu d'un seul");
		helper.assertTrue(drops.get(0).is(ModTags.Items.LUCKY_BLOCK_REWARDS),
				"Le bloc chance a rendu un objet absent du tag des recompenses");
		helper.assertTrue(!drops.get(0).is(ModBlocks.LUCKY_BLOCK.get().asItem()),
				"Le bloc chance se rend lui-meme");

		helper.succeed();
	}

	/** Le casque en Celestium accorde la vision nocturne des qu'il est porte. */
	@GameTest(template = ARENA)
	public static void celestiumHelmetGrantsNightVision(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.CELESTIUM_HELMET.get()));

		ArmorSetEffects.applyFor(ModArmorMaterials.CELESTIUM, ArmorItem.Type.HELMET, player);

		helper.assertTrue(player.hasEffect(MobEffects.NIGHT_VISION),
				"Le casque en Celestium n'accorde pas la vision nocturne");

		// Le refactor tient dans ce point : un effet encore largement valide n'est pas reconstruit
		// a chaque tick. Les trois autres pieces le faisaient vingt fois par seconde.
		int durationBefore = player.getEffect(MobEffects.NIGHT_VISION).getDuration();
		ArmorSetEffects.applyFor(ModArmorMaterials.CELESTIUM, ArmorItem.Type.HELMET, player);
		int durationAfter = player.getEffect(MobEffects.NIGHT_VISION).getDuration();

		helper.assertTrue(durationAfter == durationBefore,
				"L'effet du casque est reapplique alors qu'il est encore valide");

		helper.succeed();
	}

	/**
	 * Les planches du bois du demon brulent.
	 *
	 * <p>MCreator surchargeait {@code getFlammability} dans chacune des dix classes de blocs ;
	 * Forge attend une declaration centralisee, faite au demarrage. Ce test verifie qu'elle a bien
	 * eu lieu.
	 */
	@GameTest(template = ARENA)
	public static void demonWoodBurns(GameTestHelper helper) {
		BlockState planks = ModBlocks.BOIS_DU_DEMON.planks.get().defaultBlockState();
		BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));

		// L'interrogation passe par l'extension Forge de BlockState, pas par le bloc de feu.
		helper.assertTrue(planks.getFlammability(helper.getLevel(), pos, Direction.UP) > 0,
				"Les planches du bois du demon ne sont pas inflammables");
		helper.assertTrue(planks.getFireSpreadSpeed(helper.getLevel(), pos, Direction.UP) > 0,
				"Le feu ne se propage pas au bois du demon");

		helper.succeed();
	}

	/**
	 * Le demon bascule en seconde phase sous la moitie de ses points de vie.
	 *
	 * <p>Le sol est pose explicitement : l'arene de test ne contient que de l'air, et une creature
	 * qui tombe dans le vide cesse de reflechir avant d'avoir pu changer de phase.
	 */
	@GameTest(template = ARENA, timeoutTicks = 200)
	public static void demonSwordsmanEntersSecondPhase(GameTestHelper helper) {
		helper.setBlock(new BlockPos(1, 0, 1), Blocks.STONE);

		DemonSwordsmanEntity demon = helper.spawn(ModEntities.DEMON_SWORDSMAN.get(), 1, 1, 1);

		helper.assertTrue(!demon.isSecondPhase(),
				"Le demon commence deja en seconde phase");

		demon.setHealth(demon.getMaxHealth() * 0.4F);

		helper.succeedWhen(() -> {
			helper.assertTrue(demon.isSecondPhase(),
					"Le demon ne bascule pas en seconde phase sous la moitie de ses points de vie");
			helper.assertTrue(
					demon.getAttributeValue(Attributes.ATTACK_DAMAGE) > 14.0,
					"La seconde phase n'augmente pas les degats du demon");
		});
	}

	/** Le camp celeste est le seul a echapper a la magie celeste. */
	@GameTest(template = ARENA)
	public static void onlyCelestialFactionResistsCelestialMagic(GameTestHelper helper) {
		helper.assertTrue(Faction.DEMON.isTargetedByCelestialMagic(),
				"Le camp demon devrait etre une cible");
		helper.assertTrue(Faction.NEUTRE.isTargetedByCelestialMagic(),
				"Le camp neutre devrait etre une cible");
		helper.assertTrue(!Faction.CELESTE.isTargetedByCelestialMagic(),
				"Le camp celeste ne devrait jamais etre une cible");

		// Les anciennes valeurs numeriques doivent rester lisibles pour reprendre une sauvegarde.
		helper.assertTrue(Faction.fromLegacyValue(-1) == Faction.DEMON, "-1 devrait valoir demon");
		helper.assertTrue(Faction.fromLegacyValue(0) == Faction.NEUTRE, "0 devrait valoir neutre");
		helper.assertTrue(Faction.fromLegacyValue(1) == Faction.CELESTE, "1 devrait valoir celeste");

		helper.succeed();
	}

	/**
	 * Un cadre en blocs de Celestium s'allume et se remplit de surface de portail.
	 *
	 * <p>Le cadre est bati en dur : deux montants, un seuil et un linteau, pour un interieur de
	 * deux blocs de large sur trois de haut.
	 */
	@GameTest(template = ARENA)
	public static void celestiumFrameLightsAPortal(GameTestHelper helper) {
		int innerWidth = 2;
		int innerHeight = 3;
		BlockPos origin = new BlockPos(2, 1, 2);

		// Montants gauche et droit, seuil et linteau compris.
		for (int y = -1; y <= innerHeight; y++) {
			helper.setBlock(origin.offset(-1, y, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
			helper.setBlock(origin.offset(innerWidth, y, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		}
		for (int x = 0; x < innerWidth; x++) {
			helper.setBlock(origin.offset(x, -1, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
			helper.setBlock(origin.offset(x, innerHeight, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		}

		CelestialPortalShape shape =
				CelestialPortalShape.find(helper.getLevel(), helper.absolutePos(origin));

		helper.assertTrue(shape != null, "Le cadre en blocs de Celestium n'est pas reconnu");
		shape.createPortal();

		for (int x = 0; x < innerWidth; x++) {
			for (int y = 0; y < innerHeight; y++) {
				helper.assertBlockPresent(ModBlocks.CELESTIAL_PORTAL.get(), origin.offset(x, y, 0));
			}
		}

		// Casser un montant doit dissiper toute la surface.
		helper.setBlock(origin.offset(-1, 0, 0), Blocks.AIR);
		helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, origin));
	}

	/** Butin d'un bloc casse a mains nues, sans Fortune ni Toucher de soie. */
	private static List<ItemStack> dropsOf(GameTestHelper helper, BlockPos relativePos) {
		BlockPos absolute = helper.absolutePos(relativePos);
		BlockState state = helper.getLevel().getBlockState(absolute);

		LootParams.Builder params = new LootParams.Builder(helper.getLevel())
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(absolute))
				.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

		return state.getDrops(params);
	}
}
