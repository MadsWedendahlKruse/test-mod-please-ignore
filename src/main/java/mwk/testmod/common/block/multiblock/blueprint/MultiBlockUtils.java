package mwk.testmod.common.block.multiblock.blueprint;

import mwk.testmod.client.events.HologramClientEvents;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MultiBlockUtils {

    private MultiBlockUtils() {
    }

    /**
     * Simulates a block being placed by a player in the world. The block is extracted from the item
     * stack and placed at the given position. This means that if the ItemStack originates from the
     * player's inventory, the item stack is decremented by one. This also updates the blueprint
     * hologram if the block placement was successful.
     *
     * @param level          The level to place the block in.
     * @param player         The player that is placing the block.
     * @param hand           The hand that the player is using to place the block.
     * @param pos            The position to place the block at.
     * @param blockItemStack The item stack representing the block to place.
     * @return The result of the block placement operation.
     */
    public static InteractionResult placeBlueprintBlock(Level level, Player player,
            InteractionHand hand, BlockPos pos, ItemStack blockItemStack, boolean checkHologram) {
        if (blockItemStack == null || blockItemStack.isEmpty()) {
            return InteractionResult.FAIL;
        }
        if (blockItemStack.getItem() instanceof BlockItem blockItem) {
            // TODO: Arguments for the hit result seem irrelevant
            BlockHitResult hitResult = new BlockHitResult(new Vec3(0, 0, 0), player.getDirection(),
                    pos, false);
            UseOnContext context = new UseOnContext(level, player, hand, blockItemStack, hitResult);
            InteractionResult result = blockItem.useOn(context);
            if (result != InteractionResult.FAIL && checkHologram) {
                // Notify the blueprint hologram that a block has been placed.
                // Would be cool if this happened automatically, but
                // BlockEvent.EntityPlaceEvent only gets fired when an *entity*
                // places a block, and there's not another alternative.
                HologramClientEvents.checkHologramUpdate(pos);
            }
            return result;
        }
        return InteractionResult.FAIL;
    }

    /**
     * Attempts to build a multiblock structure from the given blueprint. This method iterates over
     * the missing blocks in the blueprint and attempts to place them in the world. If the player
     * doesn't have the required blocks in their inventory, they are notified. If the player is in
     * creative mode, the blocks are placed automatically.
     *
     * @param level                 The level to place the blocks in.
     * @param blueprint             The blueprint to build.
     * @param controllerPos         The position of the controller block.
     * @param facing                The facing of the controller block.
     * @param player                The player that is building the multiblock.
     * @param hand                  The hand that the player is using to build the multiblock.
     * @param buildEntireMultiBlock Whether to build the entire multiblock structure or just the
     *                              next missing block.
     * @return The result of the build operation.
     */
    public static InteractionResult attemptBuildMultiBlock(Level level,
            MultiBlockBlueprint blueprint, BlockPos controllerPos, Direction facing, Player player,
            InteractionHand hand, boolean buildEntireMultiBlock) {
        BlueprintState blueprintState = blueprint.getState(level, controllerPos, facing);
        // If the blueprint is complete, notify the player and return.
        if (blueprintState.isComplete()) {
            player.displayClientMessage(Component.translatable(
                    TestModLanguageProvider.KEY_INFO_CONTROLLER_BLUEPRINT_COMPLETE), true);
            return InteractionResult.FAIL;
        }
        // Otherwise, iterate over the missing blocks and attempt to place them.
        Inventory inventory = player.getInventory();
        boolean foundMissingBlock = false;
        for (BlueprintBlockInfo blockInfo : blueprintState.getMissingBlocks()) {
            BlockPos blockInfoPos = blockInfo.getAbsolutePosition(controllerPos,
                    facing);
            ItemStack expectedItemStack = blockInfo.getExpectedItemStack();
            ItemStack blockToPlace = ItemStack.EMPTY;
            if (player.isCreative()) {
                blockToPlace = expectedItemStack.copy();
            } else {
                // Check if the player has the missing block in their inventory.
                int itemIndex = inventory.findSlotMatchingItem(expectedItemStack);
                if (itemIndex != -1) {
                    blockToPlace = inventory.getItem(itemIndex);
                }
            }
            // Sanity check that we're not overwriting a block that's already there.
            if (!level.getBlockState(blockInfoPos).isAir()) {
                player.displayClientMessage(
                        Component.translatable(
                                TestModLanguageProvider.KEY_INFO_CONTROLLER_BLUEPRINT_BLOCKED,
                                blockInfoPos.getX(), blockInfoPos.getY(), blockInfoPos.getZ()),
                        true);
                return InteractionResult.SUCCESS;
            }
            // If the player has the block, simulate the block being placed.
            if (!blockToPlace.isEmpty()) {
                InteractionResult result = placeBlueprintBlock(level, player, hand, blockInfoPos,
                        blockToPlace, !buildEntireMultiBlock);
                if (!buildEntireMultiBlock) {
                    return result;
                }
                foundMissingBlock = true;
            }
        }
        // Notify the player if they don't have any of the missing blocks in their inventory.
        if (!foundMissingBlock) {
            player.displayClientMessage(Component.translatable(
                    TestModLanguageProvider.KEY_INFO_CONTROLLER_BLUEPRINT_INSUFFICIENT), true);
        } else {
            HologramClientEvents.checkHologramUpdate(controllerPos);
        }
        return InteractionResult.SUCCESS;
    }
}
