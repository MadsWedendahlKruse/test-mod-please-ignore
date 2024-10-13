package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.FluidConduitNetwork;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkFluidHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidConduitBlockEntity extends ConduitBlockEntity<IFluidHandler> {

    public static final String NBT_TAG_PAYLOAD_FLUID = "payloadFluid";
    public static final String NBT_TAG_PAYLOAD_TIMESTAMP = "payloadTimestamp";

    private FluidStack fluidStack;
    private long fluidStackTimestamp;

    public FluidConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CONDUIT_FLUID_ENTITY_TYPE.get(), ConduitType.FLUID, pos,
                blockState);
    }

    @Override
    protected IFluidHandler createNewCapability(Direction direction) {
        if (network instanceof FluidConduitNetwork fluidNetwork
                && level instanceof ServerLevel serverLevel) {
            return new NetworkFluidHandler(serverLevel, fluidNetwork, worldPosition, direction);
        }
        return null;
    }

    public void setFluidStack(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
        this.fluidStackTimestamp = level.getGameTime();
    }

    public FluidStack getFluidStack() {
        if (fluidStack == null || fluidStackTimestamp < level.getGameTime() - 20) {
            return FluidStack.EMPTY;
        }
        return fluidStack;
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (fluidStack != null) {
            tag.put(NBT_TAG_PAYLOAD_FLUID, fluidStack.save(registries, new CompoundTag()));
        }
        tag.putLong(NBT_TAG_PAYLOAD_TIMESTAMP, fluidStackTimestamp);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            Provider registries) {
        super.onDataPacket(net, pkt, registries);
        CompoundTag tag = pkt.getTag();
        if (tag.contains(NBT_TAG_PAYLOAD_FLUID)) {
            fluidStack = FluidStack.parse(registries, tag.getCompound(NBT_TAG_PAYLOAD_FLUID)).get();
        }
        fluidStackTimestamp = tag.getLong(NBT_TAG_PAYLOAD_TIMESTAMP);
    }

}
