package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.EnergyConduitNetwork;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkEnergyStorage;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.server.level.ServerLevel;

public class EnergyConduitBlockEntity extends ConduitBlockEntity<IEnergyStorage> {

    private NetworkEnergyStorage energyStorage;

    public EnergyConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CONDUIT_ENERGY_ENTITY_TYPE.get(), ConduitType.ENERGY, pos,
                blockState);
    }

    public NetworkEnergyStorage getEnergyStorage(Direction direction) {
        // TODO: Is this the best way to handle this?
        // Seems like there's a lot of moving parts here
        if (energyStorage == null) {
            if (network instanceof EnergyConduitNetwork energyNetwork
                    && level instanceof ServerLevel serverLevel) {
                energyStorage = new NetworkEnergyStorage(serverLevel, energyNetwork, worldPosition);
            }
        }
        return energyStorage;
    }

}
