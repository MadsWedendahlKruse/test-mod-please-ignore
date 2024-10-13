package mwk.testmod.common.block.entity.base;

import mwk.testmod.common.util.energy.EnergyStorageConsumer;
import mwk.testmod.common.util.energy.EnergyStorageProducer;
import mwk.testmod.common.util.energy.EnergyStorageWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * A block entity that stores energy and provides an energy handler.
 */
public class EnergyBlockEntity extends BlockEntity {

    public static final String NBT_TAG_ENERGY = "energy";

    protected final EnergyStorage energyStorage;
    protected final Lazy<IEnergyStorage> energyWrapper;

    // TODO: Overengineered?
    public enum EnergyType {
        STORAGE, CONSUMER, PRODUCER
    }

    public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, EnergyType energyType) {
        super(type, pos, state);
        this.energyStorage = new EnergyStorage(maxEnergy);
        this.energyWrapper = switch (energyType) {
            case STORAGE -> Lazy.of(() -> new EnergyStorageWrapper(this.energyStorage, this));
            case CONSUMER -> Lazy.of(() -> new EnergyStorageConsumer(this.energyStorage, this));
            case PRODUCER -> Lazy.of(() -> new EnergyStorageProducer(this.energyStorage, this));
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(NBT_TAG_ENERGY, energyStorage.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(NBT_TAG_ENERGY)) {
            energyStorage.deserializeNBT(registries, IntTag.valueOf(tag.getInt(NBT_TAG_ENERGY)));
        }
    }

    public void pushEnergy(BlockPos pos, int energyPerTick) {
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
            energyStorage.extractEnergy(received, false);
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
}
