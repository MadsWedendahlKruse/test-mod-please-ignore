package mwk.testmod.common.block.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * An interface for blocks that can be wrenched, i.e. rotated or otherwise manipulated by a wrench.
 */
public interface IWrenchable {

    /**
     * Called when the block is wrenched. This is used to e.g. rotate the block or change its
     * state.
     * <p>
     * By default, if the player is crouching, the block is removed and its drops are spawned. This
     * can be overridden by the implementing class.
     *
     * @return True if the wrenching operation did something, false otherwise.
     */
    default boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, Vec3 clickLocation) {
        // If the player is crouching, remove the block.
        if (player.isCrouching()) {
            // "manually" remove it to avoid spawning particles
            // TODO: Should we play a sound here?
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(state.getBlock().asItem()));
            level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS,
                    1.0F, 1.0F);
            return true;
        }
        return false;
    }
}
