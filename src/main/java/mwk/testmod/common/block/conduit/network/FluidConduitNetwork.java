package mwk.testmod.common.block.conduit.network;

import org.jetbrains.annotations.NotNull;
import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidConduitNetwork extends ConduitNetwork<FluidStack> {

    public FluidConduitNetwork() {
        super(ConduitType.FLUID);
    }

    @Override
    protected @NotNull FluidStack createEmptyPayload(FluidStack payload) {
        FluidStack empty = payload.copy();
        empty.setAmount(0);
        return empty;
    }

    @Override
    protected boolean isPayloadEmpty(FluidStack payload) {
        return payload.isEmpty();
    }

    @Override
    protected FluidStack aggregatePayloads(FluidStack aggregate, FluidStack receivedPayload) {
        aggregate.setAmount(aggregate.getAmount() + receivedPayload.getAmount());
        return aggregate;
    }

    @Override
    protected FluidStack getRemainingPayload(FluidStack payload, FluidStack receivedPayload) {
        FluidStack remaining = payload.copy();
        if (remaining.isEmpty()) {
            return remaining;
        }
        remaining.setAmount(payload.getAmount() - receivedPayload.getAmount());
        return remaining;
    }

    @Override
    protected FluidStack transferPayload(@NotNull Object receiver, FluidStack payload,
            boolean simulate) {
        // TODO: This seems completely different from the other implementations
        if (receiver instanceof IFluidHandler handler) {
            FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
            int amount = handler.fill(payload, action);
            return new FluidStack(payload.getFluid(), amount);
        }
        return FluidStack.EMPTY;
    }

}
