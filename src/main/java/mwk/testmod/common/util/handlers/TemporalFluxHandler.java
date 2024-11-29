package mwk.testmod.common.util.handlers;

import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.UnknownNullability;

public class TemporalFluxHandler implements ITemporalFluxHandler, INBTSerializable<Tag> {

    // No point in implementing the same code twice, so we can just an energy
    // storage under the hood
    private final EnergyStorage fluxStorage;

    public TemporalFluxHandler(int capacity) {
        this.fluxStorage = new EnergyStorage(capacity);
    }

    public TemporalFluxHandler(int capacity, int maxTransfer) {
        this.fluxStorage = new EnergyStorage(capacity, maxTransfer);
    }

    public TemporalFluxHandler(int capacity, int maxReceive, int maxExtract) {
        this.fluxStorage = new EnergyStorage(capacity, maxReceive, maxExtract);
    }

    public TemporalFluxHandler(int capacity, int maxReceive, int maxExtract, int energy) {
        this.fluxStorage = new EnergyStorage(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public boolean canReceive() {
        return fluxStorage.canReceive();
    }

    @Override
    public int receiveTemporalFlux(int amount, boolean simulate) {
        return fluxStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public boolean canExtract() {
        return fluxStorage.canExtract();
    }

    @Override
    public int extractTemporalFlux(int amount, boolean simulate) {
        return fluxStorage.extractEnergy(amount, simulate);
    }

    @Override
    public int getTemporalFluxStored() {
        return fluxStorage.getEnergyStored();
    }

    @Override
    public int getMaxTemporalFluxStored() {
        return fluxStorage.getMaxEnergyStored();
    }

    @Override
    public @UnknownNullability Tag serializeNBT(Provider provider) {
        return fluxStorage.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(Provider provider, Tag tag) {
        fluxStorage.deserializeNBT(provider, (IntTag) tag);
    }
}
