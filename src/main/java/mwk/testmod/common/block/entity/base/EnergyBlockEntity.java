package mwk.testmod.common.block.entity.base;

import mwk.testmod.common.util.energy.EnergyStorageConsumer;
import mwk.testmod.common.util.energy.EnergyStorageProducer;
import mwk.testmod.common.util.energy.EnergyStorageWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
            EnergyStorage energy, EnergyType energyType) {
        super(type, pos, state);
        this.energyStorage = energy;
        this.energyWrapper = switch (energyType) {
            case STORAGE -> Lazy.of(() -> new EnergyStorageWrapper(energy, this));
            case CONSUMER -> Lazy.of(() -> new EnergyStorageConsumer(energy, this));
            case PRODUCER -> Lazy.of(() -> new EnergyStorageProducer(energy, this));
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBT_TAG_ENERGY, energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_ENERGY)) {
            energyStorage.deserializeNBT(IntTag.valueOf(tag.getInt(NBT_TAG_ENERGY)));
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
