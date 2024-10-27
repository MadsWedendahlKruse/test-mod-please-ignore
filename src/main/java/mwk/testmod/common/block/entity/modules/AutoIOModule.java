package mwk.testmod.common.block.entity.modules;

import mwk.testmod.TestModConfig;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public class AutoIOModule implements MachineModule {

    public static final String NBT_TAG_AUTO_PULL = "autoPull";
    public static final String NBT_TAG_AUTO_PUSH = "autoPush";

    public static final int ITEM_IO_SPEED = TestModConfig.MACHINE_ITEM_IO_SPEED_DEFAULT.get(); // [items/tick]
    public static final int FLUID_IO_SPEED = TestModConfig.MACHINE_FLUID_IO_SPEED_DEFAULT.get(); // [mB/tick]

    private boolean autoPull;
    private boolean autoPush;

    public AutoIOModule(boolean autoPull, boolean autoPush) {
        this.autoPull = autoPull;
        this.autoPush = autoPush;
    }

    public AutoIOModule() {
        // TODO: Default false instead?
        this(true, true);
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.putBoolean(NBT_TAG_AUTO_PULL, autoPull);
        tag.putBoolean(NBT_TAG_AUTO_PUSH, autoPush);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        autoPull = tag.getBoolean(NBT_TAG_AUTO_PULL);
        autoPush = tag.getBoolean(NBT_TAG_AUTO_PUSH);
    }

    public boolean isAutoPush() {
        return autoPush;
    }

    public void setAutoPush(boolean autoPush) {
        this.autoPush = autoPush;
    }

    public boolean isAutoPull() {
        return autoPull;
    }

    public void setAutoPull(boolean autoPull) {
        this.autoPull = autoPull;
    }

    public int getItemIoSpeed() {
        return ITEM_IO_SPEED;
    }

    public int getFluidIoSpeed() {
        return FLUID_IO_SPEED;
    }
}
