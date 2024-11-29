package mwk.testmod.common.util.handlers;

import mwk.testmod.common.block.interfaces.IUpgradable;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class UpgradeItemHandler extends ItemHandlerWrapper {

    private final IUpgradable upgradable;

    public UpgradeItemHandler(ItemStackHandler itemHandler, int startSlot, int slots,
            IUpgradable upgradable) {
        super(itemHandler, startSlot, slots);
        this.upgradable = upgradable;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (stack.getItem() instanceof UpgradeItem upgrade) {
            return upgradable.isUpgradeValid(upgrade);
        }
        return false;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        super.setStackInSlot(slot, stack);
        applyUpgrades();
    }

    public void applyUpgrades() {
        upgradable.resetUpgrades();
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).getItem() instanceof UpgradeItem upgrade) {
                upgradable.installUpgrade(upgrade);
            }
        }
    }
}
