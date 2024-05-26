package mwk.testmod.common.util.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class EnergyStorageVariableCapacity extends EnergyStorage {

    public EnergyStorageVariableCapacity(int transferRate) {
        super(0, Integer.MAX_VALUE, transferRate);
    }

    public abstract int getMaxEnergyStored();

    public int receiveEnergy(int maxReceive, boolean simulate) {
        // Same as EnergyStorage#receiveEnergy, except for the max energy stored check
        // instead of this.capacity
        if (!this.canReceive()) {
            return 0;
        } else {
            int energyReceived = Math.min(getMaxEnergyStored() - this.energy,
                    Math.min(this.maxReceive, maxReceive));
            if (!simulate) {
                this.energy += energyReceived;
            }

            return energyReceived;
        }
    }
}
