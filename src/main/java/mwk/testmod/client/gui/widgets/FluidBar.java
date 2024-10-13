package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.GuiUtils;
import mwk.testmod.client.gui.GuiUtils.TilingDirection;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidBar extends AbstractWidget {

    public static final ResourceLocation SPRITE_FLUID_BAR_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/fluid_bar_overlay");

    public static final int WIDTH = 16;
    public static final int HEIGHT = 52;

    private final IFluidHandler fluidHandler;
    private final int tank;
    private final Font font;

    public FluidBar(IFluidHandler fluidHandler, int tank, int x, int y) {
        super(x, y, WIDTH, HEIGHT,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_FLUID_BAR));
        this.fluidHandler = fluidHandler;
        this.tank = tank;
        Minecraft minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
        if (fluidStack == null) {
            return;
        }
        int fluidAmount = fluidStack.getAmount();
        int maxFluidAmount = fluidHandler.getTankCapacity(tank);
        Component fluidName = fluidStack.isEmpty()
                ? Component.translatable(TestModLanguageProvider.KEY_WIDGET_FLUID_EMPTY)
                : fluidStack.getHoverName();
        int barHeight = fluidAmount * this.height / maxFluidAmount;
        // TODO: Get proper color - maybe not needed?
        int color = Integer.MAX_VALUE;
        if (!fluidStack.isEmpty()) {
            TextureAtlasSprite sprite = RenderUtils.getFluidTexture(fluidStack, false);
            GuiUtils.drawTiledSprite(guiGraphics, getX(), getY() + this.height, 0, this.width,
                    barHeight, sprite, 16, 16, 0, TilingDirection.UP_RIGHT, true);

        }
        guiGraphics.blitSprite(SPRITE_FLUID_BAR_OVERLAY, getX(), getY(), this.width, this.height);
        if (isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable(TestModLanguageProvider.KEY_WIDGET_FLUID_BAR_TOOLTIP,
                            fluidName, GuiUtils.NUMBER_FORMAT.format(fluidAmount),
                            GuiUtils.NUMBER_FORMAT.format(maxFluidAmount)),
                    mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput arg0) {
        // TODO Auto-generated method stub
    }

}
