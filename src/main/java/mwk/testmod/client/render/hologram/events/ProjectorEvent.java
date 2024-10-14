package mwk.testmod.client.render.hologram.events;

import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintState;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Event triggered by the player aiming the hologram projector at a block, while having selected a
 * blueprint in the menu
 */
public record ProjectorEvent(Level level, BlockPos lookAtPos, MultiBlockBlueprint blueprint,
                             Direction blueprintDirection) implements HologramEvent {

    @Override
    public void apply(HologramRenderer renderer) {
        // If the renderer is locked, and it's already rendering the same blueprint, don't do
        // anything
        if (renderer.getLatestEvent() instanceof ProjectorEvent event) {
            if (renderer.isLocked() && event.blueprint == this.blueprint) {
                return;
            }
        }
        AABB aabb = blueprint.getAABB(null, blueprintDirection);
        BlockPos controllerPos = lookAtPos.offset(0, (int) -aabb.minY + 1, 0);
        BlueprintState blueprintState =
                blueprint.getState(level, controllerPos, blueprintDirection);
        boolean animateMove = renderer.getLatestEvent() instanceof ProjectorEvent;
        boolean animateScale = !animateMove;
        renderer.setHologramBlueprint(controllerPos, blueprint, blueprintState, blueprintDirection,
                animateScale, animateMove);
    }
}
