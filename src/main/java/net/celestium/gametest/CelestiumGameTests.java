package net.celestium.gametest;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.feature.magie.Faction;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
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

		player.inventoryMenu.broadcastChanges();
		player.tick();

		helper.succeedWhen(() -> helper.assertTrue(
				player.hasEffect(MobEffects.NIGHT_VISION),
				"Le casque en Celestium n'accorde pas la vision nocturne"));
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
		FireBlock fire = (FireBlock) Blocks.FIRE;
		BlockState planks = ModBlocks.BOIS_DU_DEMON.planks.get().defaultBlockState();

		helper.assertTrue(
				fire.getFlammability(planks, helper.getLevel(), helper.absolutePos(BlockPos.ZERO), Direction.UP) > 0,
				"Les planches du bois du demon ne sont pas inflammables");

		helper.succeed();
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
