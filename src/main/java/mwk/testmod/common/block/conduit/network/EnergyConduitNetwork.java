package mwk.testmod.common.block.conduit.network;

import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class EnergyConduitNetwork extends ConduitNetwork<IEnergyStorage, Integer> {

    public EnergyConduitNetwork() {
        super(ConduitType.ENERGY);
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
            IEnergyStorage source) {
        // TODO: I think energy conduits shouldn't do anything here?
    }

    @Override
    protected Integer transferPayload(@NotNull IEnergyStorage receiver, Integer payload,
            boolean simulate) {
        return receiver.receiveEnergy(payload, simulate);
    }

}
