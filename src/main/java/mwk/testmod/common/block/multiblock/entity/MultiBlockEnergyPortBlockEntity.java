package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.CapacitronBlockEntity;
import mwk.testmod.common.block.entity.base.EnergyBlockEntity;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class MultiBlockEnergyPortBlockEntity extends MultiBlockPartBlockEntity
        implements ITickable {

    public MultiBlockEnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.MULTI_ENERGY_PORT_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    public IEnergyStorage getEnergyHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof EnergyBlockEntity energyEntity) {
                return energyEntity.getEnergyStorage(direction);
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (!isFormed()) {
            return;
        }
        BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
        if (controllerEntity instanceof GeneratorBlockEntity<?> generator) {
            // Generator can push twice the energy per tick it generates
            generator.pushEnergy(this.worldPosition, 2 * generator.getEnergyPerTick());
        }
        if (controllerEntity instanceof CapacitronBlockEntity capacitron) {
            // TODO: Push as much as possible?
            capacitron.pushEnergy(this.worldPosition, Integer.MAX_VALUE);
        }
    }
}
