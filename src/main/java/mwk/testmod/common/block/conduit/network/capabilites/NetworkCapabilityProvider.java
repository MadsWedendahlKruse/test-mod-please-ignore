package mwk.testmod.common.block.conduit.network.capabilites;

import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class NetworkCapabilityProvider<T extends ConduitNetwork<?, ?>> {

    // Reference to the world this capability provider belongs to
    protected final ServerLevel level;
    // Reference to the network this capability provider belongs to
    protected final T network;
    // Position of the block this capability provider belongs to
    protected final BlockPos pos;
    // The direction the capability is being requested from
    protected final Direction direction;

    public NetworkCapabilityProvider(ServerLevel level, T network, BlockPos pos,
            Direction direction) {
        this.level = level;
        this.network = network;
        this.pos = pos;
        this.direction = direction;
    }
}
