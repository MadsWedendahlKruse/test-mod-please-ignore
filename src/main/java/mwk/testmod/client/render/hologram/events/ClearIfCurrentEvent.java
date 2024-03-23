package mwk.testmod.client.render.hologram.events;

import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * This event clears the hologram if the current blueprint is the same as the one being cleared.
 * This is useful for example when the player breaks a block that the hologram is projected on.
 */
public class ClearIfCurrentEvent implements HologramEvent {
    private final MultiBlockBlueprint blueprint;
    private final BlockPos controllerPos;
    private final Direction facing;

    public ClearIfCurrentEvent(MultiBlockBlueprint blueprint, BlockPos controllerPos,
            Direction facing) {
        this.blueprint = blueprint;
        this.controllerPos = controllerPos;
        this.facing = facing;
    }

    @Override
    public void apply(HologramRenderer renderer) {
        if (renderer.isCurrentBlueprint(controllerPos, blueprint, facing)) {
            renderer.clearHologram();
        }
    }
}
