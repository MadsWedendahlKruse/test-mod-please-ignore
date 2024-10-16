package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.ItemConduitNetwork;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkItemHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemConduitBlockEntity extends ConduitBlockEntity<IItemHandler> {

    public ItemConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CONDUIT_ITEM_ENTITY_TYPE.get(), ConduitType.ITEM, pos,
                blockState);
    }

    @Override
    protected IItemHandler createNewCapability(Direction direction) {
        if (network instanceof ItemConduitNetwork itemNetwork
                && level instanceof ServerLevel serverLevel) {
            return new NetworkItemHandler(serverLevel, itemNetwork, worldPosition, direction);
        }
        return null;
    }
}
