package mwk.testmod.common.block.entity.modules;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

/**
 * A module encapsulating functionality for a machine block entity. Not all machines need the same
 * functionality, so instead of having a single monolithic block entity class, which can potentially
 * support all possible features, we can have a base block entity class that supports the common
 * features and then add modules to it to add additional functionality.
 */
public interface MachineModule {

    public void saveAdditional(CompoundTag tag, Provider registries);

    public void loadAdditional(CompoundTag tag, Provider registries);
}
