package mwk.testmod.client.gui.widgets;

import java.util.List;
import mwk.testmod.client.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class TextScrollWidget extends AbstractScrollWidget {

    public static final int BORDER_SIZE = 1;
    public static final int SCROLL_BAR_WIDTH = 8;

    private static final Font font = Minecraft.getInstance().font;

    private final int lineHeight;
    private final List<FormattedCharSequence> lines;

    public TextScrollWidget(int x, int y, int width, int height, Component text, int lineHeight) {
        // TODO: Not sure what to do with the message
        super(x, y, width - SCROLL_BAR_WIDTH, height, null);
        this.lineHeight = lineHeight;
        this.lines = font.split(text, width - scrollbarWidth() - BORDER_SIZE * 2);
    }

    @Override
    protected int getInnerHeight() {
        // There seems to be space for one line too many
        return (lines.size() - 1) * lineHeight + BORDER_SIZE * 2;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), getX() + BORDER_SIZE,
                    getY() + BORDER_SIZE + i * lineHeight, ColorUtils.TEXT_WHITE, false);
        }
    }

    @Override
    protected double scrollRate() {
        return 1;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight) {
        return;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean superResult = super.mouseClicked(mouseX, mouseY, button);
        if (superResult) {
            setFocused(true);
        }
        return superResult;
    }

}
