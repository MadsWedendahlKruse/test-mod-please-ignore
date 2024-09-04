package mwk.testmod.common.block.entity;

import mwk.testmod.common.block.entity.base.EnergyBlockEntity;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TeleporterBlockEntity extends EnergyBlockEntity {

    public TeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.TELEPORTER_ENTITY_TYPE.get(), pos, state,
                10_000_000, EnergyType.CONSUMER);
    }
}
