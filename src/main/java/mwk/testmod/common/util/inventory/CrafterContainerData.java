package mwk.testmod.common.util.inventory;

import mwk.testmod.common.block.entity.base.CrafterMachineBlockEntity;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.world.inventory.ContainerData;

public class CrafterContainerData implements ContainerData {

    private final CrafterMachineBlockEntity<?> blockEntity;
    private final CrafterMachineMenu menu;

    public CrafterContainerData(CrafterMachineBlockEntity<?> blockEntity, CrafterMachineMenu menu) {
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
