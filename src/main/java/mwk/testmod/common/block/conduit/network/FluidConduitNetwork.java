package mwk.testmod.common.block.conduit.network;

import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.conduit.FluidConduitBlockEntity;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class FluidConduitNetwork extends ConduitNetwork<IFluidHandler, FluidStack> {

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
    protected FluidStack transferPayload(@NotNull IFluidHandler receiver, FluidStack payload,
            boolean simulate) {
        FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
        int amount = receiver.fill(payload, action);
        return new FluidStack(payload.getFluid(), amount);
    }

    @Override
    public FluidStack receivePayload(ServerLevel level, BlockPos start, Direction direction,
            FluidStack payload, boolean simulate) {
        FluidStack received = super.receivePayload(level, start, direction, payload, simulate);
        if (!isPayloadEmpty(received)) {
            for (BlockPos pos : getPositions()) {
                if (level.getBlockEntity(pos) instanceof FluidConduitBlockEntity conduit) {
                    conduit.setFluidStack(received);
                    // TODO: Use a custom packet for performance instead of a block update
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos),
                            Block.UPDATE_CLIENTS);
                }
            }
        }
        return received;
    }

    @Override
    public void pullPayload(ServerLevel level, BlockPos start, Direction direction,
            IFluidHandler source) {
        // Pull from the source
        for (int i = 0; i < source.getTanks(); i++) {
            // TODO: drain limit?
            FluidStack stack = source.drain(1000, FluidAction.SIMULATE);
            if (!stack.isEmpty()) {
                FluidStack received = receivePayload(level, start, direction, stack, false);
                source.drain(received.getAmount(), FluidAction.EXECUTE);
            }
        }
    }
}
