package mwk.testmod.common.block.inventory;

import mwk.testmod.common.block.inventory.base.MachineMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class CapacitronMenu extends MachineMenu {

    public CapacitronMenu(int containerId, Player player, BlockPos pos) {
        super(TestModMenus.CAPACITRON_MENU.get(), containerId, player, pos,
                DEFAULT_PLAYER_INVENTORY_X, DEFAULT_PLAYER_INVENTORY_Y, 0, 0, 0, 0);
    }

}
