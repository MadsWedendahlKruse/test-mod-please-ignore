package mwk.testmod.common.block.inventory;

import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class CrusherMenu extends CrafterMachineMenu {

	public CrusherMenu(int containerId, Player player, BlockPos pos) {
		super(TestModMenus.CRUSHER_MENU.get(), containerId, player, pos, 14,
				DEFAULT_PLAYER_INVENTORY_Y, 31, 27, 121, 27);
	}

}
