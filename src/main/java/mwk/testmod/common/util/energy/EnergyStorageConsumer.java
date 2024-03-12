package mwk.testmod.common.util.energy;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageConsumer extends EnergyStorageWrapper {

    public EnergyStorageConsumer(IEnergyStorage energyStorage, BlockEntity blockEntity) {
        super(energyStorage, blockEntity);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        blockEntity.setChanged();
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean canExtract() {
        return false;
    }
}
