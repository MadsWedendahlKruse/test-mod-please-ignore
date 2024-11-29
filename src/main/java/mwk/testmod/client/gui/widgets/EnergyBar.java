package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.resource.ResourceBar;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * A widget that displays the energy level of a machine.
 */
public class EnergyBar extends ResourceBar {

    public static final ResourceLocation SPRITE_EMPTY =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/energy_bar_empty");
    public static final ResourceLocation SPRITE_FULL =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/energy_bar_full");
    
    public EnergyBar(MachineMenu menu, int x, int y, int width, int height) {
        super(menu, x, y, width, height, TestModLanguageProvider.KEY_WIDGET_ENERGY_BAR,
                TestModLanguageProvider.KEY_WIDGET_ENERGY_BAR_TOOLTIP, SPRITE_EMPTY, SPRITE_FULL,
                menu::getEnergy, menu::getMaxEnergy);
    }

    public EnergyBar(MachineMenu menu, int x, int y) {
        this(menu, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

}
