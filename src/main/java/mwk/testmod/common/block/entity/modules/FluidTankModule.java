package mwk.testmod.common.block.entity.modules;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.util.inventory.SimpleFluidContainer;
import mwk.testmod.common.util.inventory.handler.FluidStackHandler;
import mwk.testmod.common.util.inventory.handler.InputFluidHandler;
import mwk.testmod.common.util.inventory.handler.OutputFluidHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * A module encapsulating fluid tanks for a machine block entity.
 */
public class FluidTankModule implements MachineModule {

    public static final String NBT_TAG_FLUID_TANKS = "fluidTanks";
    public static final int[] EMPTY_TANKS = new int[0];

    private final int inputTanks;
    private final int outputTanks;

    private final FluidStackHandler fluidTanks;
    private final InputFluidHandler inputFluidHandler;
    private final OutputFluidHandler outputFluidHandler;

    public FluidTankModule(MachineBlockEntity machine, int[] inputTankCapacities,
            int[] outputTankCapacities, InputFluidHandler.InputValidator canInsert) {
        inputTanks = inputTankCapacities.length;
        outputTanks = outputTankCapacities.length;
        int[] tankCapacities = new int[inputTanks + outputTanks];
        System.arraycopy(inputTankCapacities, 0, tankCapacities, 0, inputTanks);
        System.arraycopy(outputTankCapacities, 0, tankCapacities, inputTanks, outputTanks);
        fluidTanks = new FluidStackHandler(tankCapacities) {
            @Override
            protected void onContentsChanged(int tank) {
                machine.setChanged();
                // TODO: This works, but for most cases the tank contents can only be seen in the
                // GUI, so most of these syncs are unnecessary
                machine.getLevel().sendBlockUpdated(machine.getBlockPos(), machine.getBlockState(),
                        machine.getBlockState(), Block.UPDATE_CLIENTS);
            }
        };
        inputFluidHandler =
                new InputFluidHandler(fluidTanks, 0, inputTanks, canInsert);
        outputFluidHandler = new OutputFluidHandler(fluidTanks, inputTanks, outputTanks);
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.put(NBT_TAG_FLUID_TANKS, fluidTanks.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        if (tag.contains(NBT_TAG_FLUID_TANKS)) {
            fluidTanks.deserializeNBT(registries, tag.getCompound(NBT_TAG_FLUID_TANKS));
        }
    }

    public CompoundTag getUpdateTag(CompoundTag tag, Provider registries) {
        if (inputTanks + outputTanks > 0) {
            tag.put(NBT_TAG_FLUID_TANKS, fluidTanks.serializeNBT(registries));
        }
        return tag;
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag.contains(NBT_TAG_FLUID_TANKS)) {
            fluidTanks.deserializeNBT(registries, tag.getCompound(NBT_TAG_FLUID_TANKS));
        }
    }

    public InputFluidHandler getInputFluidHandler(Direction direction) {
        return inputFluidHandler;
    }

    public OutputFluidHandler getOutputFluidHandler(Direction direction) {
        return outputFluidHandler;
    }

    public int getInputTanks() {
        return inputTanks;
    }

    public int getOutputTanks() {
        return outputTanks;
    }

    public SimpleFluidContainer getInputs(boolean copy) {
        SimpleFluidContainer fluidContainer = new SimpleFluidContainer(this.inputTanks);
        for (int i = 0; i < this.inputTanks; i++) {
            if (copy) {
                fluidContainer.setFluid(i, this.fluidTanks.getFluidInTank(i).copy());
            } else {
                fluidContainer.setFluid(i, this.fluidTanks.getFluidInTank(i));
            }
        }
        return fluidContainer;
    }

    public boolean inputsChanged(SimpleFluidContainer newInputs) {
        for (int i = 0; i < this.inputTanks; i++) {
            FluidStack newFluid = newInputs.getFluid(i);
            FluidStack tankFluid = this.fluidTanks.getFluidInTank(i);
            if (!FluidStack.isSameFluid(newFluid, tankFluid)
                    || newFluid.getAmount() != tankFluid.getAmount()) {
                return true;
            }
        }
        return false;
    }

    public boolean canInsertFluidIntoTank(int tank, FluidStack fluid) {
        return fluidTanks.getFluidInTank(tank).isEmpty()
                || FluidStack.isSameFluid(fluidTanks.getFluidInTank(tank), fluid)
                && fluidTanks.getFluidInTank(tank).getAmount()
                + fluid.getAmount() <= fluidTanks.getTankCapacity(tank);
    }

    public FluidStack drain(int tank, FluidStack stack, FluidAction action) {
        return fluidTanks.drain(tank, stack, action);
    }

    public int fill(int tank, FluidStack stack, FluidAction action) {
        return fluidTanks.fill(tank, stack, action);
    }

    public FluidStack getFluidInTank(int i) {
        return fluidTanks.getFluidInTank(i);
    }

}
