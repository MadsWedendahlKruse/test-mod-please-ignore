package mwk.testmod.common.block.conduit.network.capabilites;

import mwk.testmod.common.block.conduit.network.FluidConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class NetworkFluidHandler extends NetworkCapabilityProvider<FluidConduitNetwork>
        implements IFluidHandler {

    public NetworkFluidHandler(ServerLevel level, FluidConduitNetwork network, BlockPos pos) {
        super(level, network, pos);
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
        FluidStack received = network.receivePayload(level, pos, stack, action.simulate());
        return received.getAmount();
    }

    @Override
    public FluidStack drain(FluidStack arg0, FluidAction arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drain'");
    }

    @Override
    public FluidStack drain(int arg0, FluidAction arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drain'");
    }

}
