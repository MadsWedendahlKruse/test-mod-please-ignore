package mwk.testmod.common.block.cable;

import mwk.testmod.common.block.cable.network.CableNetworkManager;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableBlockEntity extends BlockEntity {

    public CableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CABLE_ENTITY_TYPE.get(), pos, blockState);
    }

    public IEnergyStorage getEnergyHandler(Direction direction) {
        return CableNetworkManager.getInstance().getEnergyStorage(worldPosition);
    }
}
