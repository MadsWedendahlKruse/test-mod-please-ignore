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
        return 2;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> blockEntity.getProgress();
            case 1 -> blockEntity.getMaxProgress();
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> menu.setProgress(value);
            case 1 -> menu.setMaxProgress(value);
        }
    }
}
