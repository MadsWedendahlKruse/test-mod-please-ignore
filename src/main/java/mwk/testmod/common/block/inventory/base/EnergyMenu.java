package mwk.testmod.common.block.inventory.base;

import mwk.testmod.common.block.entity.base.EnergyBlockEntity;
import mwk.testmod.common.util.inventory.container_data.EnergyContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

/**
 * A base class for all menus where the corresponding block entity has an energy storage.
 */
public abstract class EnergyMenu extends AbstractContainerMenu {

    protected int energy;
    protected int maxEnergy;

    protected final BlockPos pos;
    protected final Block block;

    protected EnergyMenu(MenuType<?> menuType, int containerId, Player player, BlockPos pos) {
        super(menuType, containerId);
        this.pos = pos;
        this.block = player.level().getBlockState(pos).getBlock();
        if (player.level().getBlockEntity(pos) instanceof EnergyBlockEntity blockEntity) {
            this.energy = blockEntity.getEnergyStored();
            this.maxEnergy = blockEntity.getMaxEnergyStored();
            addDataSlots(new EnergyContainerData(blockEntity, this));
        } else {
            // TODO: Not sure what to do here
            throw new IllegalArgumentException(
                    "Block entity is not an instance of EnergyBlockEntity");
        }
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, block);
    }
}
