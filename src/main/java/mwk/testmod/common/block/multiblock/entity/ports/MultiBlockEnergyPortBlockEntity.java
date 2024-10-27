package mwk.testmod.common.block.multiblock.entity.ports;

import mwk.testmod.common.block.entity.CapacitronBlockEntity;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.entity.modules.ProcessingModule;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.entity.MultiBlockPartBlockEntity;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
            if (controllerEntity instanceof MachineBlockEntity machine &&
                    machine.energy().isPresent()) {
                return machine.energy().get().getEnergyStorage(direction);
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
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (controllerEntity instanceof MachineBlockEntity machine &&
                machine.energy().isPresent()) {
            EnergyModule energyModule = machine.energy().get();
            if (controllerEntity instanceof GeneratorBlockEntity<?, ?> generator &&
                    generator.processing().isPresent()) {
                // Generator can push twice the energy per tick it generates
                ProcessingModule<?, ?> processing = generator.processing().get();
                energyModule.pushEnergy(serverLevel, this.worldPosition,
                        2 * processing.getResourcePerTick());
            }
            if (controllerEntity instanceof CapacitronBlockEntity capacitron) {
                // TODO: Push as much as possible?
                energyModule.pushEnergy(serverLevel, this.worldPosition, Integer.MAX_VALUE);
            }
        }

    }
}
