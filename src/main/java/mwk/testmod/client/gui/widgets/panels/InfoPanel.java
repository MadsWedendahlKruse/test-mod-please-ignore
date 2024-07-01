package mwk.testmod.client.gui.widgets.panels;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.TextScrollWidget;
import mwk.testmod.client.gui.widgets.panels.base.MachinePanel;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InfoPanel extends MachinePanel {

	public static final ResourceLocation ICON =
			new ResourceLocation(TestMod.MODID, "widget/icon_info");
	public static final float[] COLOR = new float[] {0, 0.6F, 1.0F, 1};
	public static final int WIDTH = 110;
	public static final int HEIGHT = 100;

	private final TextScrollWidget textScrollWidget;

	public InfoPanel(MachineBlockEntity machine) {
		super(WIDTH, HEIGHT, Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_INFO),
				COLOR, ICON);
		this.textScrollWidget = new TextScrollWidget(0, 0, WIDTH, HEIGHT,
				Component.translatable(machine.getDescriptionKey()), LINE_HEIGHT);
	}

	@Override
	protected void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		textScrollWidget.setPosition(getOpenLeft(), getOpenTop());
		textScrollWidget.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isOpenFully() && textScrollWidget.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (isOpenFully() && textScrollWidget.mouseReleased(pMouseX, pMouseY, pButton)) {
			return true;
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX,
			double pDragY) {
		if (isOpenFully()
				&& textScrollWidget.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
			return true;
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}

	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
		if (isOpenFully() && textScrollWidget.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY)) {
			return true;
		}
		return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
	}

	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (isOpenFully() && textScrollWidget.keyPressed(pKeyCode, pScanCode, pModifiers)) {
			return true;
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
}
