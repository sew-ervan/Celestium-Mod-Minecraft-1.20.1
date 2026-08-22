package net.celestium.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.Minecraft;

import net.celestium.world.inventory.QCMMacronGUIMenu;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

public class QCMMacronGUIScreen extends AbstractContainerScreen<QCMMacronGUIMenu> {
	private final static HashMap<String, Object> guistate = QCMMacronGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Checkbox PourMacron;
	Checkbox ContreMacron;

	public QCMMacronGUIScreen(QCMMacronGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 348;
		this.imageHeight = 112;
	}

	private static final ResourceLocation texture = new ResourceLocation("celestium:textures/screens/qcm_macron_gui.png");

	@Override
	public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(ms);
		super.render(ms, mouseX, mouseY, partialTicks);
		this.renderTooltip(ms, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderTexture(0, texture);
		this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, Component.translatable("gui.celestium.qcm_macron_gui.label_qcm_estu_pour_macron"), 9, 11, -12829636);
	}

	@Override
	public void onClose() {
		super.onClose();
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void init() {
		super.init();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		PourMacron = new Checkbox(this.leftPos + 9, this.topPos + 32, 20, 20, Component.translatable("gui.celestium.qcm_macron_gui.PourMacron"), false);
		guistate.put("checkbox:PourMacron", PourMacron);
		this.addRenderableWidget(PourMacron);
		ContreMacron = new Checkbox(this.leftPos + 9, this.topPos + 66, 20, 20, Component.translatable("gui.celestium.qcm_macron_gui.ContreMacron"), false);
		guistate.put("checkbox:ContreMacron", ContreMacron);
		this.addRenderableWidget(ContreMacron);
	}
}
