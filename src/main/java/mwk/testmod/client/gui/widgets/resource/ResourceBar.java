package mwk.testmod.client.gui.widgets.resource;

import java.util.function.Supplier;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A widget that displays the level of a resource in a machine.
 */
public abstract class ResourceBar extends AbstractWidget {

    public static final int DEFAULT_WIDTH = 12;
    public static final int DEFAULT_HEIGHT = 52;

    private final Font font;
    private final String keyTooltip;

    private final ResourceLocation spriteEmpty;
    private final ResourceLocation spriteFull;

    private final Supplier<Integer> getResource;
    private final Supplier<Integer> getMaxResource;

    public ResourceBar(MachineMenu menu, int x, int y, int width, int height, String keyName,
            String keyTooltip, ResourceLocation spriteEmpty, ResourceLocation spriteFull,
            Supplier<Integer> getResource, Supplier<Integer> getMaxResource) {
        super(x, y, width, height, Component.translatable(keyName));
        Minecraft minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
        this.keyTooltip = keyTooltip;
        this.spriteEmpty = spriteEmpty;
        this.spriteFull = spriteFull;
        this.getResource = getResource;
        this.getMaxResource = getMaxResource;
    }

    public ResourceBar(MachineMenu menu, int x, int y, String keyName, String keyTooltip,
            ResourceLocation spriteEmpty, ResourceLocation spriteFull,
            Supplier<Integer> getResource, Supplier<Integer> getMaxResource) {
        this(menu, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, keyName, keyTooltip, spriteEmpty,
                spriteFull, getResource, getMaxResource);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        int resource = getResource.get();
        int maxResource = getMaxResource.get();
        if (resource != maxResource) {
            guiGraphics.blitSprite(spriteEmpty, getX(), getY(), this.width, this.height);
        }
        int barHeight = resource * this.height / maxResource;
        guiGraphics.blitSprite(spriteFull, this.width, this.height, 0, this.height - barHeight,
                getX(), getY() + this.height - barHeight, this.width, barHeight);
        if (isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable(keyTooltip,
                            GuiUtils.NUMBER_FORMAT.format(resource),
                            GuiUtils.NUMBER_FORMAT.format(maxResource)),
                    mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }
}
