package mwk.testmod.common.item.misc;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StampingDieItem extends Item {

    public StampingDieItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
