package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.EnergyConduitNetwork;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkEnergyStorage;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyConduitBlockEntity extends ConduitBlockEntity<IEnergyStorage> {

    public EnergyConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CONDUIT_ENERGY_ENTITY_TYPE.get(), ConduitType.ENERGY, pos,
                blockState);
    }

    @Override
    protected IEnergyStorage createNewCapability(Direction direction) {
        if (network instanceof EnergyConduitNetwork energyNetwork
                && level instanceof ServerLevel serverLevel) {
            return new NetworkEnergyStorage(serverLevel, energyNetwork, worldPosition, direction);
        }
        return null;
    }
}
