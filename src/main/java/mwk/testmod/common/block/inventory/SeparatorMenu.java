package mwk.testmod.common.block.inventory;

import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class SeparatorMenu extends ProcessingMenu {

    public SeparatorMenu(int containerId, Player player, BlockPos pos) {
        super(TestModMenus.SEPARATOR_MENU.get(), containerId, player, pos,
                DEFAULT_PLAYER_INVENTORY_X, DEFAULT_PLAYER_INVENTORY_Y, 44, 45, 116, 27);
    }
}
