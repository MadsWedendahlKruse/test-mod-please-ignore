package mwk.testmod.client.gui.screen.config;

import mwk.testmod.client.gui.widgets.progress.ProgressArrowFactory.ArrowType;
import net.minecraft.resources.ResourceLocation;

/**
 * Configuration for a GUI screen.
 */
public record GuiConfig(ResourceLocation background, int energyBarX, int energyBarY, int imageWidth,
                        int imageHeight, String progressIconName, int progressIconX,
                        int progressIconY,
                        ArrowType progressArrowType, int progressArrows, int progressArrowX,
                        int progressArrowY, int progressArrowSpacing) {

}
