package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.FluidConduitNetwork;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkFluidHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidConduitBlockEntity extends ConduitBlockEntity<IFluidHandler> {

    private NetworkFluidHandler fluidHandler;

    public FluidConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CONDUIT_FLUID_ENTITY_TYPE.get(), ConduitType.FLUID, pos,
                blockState);
    }

    public NetworkFluidHandler getFluidHandler(Direction direction) {
        if (fluidHandler == null) {
            if (network instanceof FluidConduitNetwork fluidNetwork
                    && level instanceof ServerLevel serverLevel) {
                fluidHandler = new NetworkFluidHandler(serverLevel, fluidNetwork, worldPosition);
            }
        }
        return fluidHandler;
    }

}
