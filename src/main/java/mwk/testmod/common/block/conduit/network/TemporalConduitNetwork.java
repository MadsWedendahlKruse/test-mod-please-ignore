package mwk.testmod.common.block.conduit.network;

import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public class TemporalConduitNetwork extends ConduitNetwork<ITemporalFluxHandler, Integer> {

    public TemporalConduitNetwork() {
        super(ConduitType.TEMPORAL);
    }

    @Override
    protected @NotNull Integer createEmptyPayload(Integer payload) {
        return 0;
    }

    @Override
    protected boolean isPayloadEmpty(Integer payload) {
        // TODO: What if it's negative?
        return payload == 0;
    }

    @Override
    protected Integer aggregatePayloads(Integer aggregate, Integer receivedPayload) {
        return aggregate + receivedPayload;
    }

    @Override
    protected Integer getRemainingPayload(Integer payload, Integer receivedPayload) {
        return payload - receivedPayload;
    }

    @Override
    public void pullPayload(ServerLevel level, BlockPos start, Direction direction,
            ITemporalFluxHandler source) {
        // TODO: I think energy conduits shouldn't do anything here?
    }

    @Override
    protected Integer transferPayload(@NotNull ITemporalFluxHandler receiver, Integer payload,
            boolean simulate) {
        return receiver.receiveTemporalFlux(payload, simulate);
    }

}
