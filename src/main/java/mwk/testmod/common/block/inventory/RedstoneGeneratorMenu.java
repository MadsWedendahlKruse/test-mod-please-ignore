package mwk.testmod.common.block.inventory;

import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class RedstoneGeneratorMenu extends ProcessingMenu {

    public RedstoneGeneratorMenu(int containerId, Player player, BlockPos pos) {
        super(TestModMenus.REDSTONE_GENERATOR_MENU.get(), containerId, player, pos,
                DEFAULT_PLAYER_INVENTORY_X, DEFAULT_PLAYER_INVENTORY_Y, 44, 34, 0, 0);
    }
}
