package mwk.testmod.common.util.handlers;

import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TemporalFluxWrapper implements ITemporalFluxHandler {

    private final ITemporalFluxHandler temporalFluxHandler;
    private final BlockEntity blockEntity;

    public TemporalFluxWrapper(ITemporalFluxHandler temporalFluxHandler, BlockEntity blockEntity) {
        this.temporalFluxHandler = temporalFluxHandler;
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean canReceive() {
        return temporalFluxHandler.canReceive();
    }

    @Override
    public int receiveTemporalFlux(int amount, boolean simulate) {
        blockEntity.setChanged();
        return temporalFluxHandler.receiveTemporalFlux(amount, simulate);
    }

    @Override
    public boolean canExtract() {
        return temporalFluxHandler.canExtract();
    }

    @Override
    public int extractTemporalFlux(int amount, boolean simulate) {
        blockEntity.setChanged();
        return temporalFluxHandler.extractTemporalFlux(amount, simulate);
    }

    @Override
    public int getTemporalFluxStored() {
        return temporalFluxHandler.getTemporalFluxStored();
    }

    @Override
    public int getMaxTemporalFluxStored() {
        return temporalFluxHandler.getMaxTemporalFluxStored();
    }
}
