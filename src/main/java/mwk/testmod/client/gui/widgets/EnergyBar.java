package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.common.block.inventory.base.EnergyMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A widget that displays the energy level of a machine.
 */
public class EnergyBar extends AbstractWidget {

    public static final ResourceLocation SPRITE_EMPTY =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/energy_bar_empty");
    public static final ResourceLocation SPRITE_FULL =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/energy_bar_full");

    public static final int WIDTH = 12;
    public static final int HEIGHT = 52;

    private final EnergyMenu menu;
    private final Font font;

    public EnergyBar(EnergyMenu menu, int x, int y) {
        this(menu, x, y, WIDTH, HEIGHT);
    }

    public EnergyBar(EnergyMenu menu, int x, int y, int width, int height) {
        super(x, y, width, height,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_ENERGY_BAR));
        this.menu = menu;
        Minecraft minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (energy != maxEnergy) {
            guiGraphics.blitSprite(SPRITE_EMPTY, getX(), getY(), this.width, this.height);
        }
        int barHeight = energy * this.height / maxEnergy;
        // Reverse engineered from AbstractFurnaceScreen#renderBg. Not completely sure
        // how it works, some of the args are obfuscated names.
        guiGraphics.blitSprite(SPRITE_FULL, this.width, this.height, 0, this.height - barHeight,
                getX(), getY() + this.height - barHeight, this.width, barHeight);
        if (isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable(TestModLanguageProvider.KEY_WIDGET_ENERGY_BAR_TOOLTIP,
                            GuiUtils.NUMBER_FORMAT.format(energy),
                            GuiUtils.NUMBER_FORMAT.format(maxEnergy)),
                    mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }

}
