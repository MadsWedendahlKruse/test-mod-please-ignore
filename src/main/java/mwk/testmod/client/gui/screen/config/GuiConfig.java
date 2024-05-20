package mwk.testmod.client.gui.screen.config;

import mwk.testmod.client.gui.widgets.progress.ProgressArrowFactory;
import net.minecraft.resources.ResourceLocation;

/**
 * Configuration for a GUI screen.
 */
public record GuiConfig(ResourceLocation background, int energyBarX, int energyBarY, int imageWidth,
        int imageHeight, String progressIconName, int progressIconX, int progressIconY,
        ProgressArrowFactory.Type progressArrowType, int progressArrows, int progressArrowX,
        int progressArrowY, int progressArrowSpacing) {
}
