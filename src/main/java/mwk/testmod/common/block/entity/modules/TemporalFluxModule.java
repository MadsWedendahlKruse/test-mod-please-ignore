package mwk.testmod.common.block.entity.modules;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import mwk.testmod.common.util.handlers.TemporalFluxHandler;
import mwk.testmod.common.util.handlers.TemporalFluxWrapper;
import mwk.testmod.init.registries.TestModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerLevel;
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

    public void pushTemporalFlux(ServerLevel level, BlockPos pos, int temporalFluxPerTick) {
        // TODO: This entire method is a copy of EnergyModule.pushEnergy
        if (getTemporalFluxStored() == 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            // TODO: Capability cache
            ITemporalFluxHandler receiver = level.getCapability(
                    TestModCapabilities.TemporalFluxHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            if (receiver == null || receiver == this.getTemporalFluxHandler(direction)) {
                continue;
            }
            // We don't want to transfer more energy than we have
            // Generator can push twice as much energy as it can generate so we don't
            // end up with a full buffer that never gets emptied
            int maxTransfer = Math.min(getTemporalFluxStored(), temporalFluxPerTick);
            int received = receiver.receiveTemporalFlux(maxTransfer, false);
            int extracted = temporalFluxHandler.extractTemporalFlux(received, false);
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
