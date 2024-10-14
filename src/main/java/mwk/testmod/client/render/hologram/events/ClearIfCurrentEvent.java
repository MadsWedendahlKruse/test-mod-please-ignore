package mwk.testmod.client.render.hologram.events;

import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * This event clears the hologram if the current blueprint is the same as the one being cleared.
 * This is used when the player breaks the block the hologram is being projected from.
 */
public record ClearIfCurrentEvent(MultiBlockBlueprint blueprint, BlockPos controllerPos,
                                  Direction facing) implements HologramEvent {

    @Override
    public void apply(HologramRenderer renderer) {
        if (renderer.isCurrentBlueprint(controllerPos, blueprint, facing)) {
            renderer.clearHologram();
        }
    }
}
