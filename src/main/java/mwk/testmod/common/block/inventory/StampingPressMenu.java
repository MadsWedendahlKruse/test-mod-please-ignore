package mwk.testmod.common.block.inventory;

import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.client.utils.ItemSlotGridHelper;
import mwk.testmod.common.util.inventory.handler.InputItemHandler;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StampingPressMenu extends ProcessingMenu {

    public StampingPressMenu(int containerId, Player player, BlockPos pos) {
        super(TestModMenus.STAMPING_PRESS_MENU.get(), containerId, player, pos,
                DEFAULT_PLAYER_INVENTORY_X, DEFAULT_PLAYER_INVENTORY_Y, 44, 0, 116, 45);
    }

    @Override
    protected void addInputSlots() {
        InputItemHandler inputHandler = blockEntity.getInputItemHandler(null, true);
        // Stamping die slot
        addItemHandlerSlots(inputHandler, 1, 0, inputSlotsX, 27, ItemSlotGridHelper.ROWS_1);
        // Input slot
        addItemHandlerSlots(inputHandler, 1, 1, inputSlotsX, 63, ItemSlotGridHelper.ROWS_1);
    }

    public ItemStack getStampingDie() {
        return getItems().get(TE_INVENTORY_FIRST_SLOT_INDEX);
    }
}
