package net.celestium.client.screen;

import net.celestium.CelestiumMod;
import net.celestium.feature.enchant.CorruptedEnchantingMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;

/**
 * L'interface de la table corrompue.
 *
 * <p>Un emplacement a gauche, jusqu'a trois propositions a droite. Chaque proposition dit ce qu'elle
 * accorde, a quel palier, et ce qu'elle coute — la table du jeu de base cache deux de ces trois
 * choses derriere un alphabet illisible, ce qui n'a d'interet que pour un tirage au sort. Ici il n'y
 * en a pas : ce qu'on paie est ce qu'on obtient, donc autant l'ecrire.
 */
public class CorruptedEnchantingScreen extends AbstractContainerScreen<CorruptedEnchantingMenu> {

	private static final ResourceLocation TEXTURE =
			CelestiumMod.id("textures/gui/corrupted_enchanting_table.png");

	// Les propositions, a droite de l'emplacement.
	private static final int OFFER_X = 44;
	private static final int OFFER_Y = 17;
	private static final int OFFER_WIDTH = 116;
	private static final int OFFER_HEIGHT = 20;
	private static final int OFFER_SPACING = 22;

	private static final int OFFER_IDLE = 0xFF6B6B6B;
	private static final int OFFER_HOVER = 0xFF8E8E8E;
	private static final int OFFER_UNAFFORDABLE = 0xFF4A3030;
	private static final int OFFER_BORDER = 0xFF373737;

	public CorruptedEnchantingScreen(CorruptedEnchantingMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = CorruptedEnchantingMenu.IMAGE_WIDTH;
		this.imageHeight = CorruptedEnchantingMenu.IMAGE_HEIGHT;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderOffers(graphics, mouseX, mouseY);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	private void renderOffers(GuiGraphics graphics, int mouseX, int mouseY) {
		ItemStack tool = this.menu.tool();
		List<Enchantment> offers = CorruptedEnchantingMenu.offersFor(tool);

		if (offers.isEmpty()) {
			this.renderHint(graphics, tool);
			return;
		}

		for (int index = 0; index < Math.min(offers.size(), CorruptedEnchantingMenu.MAX_OFFERS); index++) {
			Enchantment enchantment = offers.get(index);
			int cost = CorruptedEnchantingMenu.costOf(tool, enchantment);
			boolean affordable = this.affordable(cost);

			int left = this.leftPos + OFFER_X;
			int top = this.topPos + OFFER_Y + index * OFFER_SPACING;
			boolean hovered = this.overOffer(mouseX, mouseY, index) && affordable;

			int fill = !affordable ? OFFER_UNAFFORDABLE : hovered ? OFFER_HOVER : OFFER_IDLE;
			graphics.fill(left, top, left + OFFER_WIDTH, top + OFFER_HEIGHT, OFFER_BORDER);
			graphics.fill(left + 1, top + 1, left + OFFER_WIDTH - 1, top + OFFER_HEIGHT - 1, fill);

			int nextLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, tool) + 1;
			Component name = Component.translatable(enchantment.getDescriptionId())
					.append(" ")
					.append(Component.translatable("enchantment.level." + nextLevel));

			graphics.drawString(this.font, name, left + 5, top + 3,
					affordable ? 0xFFFFFF : 0xA0A0A0, false);
			graphics.drawString(this.font,
					Component.translatable("message.celestium.enchant.cost", cost)
							.withStyle(affordable ? ChatFormatting.GREEN : ChatFormatting.RED),
					left + 5, top + 12, 0xFFFFFF, false);
		}
	}

	/** Ce qu'affiche la table quand elle n'a rien a proposer. */
	private void renderHint(GuiGraphics graphics, ItemStack tool) {
		String key = tool.isEmpty()
				? "message.celestium.enchant.empty"
				: "message.celestium.enchant.already_maxed";

		graphics.drawWordWrap(this.font, Component.translatable(key),
				this.leftPos + OFFER_X, this.topPos + OFFER_Y + 4, OFFER_WIDTH, 0x808080);
	}

	private boolean affordable(int cost) {
		return this.minecraft != null && this.minecraft.player != null
				&& (this.minecraft.player.getAbilities().instabuild
				|| this.minecraft.player.experienceLevel >= cost);
	}

	private boolean overOffer(double mouseX, double mouseY, int index) {
		int left = this.leftPos + OFFER_X;
		int top = this.topPos + OFFER_Y + index * OFFER_SPACING;

		return mouseX >= left && mouseX < left + OFFER_WIDTH
				&& mouseY >= top && mouseY < top + OFFER_HEIGHT;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		ItemStack tool = this.menu.tool();
		List<Enchantment> offers = CorruptedEnchantingMenu.offersFor(tool);

		for (int index = 0; index < Math.min(offers.size(), CorruptedEnchantingMenu.MAX_OFFERS); index++) {
			if (!this.overOffer(mouseX, mouseY, index)) {
				continue;
			}
			if (!this.affordable(CorruptedEnchantingMenu.costOf(tool, offers.get(index)))) {
				return true;
			}

			// Le serveur decide : le clic n'est qu'une demande, et il refera lui-meme le calcul du
			// prix avant d'accorder quoi que ce soit.
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
			}
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}
}
