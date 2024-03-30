package mwk.testmod.common.block.inventory;

import mwk.testmod.client.gui.screen.base.BaseMachineScreen;
import mwk.testmod.client.gui.screen.base.ParallelCrafterMachineScreen;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import mwk.testmod.init.registries.TestModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class InductionFurnaceMenu extends CrafterMachineMenu {

	public InductionFurnaceMenu(int containerId, Player player, BlockPos pos) {
		super(TestModMenus.INDUCTION_FURNACE_MENU.get(), containerId, player, pos,
				(ParallelCrafterMachineScreen.PRESET_3X3_PARALLEL.imageWidth
						- BaseMachineScreen.TEXTURE_INVENTORY_WIDTH) / 2
						+ DEFAULT_PLAYER_INVENTORY_X,
				ParallelCrafterMachineScreen.PRESET_3X3_PARALLEL.imageHeight
						- BaseMachineScreen.TEXTURE_INVENTORY_HEIGHT
						+ BaseMachineScreen.INVETORY_PADDING_Y);
	}
}
