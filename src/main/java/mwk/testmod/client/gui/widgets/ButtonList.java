package mwk.testmod.client.gui.widgets;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ButtonList extends AbstractWidget {

    private ArrayList<Button> buttons;
    private boolean horizontal;
    private int padding;

    public ButtonList(int x, int y, Component message, boolean horizontal, int padding) {
        super(x, y, 0, 0, message);
        buttons = new ArrayList<Button>();
        this.horizontal = horizontal;
        this.padding = padding;
    }

    public void addButton(Button button) {
        buttons.add(button);
        int buttonX = getX();
        int buttonY = getY();
        if (horizontal) {
            buttonX += width;
            width += button.getWidth() + padding;
            height = Math.max(height, button.getHeight());
        } else {
            buttonY += height;
            width = Math.max(width, button.getWidth());
            height += button.getHeight() + padding;
        }
        button.setX(buttonX);
        button.setY(buttonY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY,
            float pPartialTick) {
        for (Button button : buttons) {
            button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        for (Button buttonWidget : buttons) {
            if (buttonWidget.isMouseOver(mouseX, mouseY)) {
                buttonWidget.onClick(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        for (Button button : buttons) {
            if (button.isMouseOver(pMouseX, pMouseY)) {
                button.onRelease(pMouseX, pMouseY);
            }
        }
    }
}
