package mwk.testmod.common.block.inventory;

import mwk.testmod.client.gui.screen.CrusherScreen;
import mwk.testmod.client.gui.screen.base.BaseMachineScreen;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class CrusherMenu extends CrafterMachineMenu {

    public CrusherMenu(int containerId, Player player, BlockPos pos) {
        super(TestModMenus.CRUSHER_MENU.get(), containerId, player, pos,
                (CrusherScreen.TEXTURE_WIDTH - BaseMachineScreen.TEXTURE_INVENTORY_WIDTH) / 2
                        + DEFAULT_PLAYER_INVENTORY_X,
                CrusherScreen.TEXTURE_HEIGHT - BaseMachineScreen.TEXTURE_INVENTORY_HEIGHT
                        + BaseMachineScreen.INVETORY_PADDING_Y,
                49, 27, 139, 27);
    }

}
