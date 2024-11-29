package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.TemporalConduitNetwork;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkTemporalFluxHandler;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class TemporalConduitBlockEntity extends ConduitBlockEntity<ITemporalFluxHandler> {

    public TemporalConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CONDUIT_TEMPORAL_ENTITY_TYPE.get(), ConduitType.TEMPORAL, pos,
                blockState);
    }

    @Override
    protected ITemporalFluxHandler createNewCapability(Direction direction) {
        if (network instanceof TemporalConduitNetwork temporalNetwork
                && level instanceof ServerLevel serverLevel) {
            return new NetworkTemporalFluxHandler(serverLevel, temporalNetwork, worldPosition,
                    direction);
        }
        return null;
    }
}
