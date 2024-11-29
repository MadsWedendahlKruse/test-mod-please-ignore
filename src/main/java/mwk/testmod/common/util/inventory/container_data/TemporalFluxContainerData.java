package mwk.testmod.common.util.inventory.container_data;

import mwk.testmod.common.block.entity.modules.TemporalFluxModule;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.world.inventory.ContainerData;

/**
 * A container data implementation for energy storage. DataSlots are limited to 16 bits, so we need
 * to split the energy storage into two parts.
 */
public class TemporalFluxContainerData implements ContainerData {

    private static final int FIRST_16_BITS = 0xffff;
    private static final int LAST_16_BITS = 0xffff0000;

    private final TemporalFluxModule temporalFluxModule;
    private final MachineMenu menu;

    public TemporalFluxContainerData(TemporalFluxModule temporalFluxModule, MachineMenu menu) {
        this.temporalFluxModule = temporalFluxModule;
        this.menu = menu;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> temporalFluxModule.getTemporalFluxStored() & FIRST_16_BITS;
            case 1 -> (temporalFluxModule.getTemporalFluxStored() >> 16) & FIRST_16_BITS;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> menu.setTemporalFlux(
                    (menu.getTemporalFlux() & LAST_16_BITS) | (value & FIRST_16_BITS));
            case 1 -> menu.setTemporalFlux(
                    (menu.getTemporalFlux() & FIRST_16_BITS) | ((value & FIRST_16_BITS) << 16));
        }
    }
}
