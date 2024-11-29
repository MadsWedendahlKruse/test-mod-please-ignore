package mwk.testmod.common.block.multiblock.entity.ports;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.entity.modules.TemporalFluxModule;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.entity.MultiBlockPartBlockEntity;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockTemporalFluxPortBlockEntity extends MultiBlockPartBlockEntity
        implements ITickable {

    public MultiBlockTemporalFluxPortBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.MULTI_TEMPORAL_FLUX_PORT_ENTITY_TYPE.get(), pos, state);
    }

    public ITemporalFluxHandler getTemporalFluxHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof MachineBlockEntity machine &&
                    machine.temporalFlux().isPresent()) {
                return machine.temporalFlux().get().getTemporalFluxHandler(direction);
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
                machine.temporalFlux().isPresent()) {
            TemporalFluxModule temporalFluxModule = machine.temporalFlux().get();
            // TODO: Implement temporal flux pushing
//            if (controllerEntity instanceof GeneratorBlockEntity<?, ?> generator &&
//                    generator.processing().isPresent()) {
//                // Generator can push twice the energy per tick it generates
//                ProcessingModule<?, ?> processing = generator.processing().get();
//                energyModule.pushEnergy(serverLevel, this.worldPosition,
//                        2 * processing.getResourcePerTick());
//            }
//            if (controllerEntity instanceof CapacitronBlockEntity capacitron) {
//                // TODO: Push as much as possible?
//                energyModule.pushEnergy(serverLevel, this.worldPosition, Integer.MAX_VALUE);
//            }
        }
    }
}
