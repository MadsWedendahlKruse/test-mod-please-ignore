package mwk.testmod.client.hologram.events;

import mwk.testmod.client.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintState;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Event triggered by the player using a wrench on a multi-block controller. This event toggles the
 * hologram, i.e. it renders the hologram if it's not already rendered, and clears it if it's
 * already rendered.
 */
public class WrenchEvent implements HologramEvent {
    protected final Level level;
    protected final BlockPos controllerPos;

    public WrenchEvent(Level level, BlockPos controllerPos) {
        this.level = level;
        this.controllerPos = controllerPos;
    }

    @Override
    public void apply(HologramRenderer renderer) {
        BlockState state = level.getBlockState(controllerPos);
        if (state.getBlock() instanceof MultiBlockControllerBlock controller) {
            MultiBlockBlueprint blueprint = controller.getBlueprint();
            Direction facing = state.getValue(MultiBlockControllerBlock.FACING);
            BlueprintState blueprintState = blueprint.getState(level, controllerPos, facing);
            if (renderer.isCurrentBlueprint(controllerPos, blueprint, facing)) {
                renderer.clearHologram();
            } else {
                renderer.setHologramBlueprint(controllerPos, blueprint, blueprintState, facing,
                        true, false);
            }
            return;
        }
        throw new IllegalStateException(
                "Wrench event can only be applied to a multi-block controller");
    }
}
