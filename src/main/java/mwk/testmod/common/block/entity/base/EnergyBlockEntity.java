package mwk.testmod.common.block.entity.base;

import mwk.testmod.TestMod;
import mwk.testmod.TestModConfig;
import mwk.testmod.common.util.energy.EnergyStorageConsumer;
import mwk.testmod.common.util.energy.EnergyStorageGenerator;
import mwk.testmod.common.util.energy.EnergyStorageWrapper;
import net.minecraft.core.BlockPos;
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

    protected final EnergyStorage energy;
    protected final Lazy<IEnergyStorage> energyHandler;

    // TODO: Overengineered?
    public enum EnergyType {
        STORAGE, CONSUMER, GENERATOR
    }

    public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            EnergyStorage energy, EnergyType energyType) {
        super(type, pos, state);
        this.energy = energy;
        this.energyHandler = switch (energyType) {
            case STORAGE -> Lazy.of(() -> new EnergyStorageWrapper(energy, this));
            case CONSUMER -> Lazy.of(() -> new EnergyStorageConsumer(energy, this));
            case GENERATOR -> Lazy.of(() -> new EnergyStorageGenerator(energy, this));
        };
        // TODO: SUPER ILLEGAL! Only for testing purposes
        energy.receiveEnergy(TestModConfig.ENERGY_CAPACITY_DEFAULT.get() / 2, false);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBT_TAG_ENERGY, energy.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        TestMod.LOGGER.debug(tag.toString());
        super.load(tag);
        if (tag.contains(NBT_TAG_ENERGY)) {
            energy.deserializeNBT(IntTag.valueOf(tag.getInt(NBT_TAG_ENERGY)));
        }
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energy.getMaxEnergyStored();
    }

    public IEnergyStorage getEnergyHandler() {
        return energyHandler.get();
    }
}
