package mwk.testmod.common.block.conduit.network.capabilites;

import mwk.testmod.common.block.conduit.network.FluidConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class NetworkFluidHandler extends NetworkCapabilityProvider<FluidConduitNetwork>
        implements IFluidHandler {

    public NetworkFluidHandler(ServerLevel level, FluidConduitNetwork network, BlockPos pos,
            Direction direction) {
        super(level, network, pos, direction);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 1;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        FluidStack received = network.receivePayload(level, pos, direction, stack,
                action.simulate());
        return received.getAmount();
    }

    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return FluidStack.EMPTY;
    }

}
