package mwk.testmod.common.util.energy;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageProducer extends EnergyStorageWrapper {

    public EnergyStorageProducer(IEnergyStorage energyStorage, BlockEntity blockEntity) {
        super(energyStorage, blockEntity);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    public int generateEnergy(int maxGenerate, boolean simulate) {
        return super.receiveEnergy(maxGenerate, simulate);
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public boolean canExtract() {
        // Producer is responsible for pushing energy to the grid
        // return false;
        // TOOD: I don't think the above is correct?
        return true;
    }
}
