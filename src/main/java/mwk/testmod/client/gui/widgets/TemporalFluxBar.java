package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.resource.ResourceBar;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * A widget that displays the energy level of a machine.
 */
public class TemporalFluxBar extends ResourceBar {

    public static final ResourceLocation SPRITE_EMPTY =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/temporal_flux_bar_empty");
    public static final ResourceLocation SPRITE_FULL =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/temporal_flux_bar_full");

    public TemporalFluxBar(MachineMenu menu, int x, int y, int width, int height) {
        super(menu, x, y, width, height, TestModLanguageProvider.KEY_WIDGET_TEMPORAL_FLUX_BAR,
                TestModLanguageProvider.KEY_WIDGET_TEMPORAL_FLUX_BAR_TOOLTIP, SPRITE_EMPTY,
                SPRITE_FULL, menu::getTemporalFlux, menu::getMaxTemporalFlux);
    }

    public TemporalFluxBar(MachineMenu menu, int x, int y) {
        this(menu, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

}
