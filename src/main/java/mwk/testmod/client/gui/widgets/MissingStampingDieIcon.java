package mwk.testmod.client.gui.widgets;

import mwk.testmod.common.block.inventory.StampingPressMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MissingStampingDieIcon extends AbstractWidget {

    public static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath("testmod",
            "widget/missing_stamping_die");

    private final StampingPressMenu menu;
    private final Font font;

    public MissingStampingDieIcon(StampingPressMenu menu, int x, int y) {
        super(x, y, 16, 16,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_MISSING_STAMPING_DIE));
        this.menu = menu;
        Minecraft minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        guiGraphics.blitSprite(SPRITE, getX(), getY(), this.width, this.height);
        if (menu.getStampingDie().isEmpty() && isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, getMessage(), mouseX,
                    mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }
}
