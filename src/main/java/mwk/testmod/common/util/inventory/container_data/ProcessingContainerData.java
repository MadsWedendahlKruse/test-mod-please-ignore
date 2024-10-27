package mwk.testmod.common.util.inventory.container_data;

import mwk.testmod.common.block.entity.modules.ProcessingModule;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.world.inventory.ContainerData;

public class ProcessingContainerData implements ContainerData {

    private final ProcessingModule<?, ?> processingModule;
    private final ProcessingMenu menu;

    public ProcessingContainerData(ProcessingModule<?, ?> processingModule, ProcessingMenu menu) {
        this.processingModule = processingModule;
        this.menu = menu;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> processingModule.getProgress();
            case 1 -> processingModule.getMaxProgress();
            // TODO: EnergyPerTick is currently technically limited to a short, but we should
            // probably change it to an int. On the other hand that's some serious power creep
            // if it can use more than 32k RF/tick
            case 2 -> processingModule.getResourcePerTick();
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
