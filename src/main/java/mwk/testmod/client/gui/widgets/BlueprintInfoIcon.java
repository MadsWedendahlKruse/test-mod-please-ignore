package mwk.testmod.client.gui.widgets;

import java.util.List;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlueprintInfoIcon extends AbstractWidget {

    public static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath("testmod",
            "widget/icon_info");
    private static final int MAX_TOOLTIP_WIDTH = 200;

    private MachineBlockEntity machine;
    private final Font font;

    public BlueprintInfoIcon(int x, int y) {
        super(x, y, 16, 16, null);
        Minecraft minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
    }

    public void setMachine(BlockEntity blockEntity) {
        if (blockEntity instanceof MachineBlockEntity machine) {
            this.machine = machine;
        } else {
            this.machine = null;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        guiGraphics.blitSprite(SPRITE, getX(), getY(), this.width, this.height);
        if (isMouseOver(mouseX, mouseY)) {
            if (machine == null) {
                guiGraphics.renderTooltip(this.font, Component.translatable(
                                TestModLanguageProvider.KEY_WIDGET_BLUEPRINT_ICON_PICK_BLUEPRINT), mouseX,
                        mouseY);
            } else {
                // Split tooltip into multiple lines
                List<FormattedCharSequence> lines = font.split(
                        Component.translatable(machine.getDescriptionKey()), MAX_TOOLTIP_WIDTH);
                guiGraphics.renderTooltip(this.font, lines, mouseX,
                        mouseY);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }
}
