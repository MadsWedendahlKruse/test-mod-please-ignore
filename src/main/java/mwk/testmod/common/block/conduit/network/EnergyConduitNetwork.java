package mwk.testmod.common.block.conduit.network;

import org.jetbrains.annotations.NotNull;
import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.neoforged.neoforge.energy.IEnergyStorage;

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
    protected Integer transferPayload(@NotNull IEnergyStorage receiver, Integer payload,
            boolean simulate) {
        return receiver.receiveEnergy(payload, simulate);
    }

}
