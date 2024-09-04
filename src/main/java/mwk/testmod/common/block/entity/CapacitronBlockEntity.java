package mwk.testmod.common.block.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.inventory.CapacitronMenu;
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

public class CapacitronBlockEntity extends MachineBlockEntity {

    public CapacitronBlockEntity(BlockPos pos, BlockState state) {
        // TODO: Different tiers of capacitrons?
        super(TestModBlockEntities.CAPACITRON_ENTITY_TYPE.get(), pos, state, 1048576,
                EnergyType.STORAGE, 0, 0, 0, EMPTY_TANKS, EMPTY_TANKS);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_CAPACITRON;
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.CAPACITRON.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
            Player pPlayer) {
        return new CapacitronMenu(pContainerId, pPlayer, worldPosition);
    }

    @Override
    protected void resetUpgrades() {

    }

    @Override
    protected void installUpgrade(UpgradeItem upgrade) {

    }

    @Override
    public boolean isUpgradeValid(UpgradeItem upgrade) {
        return false;
    }
}
