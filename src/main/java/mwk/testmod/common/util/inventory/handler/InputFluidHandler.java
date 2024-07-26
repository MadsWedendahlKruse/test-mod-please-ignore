package mwk.testmod.common.util.inventory.handler;

import net.neoforged.neoforge.fluids.FluidStack;

public class InputFluidHandler extends FluidHandlerWrapper {

    private final InputValidator validator;

    /**
     * Creates a new input fluid handler.
     * 
     * @param fluidHandler the underlying fluid handler
     * @param startInputTank the index of the first input tank
     * @param inputTanks the number of input tanks
     * @param validator the input validator
     */
    public InputFluidHandler(FluidStackHandler fluidHandler, int startInputTank, int inputTanks,
            InputValidator validator) {
        super(fluidHandler, startInputTank, inputTanks);
        this.validator = validator;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return validator.isFluidValid(tank, stack);
    }

    @FunctionalInterface
    public interface InputValidator {
        boolean isFluidValid(int tank, FluidStack stack);
    }

}
