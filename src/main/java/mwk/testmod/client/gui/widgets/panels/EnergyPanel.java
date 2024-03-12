package mwk.testmod.client.gui.widgets.panels;

import mwk.testmod.TestMod;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyPanel extends MachinePanel {

    public static final ResourceLocation ICON =
            new ResourceLocation(TestMod.MODID, "widget/icon_energy");
    public static final float[] COLOR = new float[] {1, 0.25F, 0, 1};

    public EnergyPanel() {
        super(100, 100, Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY),
                COLOR, ICON, 5, 5);
    }

    @Override
    public void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // TODO Auto-generated method stub
    }

}
