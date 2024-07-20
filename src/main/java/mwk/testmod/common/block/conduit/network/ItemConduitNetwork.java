package mwk.testmod.common.block.conduit.network;

import org.jetbrains.annotations.NotNull;
import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * A conduit network for transferring items. ItemHandler works differently from FluidHandler and
 * EnergyStorage, where they would return the received payload, the ItemHandler will return the
 * remaining payload, so that's why some of the methods are implemented differently.
 */
public class ItemConduitNetwork extends ConduitNetwork<IItemHandler, ItemStack> {

    public ItemConduitNetwork() {
        super(ConduitType.ITEM);
    }

    @Override
    protected @NotNull ItemStack createEmptyPayload(ItemStack payload) {
        return payload.copy();
    }

    @Override
    protected boolean isPayloadEmpty(ItemStack payload) {
        return payload.isEmpty();
    }

    @Override
    protected ItemStack aggregatePayloads(ItemStack aggregate, ItemStack receivedPayload) {
        return receivedPayload;
    }

    @Override
    protected ItemStack getRemainingPayload(ItemStack payload, ItemStack receivedPayload) {
        return receivedPayload;
    }

    @Override
    protected ItemStack transferPayload(@NotNull IItemHandler receiver, ItemStack payload,
            boolean simulate) {
        // I guess we have to attempt to insert into all slots?
        ItemStack remaining = payload.copy();
        for (int i = 0; i < receiver.getSlots(); i++) {
            remaining = receiver.insertItem(i, payload, simulate);
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return remaining;
    }

}
