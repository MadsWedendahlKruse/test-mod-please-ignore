package mwk.testmod.client.render;

import mwk.testmod.TestMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class RenderUtils {

    private RenderUtils() {}

    public static float getRotation(Direction facing) {
        switch (facing) {
            case EAST:
                return -(float) Math.PI / 2.0F;
            case WEST:
                return (float) Math.PI / 2.0F;
            case SOUTH:
                return (float) Math.PI;
            case NORTH:
            default:
                return 0.0F;
        }
    }

    public static final ResourceLocation ITEM_SLOT_SPRITE =
            new ResourceLocation(TestMod.MODID, "widget/item_slot");
    public static final int ITEM_SLOT_SIZE = 18;

    /**
     * Renders an item slot sprite at the given position. The position is the top-left corner of the
     * item slot, meaning the border sprite will be rendered 1 pixel to the left and 1 pixel above
     * the given position. The width and height can be used to render e.g. the border of an energy
     * bar
     *
     * @param guiGraphics the graphics object to render with
     * @param x the x position to render the item slot at
     * @param y the y position to render the item slot at
     * @param width the width of the item slot
     * @param height the height of the item slot
     */
    public static void renderItemSlot(GuiGraphics guiGraphics, int x, int y, int width,
            int height) {
        // -1 to account for the border of the item slot sprite
        guiGraphics.blitSprite(ITEM_SLOT_SPRITE, x - 1, y - 1, width, height);
    }

    /**
     * Renders an item slot with the default size of 18x18 at the given position. See
     * {@link #renderItemSlot(GuiGraphics, int, int, int, int)}
     */
    public static void renderItemSlot(GuiGraphics guiGraphics, int x, int y) {
        renderItemSlot(guiGraphics, x, y, ITEM_SLOT_SIZE, ITEM_SLOT_SIZE);
    }
}
