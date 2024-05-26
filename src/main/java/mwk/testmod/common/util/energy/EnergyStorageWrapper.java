package mwk.testmod.common.util.energy;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageWrapper implements IEnergyStorage {

    private final IEnergyStorage energyStorage;
    protected final BlockEntity blockEntity;

    public EnergyStorageWrapper(IEnergyStorage energyStorage, BlockEntity blockEntity) {
        this.energyStorage = energyStorage;
        this.blockEntity = blockEntity;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        blockEntity.setChanged();
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        blockEntity.setChanged();
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canReceive();
    }
}
