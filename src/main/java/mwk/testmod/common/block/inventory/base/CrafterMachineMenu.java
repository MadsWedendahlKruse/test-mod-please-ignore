package mwk.testmod.common.block.inventory.base;

import mwk.testmod.common.block.entity.base.CrafterMachineBlockEntity;
import mwk.testmod.common.util.inventory.CrafterContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class CrafterMachineMenu extends BaseMachineMenu {

    private int progress;
    private int maxProgress;

    protected CrafterMachineMenu(MenuType<?> menuType, int containerId, Player player, BlockPos pos,
            int playerInventoryX, int playerInventoryY) {
        super(menuType, containerId, player, pos, playerInventoryX, playerInventoryY);
        if (player.level().getBlockEntity(pos) instanceof CrafterMachineBlockEntity blockEntity) {
            this.progress = blockEntity.getProgress();
            this.maxProgress = blockEntity.getMaxProgress();
            addDataSlots(new CrafterContainerData(blockEntity, this));
        } else {
            // TODO: Not sure what to do here
            throw new IllegalArgumentException(
                    "Block entity is not an instance of CrafterMachineBlockEntity");
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }
}
