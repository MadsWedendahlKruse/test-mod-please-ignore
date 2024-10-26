package mwk.testmod.common.util.inventory.container_data;

import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.inventory.base.EnergyMenu;
import net.minecraft.world.inventory.ContainerData;

/**
 * A container data implementation for energy storage. DataSlots are limited to 16 bits, so we need
 * to split the energy storage into two parts.
 */
public class EnergyContainerData implements ContainerData {

    private static final int FIRST_16_BITS = 0xffff;
    private static final int LAST_16_BITS = 0xffff0000;

    private final EnergyModule energyModule;
    private final EnergyMenu menu;

    public EnergyContainerData(EnergyModule energyModule, EnergyMenu menu) {
        this.energyModule = energyModule;
        this.menu = menu;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> energyModule.getEnergyStored() & FIRST_16_BITS;
            case 1 -> (energyModule.getEnergyStored() >> 16) & FIRST_16_BITS;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> menu.setEnergy((menu.getEnergy() & LAST_16_BITS) | (value & FIRST_16_BITS));
            case 1 -> menu.setEnergy(
                    (menu.getEnergy() & FIRST_16_BITS) | ((value & FIRST_16_BITS) << 16));
        }
    }
}
