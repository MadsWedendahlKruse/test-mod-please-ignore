package mwk.testmod.common.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageGenerator extends EnergyStorageWrapper {

    public EnergyStorageGenerator(IEnergyStorage energyStorage, BlockEntity blockEntity) {
        super(energyStorage, blockEntity);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public boolean canExtract() {
        // Generator is responsible for pushing energy to the grid
        return false;
    }
}
