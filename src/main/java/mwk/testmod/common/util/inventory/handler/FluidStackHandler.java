package mwk.testmod.common.util.inventory.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidStackHandler {

    private FluidStack[] stacks;
    private int[] capacity;

    /**
     * Creates a new FluidStackHandler with the given capacity for each tank
     * 
     * @param capacity The capacity of each tank
     */
    public FluidStackHandler(int[] capacity) {
        this.capacity = capacity;
        this.stacks = new FluidStack[capacity.length];
        for (int i = 0; i < capacity.length; i++) {
            stacks[i] = FluidStack.EMPTY;
        }
    }

    public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
        validateTankIndex(tank);
        FluidStack stack = stacks[tank];
        if (resource.isEmpty() || !stack.isFluidEqual(resource)) {
            return FluidStack.EMPTY;
        }
        return drain(tank, resource.getAmount(), action);
    }

    public FluidStack drain(int tank, int maxDrain, FluidAction action) {
        if (maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        validateTankIndex(tank);
        FluidStack stack = stacks[tank];
        if (stack.isEmpty()) {
            return FluidStack.EMPTY;
        }

        final int drainAmount = Math.min(maxDrain, stack.getAmount());
        final FluidStack drained = new FluidStack(stack, drainAmount);
        if (action.execute()) {
            stack.shrink(drainAmount);
            if (stack.isEmpty()) {
                stacks[tank] = FluidStack.EMPTY;
            }
            onContentsChanged(tank);
            return drained;
        } else {
            return new FluidStack(stacks[tank], drainAmount);
        }
    }

    public int fill(int tank, FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        validateTankIndex(tank);
        FluidStack stack = stacks[tank];
        final int filled = Math.min(capacity[tank] - stack.getAmount(), resource.getAmount());
        if (filled <= 0) {
            return 0;
        }

        if (action.execute()) {
            if (stack.isEmpty()) {
                stacks[tank] = new FluidStack(resource, filled);
            } else {
                stack.grow(filled);
            }
            onContentsChanged(tank);
        }
        return filled;
    }

    public FluidStack getFluidInTank(int tank) {
        validateTankIndex(tank);
        return stacks[tank];
    }

    public int getTankCapacity(int tank) {
        validateTankIndex(tank);
        return capacity[tank];
    }

    public int getTanks() {
        return stacks.length;
    }

    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < stacks.length; i++) {
            CompoundTag tankTag = new CompoundTag();
            tankTag.putInt("Tank", i);
            tankTag.put("Fluid", stacks[i].writeToNBT(new CompoundTag()));
            list.add(tankTag);
        }
        nbt.put("Tanks", list);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        ListTag list = nbt.getList("Tanks", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tankTag = list.getCompound(i);
            int tank = tankTag.getInt("Tank");
            stacks[tank] = FluidStack.loadFluidStackFromNBT(tankTag.getCompound("Fluid"));
        }
    }

    protected void validateTankIndex(int tank) {
        if (tank < 0 || tank >= getTanks()) {
            throw new RuntimeException(
                    "Tank index " + tank + " is out of bounds [0, " + getTanks() + ")");
        }
    }

    protected void onContentsChanged(int tank) {}

}
