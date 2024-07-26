package mwk.testmod.common.util.inventory.handler;

import net.neoforged.neoforge.fluids.FluidStack;

public class OutputFluidHandler extends FluidHandlerWrapper {

    /**
     * Creates a new output fluid handler.
     * 
     * @param fluidHandler the underlying fluid handler
     * @param startOutputTank the index of the first output tank
     * @param outputTanks the number of output tanks
     */
    public OutputFluidHandler(FluidStackHandler fluidHandler, int startOutputTank,
            int outputTanks) {
        super(fluidHandler, startOutputTank, outputTanks);
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }
}
