package mwk.testmod.common.util.inventory.handler;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidHandlerWrapper implements IFluidHandler {

    private final FluidStackHandler fluidHandler;
    private final int startTank;
    private final int tanks;

    /**
     * Creates a new FluidHandlerWrapper with the given capacity for each tank
     *
     * @param startTank the index of the first tank
     * @param tanks     the number of tanks to wrap
     */
    public FluidHandlerWrapper(FluidStackHandler fluidHandler, int startTank, int tanks) {
        this.fluidHandler = fluidHandler;
        this.startTank = startTank;
        this.tanks = tanks;
        if (startTank + tanks > fluidHandler.getTanks()) {
            throw new IllegalArgumentException(
                    "The start tank and number of tanks exceed the capacity array length");
        }
    }

    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        for (int i = startTank; i < startTank + tanks; i++) {
            if (isFluidValid(i, stack)) {
                return fluidHandler.drain(i, stack.getAmount(), action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        for (int i = startTank; i < startTank + tanks; i++) {
            FluidStack drained = fluidHandler.drain(i, amount, action);
            if (!drained.isEmpty()) {
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        for (int i = startTank; i < startTank + tanks; i++) {
            if (isFluidValid(i, stack)) {
                return fluidHandler.fill(i, stack, action);
            }
        }
        return 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluidHandler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidHandler.getTankCapacity(tank);
    }

    @Override
    public int getTanks() {
        return fluidHandler.getTanks();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return fluidHandler.isFluidValid(tank, stack);
    }

    public int getStartTank() {
        return startTank;
    }

    public int getEndTank() {
        return startTank + tanks;
    }

}
