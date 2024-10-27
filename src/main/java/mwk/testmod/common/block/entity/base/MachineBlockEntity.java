package mwk.testmod.common.block.entity.base;

import java.util.Optional;
import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.entity.modules.FluidTankModule;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.block.entity.modules.MachineModule;
import mwk.testmod.common.block.entity.modules.SoundModule;
import mwk.testmod.common.block.interfaces.IDescribable;
import mwk.testmod.common.block.interfaces.IUpgradable;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * A block entity that stores energy and also has an inventory.
 */
public abstract class MachineBlockEntity extends BlockEntity
        implements MenuProvider, IUpgradable, IDescribable {

    private Optional<EnergyModule> energy;
    private Optional<InventoryModule> inventory;
    private Optional<FluidTankModule> fluidTanks;
    private Optional<AutoIOModule> autoIO;
    private Optional<SoundModule> sound;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energy = Optional.empty();
        inventory = Optional.empty();
        fluidTanks = Optional.empty();
        autoIO = Optional.empty();
        sound = Optional.empty();
    }

    protected void addModule(MachineModule module) {
        if (module instanceof EnergyModule) {
            energy = Optional.of((EnergyModule) module);
        } else if (module instanceof InventoryModule) {
            inventory = Optional.of((InventoryModule) module);
        } else if (module instanceof FluidTankModule) {
            fluidTanks = Optional.of((FluidTankModule) module);
        } else if (module instanceof AutoIOModule) {
            autoIO = Optional.of((AutoIOModule) module);
        } else if (module instanceof SoundModule) {
            sound = Optional.of((SoundModule) module);
        }
    }

    protected void addModules(MachineModule... modules) {
        for (MachineModule module : modules) {
            addModule(module);
        }
    }


    public boolean isFormed() {
        // We're going all in on multiblocks
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof MultiBlockControllerBlock) {
                return state.getValue(MultiBlockControllerBlock.FORMED);
            }
        }
        return false;
    }

    public void setWorking(boolean working) {
        // TODO: Right now this only works if the block entity is attached to a
        // multiblock
        // controller. This should be changed to work with any block entity?
        if (level != null && getBlockState().getBlock() instanceof MultiBlockControllerBlock) {
            level.setBlockAndUpdate(worldPosition,
                    getBlockState().setValue(MultiBlockControllerBlock.WORKING, working));
        }
        if (!working) {
            sound().ifPresent(soundModule -> soundModule.setSoundStart(0));
        }
    }

    public boolean isWorking() {
        // TODO: Same as for setWorking
        if (getBlockState().getBlock() instanceof MultiBlockControllerBlock) {
            return getBlockState().getValue(MultiBlockControllerBlock.WORKING);
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        energy.ifPresent(energyModule -> energyModule.saveAdditional(tag, registries));
        inventory.ifPresent(inventoryModule -> inventoryModule.saveAdditional(tag, registries));
        fluidTanks.ifPresent(fluidTankModule -> fluidTankModule.saveAdditional(tag, registries));
        autoIO.ifPresent(autoIOModule -> autoIOModule.saveAdditional(tag, registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        energy.ifPresent(energyModule -> energyModule.loadAdditional(tag, registries));
        inventory.ifPresent(inventoryModule -> inventoryModule.loadAdditional(tag, registries));
        fluidTanks.ifPresent(fluidTankModule -> fluidTankModule.loadAdditional(tag, registries));
        autoIO.ifPresent(autoIOModule -> autoIOModule.loadAdditional(tag, registries));
        if (inventory.isPresent()) {
            InventoryModule inventory = inventory().get();
            inventory.getUpgradeItemHandler(null).applyUpgrades();
        }
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        fluidTanks.ifPresent(fluidTankModule -> fluidTankModule.getUpdateTag(tag, registries));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            Provider registries) {
        super.onDataPacket(net, pkt, registries);
        CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }
        fluidTanks.ifPresent(fluidTankModule -> fluidTankModule.onDataPacket(net, pkt, registries));
    }

    public Optional<EnergyModule> energy() {
        return energy;
    }

    /**
     * Called when the contents of the inventory change. By default, this method does nothing, but
     * it can be overridden to provide custom behavior.
     *
     * @param slot the slot that changed
     */
    protected void onInventoryChanged(int slot) {
    }

    /**
     * Checks if the given stack can be inserted into the given slot. This is used to check if the
     * stack can be inserted into the input slots. By default, this method returns true, but it can
     * be overridden to provide custom behavior.
     *
     * @param slot  the slot
     * @param stack the stack
     * @return true if the stack can be inserted, false otherwise
     */
    protected boolean isInputItemValid(int slot, ItemStack stack) {
        return true;
    }

    public Optional<InventoryModule> inventory() {
        return inventory;
    }

    /**
     * Checks if the given stack can be inserted into the given tank. This is used to check if the
     * stack can be inserted into the input tanks. By default, this method returns true, but it can
     * be overridden to provide custom behavior.
     *
     * @param tank  the tank
     * @param stack the stack
     * @return true if the stack can be inserted, false otherwise
     */
    protected boolean isInputFluidValid(int tank, FluidStack stack) {
        return true;
    }

    public Optional<FluidTankModule> fluidTanks() {
        return fluidTanks;
    }

    /**
     * Get the inventory of the block entity. This is used to drop the inventory when the block is
     * broken. TODO: Not sure if this is the best way to do this.
     */
    public Container getDrops() {
        return inventory.map(InventoryModule::getDrops).orElse(new SimpleContainer(0));
    }

    /**
     * Apply the upgrades to the block entity. This should be called whenever the upgrades are
     * changed.
     */
//    public final void applyUpgrades() {
//        if (level != null && level.isClientSide()) {
//            return;
//        }
//        if (inventory.isPresent()) {
//            resetUpgrades();
//            for (UpgradeItem upgrade : inventory.get().getUpgrades()) {
//                installUpgrade(upgrade);
//            }
//        }
//    }
    public Optional<AutoIOModule> autoIO() {
        return autoIO;
    }

    public Optional<SoundModule> sound() {
        return sound;
    }
}
