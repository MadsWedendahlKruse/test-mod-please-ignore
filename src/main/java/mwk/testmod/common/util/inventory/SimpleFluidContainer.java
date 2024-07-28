package mwk.testmod.common.util.inventory;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * A simple container that can store fluid stacks.
 */
public class SimpleFluidContainer {

    // Number of fluid stacks that can be stored in this container.
    private final int fluidSize;
    private final NonNullList<FluidStack> fluidStacks;

    public SimpleFluidContainer(int fluidSize) {
        this.fluidSize = fluidSize;
        this.fluidStacks = NonNullList.withSize(fluidSize, FluidStack.EMPTY);
    }

    public SimpleFluidContainer(FluidStack... stacks) {
        this.fluidSize = stacks.length;
        this.fluidStacks = NonNullList.withSize(fluidSize, FluidStack.EMPTY);
        for (int i = 0; i < fluidSize; i++) {
            fluidStacks.set(i, stacks[i]);
        }
    }

    public int getSize() {
        return fluidSize;
    }

    public void setFluid(int index, FluidStack stack) {
        if (index < 0 || index >= fluidSize) {
            return;
        }
        fluidStacks.set(index, stack);
    }

    public FluidStack getFluid(int index) {
        if (index < 0 || index >= fluidSize) {
            return FluidStack.EMPTY;
        }
        return fluidStacks.get(index);
    }
}
