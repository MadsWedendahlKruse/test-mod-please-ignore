package mwk.testmod.client.render;

import net.minecraft.core.Direction;

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
}
