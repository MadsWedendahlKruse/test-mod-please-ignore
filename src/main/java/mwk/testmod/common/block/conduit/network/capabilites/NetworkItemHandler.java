package mwk.testmod.common.block.conduit.network.capabilites;

import mwk.testmod.common.block.conduit.network.ItemConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class NetworkItemHandler extends NetworkCapabilityProvider<ItemConduitNetwork>
        implements IItemHandler {

    public NetworkItemHandler(ServerLevel level, ItemConduitNetwork network, BlockPos pos,
            Direction direction) {
        super(level, network, pos, direction);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        // The slot doesn't matter
        return network.receivePayload(level, pos, direction, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }


    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

}
