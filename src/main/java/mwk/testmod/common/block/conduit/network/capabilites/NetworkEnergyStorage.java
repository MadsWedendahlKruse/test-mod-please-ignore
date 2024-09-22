package mwk.testmod.common.block.conduit.network.capabilites;

import mwk.testmod.common.block.conduit.network.EnergyConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class NetworkEnergyStorage extends NetworkCapabilityProvider<EnergyConduitNetwork>
        implements IEnergyStorage {

    public NetworkEnergyStorage(ServerLevel level, EnergyConduitNetwork network, BlockPos pos,
            Direction direction) {
        super(level, network, pos, direction);
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        return network.receivePayload(level, pos, direction, energy, simulate);
    }

    @Override
    public boolean canExtract() {
        // Not 100% sure about this one
        return false;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        // TODO: Implement this method
        return 1;
    }

    @Override
    public int getMaxEnergyStored() {
        // TODO: Implement this method
        return 1;
    }

}
