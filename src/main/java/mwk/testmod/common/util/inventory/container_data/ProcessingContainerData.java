package mwk.testmod.common.util.inventory.container_data;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.world.inventory.ContainerData;

public class ProcessingContainerData implements ContainerData {

    private final ProcessingBlockEntity<?> blockEntity;
    private final ProcessingMenu menu;

    public ProcessingContainerData(ProcessingBlockEntity<?> blockEntity, ProcessingMenu menu) {
        this.blockEntity = blockEntity;
        this.menu = menu;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> blockEntity.getProgress();
            case 1 -> blockEntity.getMaxProgress();
            // TODO: EnergyPerTick is currently technically limited to a short, but we should
            // probably change it to an int. On the other hand that's some serious power creep
            // if it can use more than 32k RF/tick
            case 2 -> blockEntity.getEnergyPerTick();
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> menu.setProgress(value);
            case 1 -> menu.setMaxProgress(value);
            case 2 -> menu.setEnergyPerTick(value);
        }
    }
}
