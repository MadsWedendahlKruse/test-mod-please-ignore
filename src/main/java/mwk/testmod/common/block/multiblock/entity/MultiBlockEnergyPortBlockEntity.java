package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.EnergyBlockEntity;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class MultiBlockEnergyPortBlockEntity extends MultiBlockPartBlockEntity {

    public MultiBlockEnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.MULTI_ENERGY_PORT_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    public IEnergyStorage getEnergyHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof EnergyBlockEntity energyEntity) {
                return energyEntity.getEnergyHandler(null);
            }
        }
        return null;
    }
}
