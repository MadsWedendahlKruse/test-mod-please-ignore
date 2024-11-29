package mwk.testmod.common.block.inventory;

import mwk.testmod.client.utils.ItemSlotGridHelper;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.common.util.handlers.InputItemHandler;
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
    protected void addInputSlots(InventoryModule inventoryModule, int inputSlotX, int inputSlotY) {
        if (blockEntity.inventory().isEmpty()) {
            return;
        }
        InputItemHandler inputHandler = blockEntity.inventory().get()
                .getInputItemHandler(null, true);
        // Stamping die slot
        addItemHandlerSlots(inputHandler, 1, 0, inputSlotX, 27, ItemSlotGridHelper.ROWS_1);
        // Input slot
        addItemHandlerSlots(inputHandler, 1, 1, inputSlotX, 63, ItemSlotGridHelper.ROWS_1);
    }

    public ItemStack getStampingDie() {
        return getItems().get(TE_INVENTORY_FIRST_SLOT_INDEX);
    }
}
