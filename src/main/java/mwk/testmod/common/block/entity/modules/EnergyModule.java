package mwk.testmod.common.block.entity.modules;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.util.handlers.EnergyStorageConsumer;
import mwk.testmod.common.util.handlers.EnergyStorageProducer;
import mwk.testmod.common.util.handlers.EnergyStorageWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyModule implements MachineModule {

    public static final String NBT_TAG_ENERGY = "energy";

    // TODO: Overengineered?
    public enum EnergyType {
        STORAGE, CONSUMER, PRODUCER
    }

    private final EnergyStorage energyStorage;
    private final Lazy<IEnergyStorage> energyWrapper;
    private final EnergyType energyType;

    public EnergyModule(MachineBlockEntity machine, int maxEnergy,
            EnergyType energyType) {
        this.energyStorage = new EnergyStorage(maxEnergy);
        this.energyWrapper = switch (energyType) {
            case STORAGE -> Lazy.of(() -> new EnergyStorageWrapper(this.energyStorage, machine));
            case CONSUMER -> Lazy.of(() -> new EnergyStorageConsumer(this.energyStorage, machine));
            case PRODUCER -> Lazy.of(() -> new EnergyStorageProducer(this.energyStorage, machine));
        };
        this.energyType = energyType;
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.put(NBT_TAG_ENERGY, energyStorage.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        if (tag.contains(NBT_TAG_ENERGY)) {
            energyStorage.deserializeNBT(registries, IntTag.valueOf(tag.getInt(NBT_TAG_ENERGY)));
        }
    }

    public void pushEnergy(ServerLevel level, BlockPos pos, int energyPerTick) {
        if (getEnergyStored() == 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            // TODO: Capability cache
            IEnergyStorage receiver = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            if (receiver == null || receiver == this.getEnergyStorage(direction)) {
                continue;
            }
            // We don't want to transfer more energy than we have
            // Generator can push twice as much energy as it can generate so we don't
            // end up with a full buffer that never gets emptied
            int maxTransfer = Math.min(getEnergyStored(), energyPerTick);
            int received = receiver.receiveEnergy(maxTransfer, false);
            int extracted = energyStorage.extractEnergy(received, false);
        }
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public IEnergyStorage getEnergyStorage(Direction direction) {
        return energyWrapper.get();
    }

    public IEnergyStorage getEnergyStorage() {
        return getEnergyStorage(null);
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }
}
