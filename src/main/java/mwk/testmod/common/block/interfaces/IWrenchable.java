package mwk.testmod.common.block.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An interface for blocks that can be wrenched, i.e. rotated or otherwise manipulated by a wrench.
 */
public interface IWrenchable {

    /**
     * Called when the block is wrenched. This is used to e.g. rotate the block or change its state.
     * 
     * By default, if the player is crouching, the block is removed and its drops are spawned. This
     * can be overridden by the implementing class.
     * 
     * @return True if the wrenching operation did something, false otherwise.
     */
    default boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand) {
        // If the player is crouching, remove the block.
        if (player.isCrouching()) {
            level.destroyBlock(pos, true);
            return true;
        }
        return false;
    }
}
