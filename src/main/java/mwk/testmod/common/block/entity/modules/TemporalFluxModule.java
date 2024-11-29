package mwk.testmod.common.block.entity.modules;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import mwk.testmod.common.util.handlers.TemporalFluxHandler;
import mwk.testmod.common.util.handlers.TemporalFluxWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.neoforged.neoforge.common.util.Lazy;

public class TemporalFluxModule implements MachineModule {

    public static final String NBT_TAG_FLUX = "temporalFlux";

    private final TemporalFluxHandler temporalFluxHandler;
    private final Lazy<ITemporalFluxHandler> temporalFluxWrapper;

    public TemporalFluxModule(MachineBlockEntity machine, int maxFlux) {
        this.temporalFluxHandler = new TemporalFluxHandler(maxFlux);
        this.temporalFluxWrapper = Lazy.of(
                () -> new TemporalFluxWrapper(temporalFluxHandler, machine));
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.put(NBT_TAG_FLUX, temporalFluxHandler.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        if (tag.contains(NBT_TAG_FLUX)) {
            temporalFluxHandler.deserializeNBT(registries,
                    IntTag.valueOf(tag.getInt(NBT_TAG_FLUX)));
        }
    }

    public int getTemporalFluxStored() {
        return temporalFluxHandler.getTemporalFluxStored();
    }

    public int getMaxTemporalFluxStored() {
        return temporalFluxHandler.getMaxTemporalFluxStored();
    }

    public ITemporalFluxHandler getTemporalFluxHandler(Direction direction) {
        return temporalFluxWrapper.get();
    }

    public ITemporalFluxHandler getTemporalFluxHandler() {
        return getTemporalFluxHandler(null);
    }

    public int recieveTemporalFlux(int amount, boolean simulate) {
        return temporalFluxHandler.receiveTemporalFlux(amount, simulate);
    }

    public int extractTemporalFlux(int amount, boolean simulate) {
        return temporalFluxHandler.extractTemporalFlux(amount, simulate);
    }
}
