package mwk.testmod.common.block.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.entity.modules.TemporalFluxModule;
import mwk.testmod.common.block.inventory.TemporalReservoirMenu;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TemporalReservoirBlockEntity extends MachineBlockEntity {

    public TemporalReservoirBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.CHRONO_CELL_ENTITY_TYPE.get(), pos, state);
        addModule(new TemporalFluxModule(this, 1048576));
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_CHRONO_CELL;
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.TEMPORAL_RESERVOIR.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new TemporalReservoirMenu(i, player, worldPosition);
    }

    @Override
    public void resetUpgrades() {
        // Do nothing
    }

    @Override
    public void installUpgrade(UpgradeItem upgrade) {
        // Do nothing
    }

    @Override
    public boolean isUpgradeValid(UpgradeItem upgrade) {
        return false;
    }
}
