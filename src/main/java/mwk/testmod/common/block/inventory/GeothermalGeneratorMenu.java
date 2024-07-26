package mwk.testmod.common.block.inventory;

import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class GeothermalGeneratorMenu extends ProcessingMenu {

    public GeothermalGeneratorMenu(int containerId, Player player, BlockPos pos) {
        super(TestModMenus.GEOTHERMAL_GENERATOR_MENU.get(), containerId, player, pos,
                DEFAULT_PLAYER_INVENTORY_X, DEFAULT_PLAYER_INVENTORY_Y, 0, 0, 0, 0);
    }

}
