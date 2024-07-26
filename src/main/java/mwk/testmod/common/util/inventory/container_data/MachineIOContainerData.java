package mwk.testmod.common.util.inventory.container_data;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.world.inventory.ContainerData;

public class MachineIOContainerData implements ContainerData {

    private final MachineBlockEntity blockEntity;
    private final MachineMenu menu;

    public MachineIOContainerData(MachineBlockEntity blockEntity, MachineMenu menu) {
        this.blockEntity = blockEntity;
        this.menu = menu;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int get(int index) {
        // TODO: This could be encoded in a single integer
        return switch (index) {
            case 0 -> blockEntity.isAutoEject() ? 1 : 0;
            case 1 -> blockEntity.isAutoInsert() ? 1 : 0;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        boolean newValue = value > 0;
        switch (index) {
            case 0 -> menu.setAutoEject(newValue);
            case 1 -> menu.setAutoInsert(newValue);
        }
    }

}
