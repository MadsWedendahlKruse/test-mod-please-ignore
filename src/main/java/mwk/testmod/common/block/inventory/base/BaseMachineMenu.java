package mwk.testmod.common.block.inventory.base;

import mwk.testmod.common.block.entity.base.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BaseMachineMenu extends EnergyMenu {

    public static final int SLOT_SIZE = 16;
    public static final int SLOT_SPACING = 2;
    public static final int DEFAULT_PLAYER_INVENTORY_X = 8;
    public static final int DEFAULT_PLAYER_INVENTORY_Y = 91;

    private final BlockPos pos;
    private final Block block;
    private final int playerInventoryX;
    private final int playerInventoryY;
    private final int inputSlots;
    private final int outputSlots;
    private final int inventorySize;
    private final int maxSlotIndex;
    private final int inputSlotCenterX;
    private final int inputSlotCenterY;
    private final int outputSlotCenterX;
    private final int outputSlotCenterY;

    protected BaseMachineMenu(MenuType<?> menuType, int containerId, Player player, BlockPos pos,
            int playerInventoryX, int playerInventoryY, int inputSlotCenterX, int inputSlotCenterY,
            int outputSlotCenterX, int outputSlotCenterY) {
        super(menuType, containerId, player, pos);
        this.pos = pos;
        this.block = player.level().getBlockState(pos).getBlock();
        if (player.level().getBlockEntity(pos) instanceof BaseMachineBlockEntity blockEntity) {
            this.inputSlots = blockEntity.getInputSlots();
            this.outputSlots = blockEntity.getOutputSlots();
            this.inventorySize = inputSlots + outputSlots;
            this.inputSlotCenterX = inputSlotCenterX;
            this.inputSlotCenterY = inputSlotCenterY;
            this.outputSlotCenterX = outputSlotCenterX;
            this.outputSlotCenterY = outputSlotCenterY;
            this.maxSlotIndex = Inventory.INVENTORY_SIZE + this.inventorySize;
            addInputOutputSlots(blockEntity.getItemHandler(null));
            this.playerInventoryX = playerInventoryX;
            this.playerInventoryY = playerInventoryY;
            addPlayerSlots(player.getInventory());
        } else {
            // TODO: Not sure what to do here
            throw new IllegalArgumentException(
                    "Block entity is not an instance of BaseMachineBlockEntity");
        }
    }

    protected final void addItemHandlerSlots(IItemHandler itemHandler, int startIndex, int slots,
            int centerX, int centerY, int dx, int dy, int rows, int columns) {
        int startX = centerX - (dx * (columns - 1)) / 2;
        int startY = centerY - (dy * (rows - 1)) / 2;
        int x = startX;
        int y = startY;
        int index = startIndex;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                addSlot(new SlotItemHandler(itemHandler, index, x, y));
                index++;
                if (index - startIndex >= slots) {
                    return;
                }
                y += dy;
            }
            x += dx;
            y = startY;
        }
    }

    protected void addInputOutputSlots(IItemHandler itemHandler) {
        int dxy = SLOT_SIZE + SLOT_SPACING;
        // Input slots
        int inputColumns = (int) Math.ceil((double) inputSlots / 3);
        int inputRows = (int) Math.ceil((double) inputSlots / inputColumns);
        addItemHandlerSlots(itemHandler, 0, inputSlots, inputSlotCenterX, inputSlotCenterY, dxy,
                dxy, inputRows, inputColumns);
        // Output slots
        int outputColumns = (int) Math.ceil((double) outputSlots / 3);
        int outputRows = (int) Math.ceil((double) outputSlots / outputColumns);
        addItemHandlerSlots(itemHandler, inputSlots, outputSlots, outputSlotCenterX,
                outputSlotCenterY, dxy, dxy, outputRows, outputColumns);
    }

    private int addSlotRange(Container playerInventory, int index, int x, int y, int slots,
            int dx) {
        for (int i = 0; i < slots; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container playerInventory, int index, int x, int y, int columns, int dx,
            int rows, int dy) {
        for (int j = 0; j < rows; j++) {
            index = addSlotRange(playerInventory, index, x, y, columns, dx);
            y += dy;
        }
        return index;
    }

    private void addPlayerSlots(Container playerInventory) {
        // Hotbar
        int index = 0;
        index = addSlotRange(playerInventory, index, playerInventoryX, playerInventoryY + 58, 9,
                18);
        // Player inventory
        addSlotBox(playerInventory, index, playerInventoryX, playerInventoryY, 9, 18, 3, 18);
    }

    public int getPlayerInventoryX() {
        return playerInventoryX;
    }

    public int getPlayerInventoryY() {
        return playerInventoryY;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < this.inventorySize) {
                if (!this.moveItemStackTo(stack, this.inventorySize, this.maxSlotIndex, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (!this.moveItemStackTo(stack, 0, this.inventorySize - 1, false)) {
                if (index < 27 + this.inventorySize) {
                    if (!this.moveItemStackTo(stack, 27 + this.inventorySize, this.maxSlotIndex,
                            false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < this.maxSlotIndex && !this.moveItemStackTo(stack,
                        this.inventorySize, 27 + this.inventorySize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, block);
    }

}
