package mwk.testmod.common.util.inventory.container_data;

import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.world.inventory.ContainerData;

public class MachineIOContainerData implements ContainerData {

    private final AutoIOModule autoIOModule;
    private final MachineMenu menu;

    public MachineIOContainerData(AutoIOModule autoIOModule, MachineMenu menu) {
        this.autoIOModule = autoIOModule;
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
            case 0 -> autoIOModule.isAutoPush() ? 1 : 0;
            case 1 -> autoIOModule.isAutoPull() ? 1 : 0;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        boolean newValue = value > 0;
        switch (index) {
            case 0 -> menu.setAutoPush(newValue);
            case 1 -> menu.setAutoPull(newValue);
        }
    }

}
