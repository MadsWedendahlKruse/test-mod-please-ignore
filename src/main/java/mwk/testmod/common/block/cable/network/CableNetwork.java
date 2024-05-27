package mwk.testmod.common.block.cable.network;

import java.util.HashSet;
import java.util.Set;
import mwk.testmod.TestMod;
import mwk.testmod.common.util.energy.EnergyStorageVariableCapacity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableNetwork implements INBTSerializable<CompoundTag> {

    public static final String NBT_TAG_TRANSFER_RATE = "transferRate";
    public static final String NBT_TAG_SIZE = "size";
    public static final String NBT_TAG_POS = "pos";
    public static final String NBT_TAG_ENERGY = "energy";

    private int transferRate;
    private final Set<BlockPos> positions;
    private EnergyStorage energyStorage;
    // The master position is the position of the cable that is in charge of serializing the network
    // data and saving it to disk
    private BlockPos masterPos;

    public CableNetwork(int transferRate) {
        this.transferRate = transferRate;
        this.positions = new HashSet<>();
        this.energyStorage = new EnergyStorageVariableCapacity(transferRate) {
            @Override
            public int getMaxEnergyStored() {
                return positions.size() * getTransferRate();
            }
        };
    }

    public void add(BlockPos pos) {
        positions.add(pos);
    }

    public void remove(BlockPos pos) {
        positions.remove(pos);
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getTransferRate() {
        return transferRate;
    }

    public int getSize() {
        return positions.size();
    }

    public Set<BlockPos> getPositions() {
        return positions;
    }

    public void merge(CableNetwork other) {
        positions.addAll(other.positions);
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
        TestMod.LOGGER.info("Cable at " + masterPos + " is now the master of " + this);
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public boolean isMasterPos(BlockPos cable) {
        return masterPos.equals(cable);
    }

    public void clearMasterPos() {
        positions.remove(masterPos);
        masterPos = null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_TAG_TRANSFER_RATE, transferRate);
        tag.putInt(NBT_TAG_SIZE, positions.size());
        BlockPos[] posArray = positions.toArray(new BlockPos[0]);
        for (int i = 0; i < positions.size(); i++) {
            tag.putLong(NBT_TAG_POS + i, posArray[i].asLong());
        }
        tag.put(NBT_TAG_ENERGY, energyStorage.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(NBT_TAG_TRANSFER_RATE)) {
            transferRate = tag.getInt(NBT_TAG_TRANSFER_RATE);
        }
        if (tag.contains(NBT_TAG_SIZE)) {
            int size = tag.getInt(NBT_TAG_SIZE);
            positions.clear();
            for (int i = 0; i < size; i++) {
                if (tag.contains(NBT_TAG_POS + i)) {
                    positions.add(BlockPos.of(tag.getLong(NBT_TAG_POS + i)));
                }
            }
        }
        energyStorage = new EnergyStorageVariableCapacity(transferRate) {
            @Override
            public int getMaxEnergyStored() {
                return positions.size() * getTransferRate();
            }
        };
        if (tag.contains(NBT_TAG_ENERGY)) {
            energyStorage.deserializeNBT(IntTag.valueOf(tag.getInt(NBT_TAG_ENERGY)));
        }
    }
}
