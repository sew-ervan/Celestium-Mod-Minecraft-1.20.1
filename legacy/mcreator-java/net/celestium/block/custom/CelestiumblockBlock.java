
package net.celestium.block;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.util.ForgeSoundType;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Collections;

public class CelestiumblockBlock extends Block {
	public CelestiumblockBlock() {
		super(BlockBehaviour.Properties.of(Material.METAL)
				.sound(new ForgeSoundType(1.0f, 1.0f, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.break")), () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.step")),
						() -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.place")), () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.hit")),
						() -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.fall"))))
				.strength(65f, 6f).requiresCorrectToolForDrops().hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true));
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return new float[]{0.5254901961f, 0.4196078431f, 1f};
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		if (player.getInventory().getSelected().getItem() instanceof PickaxeItem tieredItem)
			return tieredItem.getTier().getLevel() >= 2;
		return false;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}
}
