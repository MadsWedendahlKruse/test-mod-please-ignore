package mwk.testmod.common.block.conduit.network.capabilites;

import mwk.testmod.common.block.conduit.network.TemporalConduitNetwork;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class NetworkTemporalFluxHandler extends NetworkCapabilityProvider<TemporalConduitNetwork>
        implements ITemporalFluxHandler {

    public NetworkTemporalFluxHandler(ServerLevel level, TemporalConduitNetwork network,
            BlockPos pos, Direction direction) {
        super(level, network, pos, direction);
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int receiveTemporalFlux(int amount, boolean simulate) {
        return network.receivePayload(level, pos, direction, amount, simulate);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public int extractTemporalFlux(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int getTemporalFluxStored() {
        // TODO: Implement this method
        return 1;
    }

    @Override
    public int getMaxTemporalFluxStored() {
        // TODO: Implement this method
        return 1;
    }
}
