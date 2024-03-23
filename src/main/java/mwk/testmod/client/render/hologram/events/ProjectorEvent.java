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
public class ProjectorEvent implements HologramEvent {
    private Level level;
    private BlockPos lookAtPos;
    private MultiBlockBlueprint blueprint;
    private Direction blueprintDirection;

    /**
     * Creates a new projector event for the given level, look-at position, blueprint and controller
     * facing direction.
     * 
     * @param level The world the hologram is in
     * @param lookAtPos The position of the block the player is looking at
     * @param blueprint The blueprint to render
     * @param blueprintDirection The direction the blueprint should be rendered in
     */
    public ProjectorEvent(Level level, BlockPos lookAtPos, MultiBlockBlueprint blueprint,
            Direction blueprintDirection) {
        this.level = level;
        this.lookAtPos = lookAtPos;
        this.blueprint = blueprint;
        this.blueprintDirection = blueprintDirection;
    }

    @Override
    public void apply(HologramRenderer renderer) {
        // If the renderer is locked and it's already rendering the same blueprint, don't do
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
