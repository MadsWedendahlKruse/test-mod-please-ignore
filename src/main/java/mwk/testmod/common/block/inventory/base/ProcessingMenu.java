package mwk.testmod.common.block.inventory.base;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.util.inventory.container_data.ProcessingContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ProcessingMenu extends MachineMenu {

    private int progress;
    private int maxProgress;
    private int energyPerTick;
    public final int maxProgressBase;
    public final int energyPerTickBase;

    protected ProcessingMenu(MenuType<?> menuType, int containerId, Player player, BlockPos pos,
            int playerInventoryX, int playerInventoryY, int inputSlotsX, int inputSlotsY,
            int outputSlotsX, int outputSlotsY) {
        super(menuType, containerId, player, pos, playerInventoryX, playerInventoryY, inputSlotsX,
                inputSlotsY, outputSlotsX, outputSlotsY);
        if (player.level().getBlockEntity(pos) instanceof ProcessingBlockEntity<?, ?> blockEntity) {
            this.progress = blockEntity.getProgress();
            this.maxProgress = blockEntity.getMaxProgress();
            this.maxProgressBase = blockEntity.maxProgressBase;
            this.energyPerTick = blockEntity.getEnergyPerTick();
            this.energyPerTickBase = blockEntity.energyPerTickBase;
            addDataSlots(new ProcessingContainerData(blockEntity, this));
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

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
    }
}
