package mwk.testmod.common.block.inventory.base;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BaseMachineMenu extends EnergyMenu {

    public static final int SLOT_SIZE = 16;
    public static final int SLOT_SPACING = 2;
    public static final int SLOT_DX = SLOT_SIZE + SLOT_SPACING;
    public static final int SLOT_DY = SLOT_SIZE + SLOT_SPACING;
    public static final int DEFAULT_PLAYER_INVENTORY_X = 8;
    public static final int DEFAULT_PLAYER_INVENTORY_Y = 91;

    private final BlockPos pos;
    private final Block block;
    private final BaseMachineBlockEntity blockEntity;

    public final int inputSlots;
    public final int outputSlots;
    public final int upgradeSlots;
    public final int machineInventorySize;
    private boolean upgradesVisible = true;

    private final int playerInventoryX;
    private final int playerInventoryY;
    private final int inputSlotsX;
    private final int inputSlotsY;
    private final int outputSlotsX;
    private final int outputSlotsY;
    private int upgradeSlotsX;
    private int upgradeSlotsY;

    protected BaseMachineMenu(MenuType<?> menuType, int containerId, Player player, BlockPos pos,
            int playerInventoryX, int playerInventoryY, int inputSlotsX, int inputSlotsY,
            int outputSlotsX, int outputSlotsY) {
        super(menuType, containerId, player, pos);
        this.pos = pos;
        this.block = player.level().getBlockState(pos).getBlock();
        BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (blockEntity instanceof BaseMachineBlockEntity machineBlockEntity) {
            this.blockEntity = machineBlockEntity;
            this.playerInventoryX = playerInventoryX;
            this.playerInventoryY = playerInventoryY;
            addPlayerSlots(player.getInventory());
            this.inputSlots = machineBlockEntity.getInputSlots();
            this.outputSlots = machineBlockEntity.getOutputSlots();
            this.upgradeSlots = machineBlockEntity.getUpgradeSlots();
            this.machineInventorySize = machineBlockEntity.getInventorySize();
            this.inputSlotsX = inputSlotsX;
            this.inputSlotsY = inputSlotsY;
            this.outputSlotsX = outputSlotsX;
            this.outputSlotsY = outputSlotsY;
            addInputOutputSlots();
            addUpgradeSlots(0, 0);
        } else {
            // TODO: Not sure what to do here
            throw new IllegalArgumentException(
                    "Block entity is not an instance of BaseMachineBlockEntity");
        }
    }

    protected final void addItemHandlerSlots(IItemHandler itemHandler, int startIndex, int slots,
            int slotsX, int slotsY, int dx, int dy, int rows, int columns,
            SlotVisibilityCondition visibilityCondition) {
        int startX = slotsX;
        int startY = slotsY;
        int x = startX;
        int y = startY;
        int index = startIndex;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (visibilityCondition != null) {
                    addSlot(new SlotItemHandler(itemHandler, index, x, y) {
                        @Override
                        public boolean isActive() {
                            return visibilityCondition.isVisible();
                        }
                    });
                } else {
                    addSlot(new SlotItemHandler(itemHandler, index, x, y));
                }
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

    protected final void addItemHandlerSlots(IItemHandler itemHandler, int startIndex, int slots,
            int slotsX, int slotsY, int dx, int dy, int rows, int columns) {
        addItemHandlerSlots(itemHandler, startIndex, slots, slotsX, slotsY, dx, dy, rows, columns,
                null);
    }

    protected void addInputOutputSlots() {
        // Input slots
        int inputColumns = (int) Math.ceil((float) inputSlots / 3);
        int inputRows = (int) Math.ceil((float) inputSlots / inputColumns);
        addItemHandlerSlots(blockEntity.getInputHandler(null, true), 0, inputSlots, inputSlotsX,
                inputSlotsY, SLOT_DX, SLOT_DY, inputRows, inputColumns);
        // Output slots
        int outputColumns = (int) Math.ceil((float) outputSlots / 3);
        int outputRows = (int) Math.ceil((float) outputSlots / outputColumns);
        addItemHandlerSlots(blockEntity.getOutputHandler(null), inputSlots, outputSlots,
                outputSlotsX, outputSlotsY, SLOT_DX, SLOT_DY, outputRows, outputColumns);
    }

    protected void addUpgradeSlots(int upgradeX, int upgradeY) {
        // Upgrade slots
        int upgradeRows = (int) Math.ceil((float) upgradeSlots / 3);
        int upgradeColumns = (int) Math.ceil((float) upgradeSlots / upgradeRows);
        addItemHandlerSlots(blockEntity.getUpgradeHandler(null), inputSlots + outputSlots,
                upgradeSlots, upgradeX, upgradeY, SLOT_DX, SLOT_DY, upgradeRows, upgradeColumns,
                () -> upgradesVisible);
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

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player
    // inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which
    // means
    // 0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    // 9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    // 36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT =
            PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX =
            VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY; // EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + machineInventorySize, false)) {
                return ItemStack.EMPTY; // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + machineInventorySize) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, block);
    }

    /**
     * Set the visibility of the upgrade slots. If the slots are visible, they will be added to the
     * GUI at the specified position.
     * 
     * @param upgradesVisible Whether the upgrade slots should be visible
     * @param x The x position of the upgrade slots
     * @param y The y position of the upgrade slots
     */
    public void setUpgradesVisible(boolean upgradesVisible, int x, int y) {
        this.upgradesVisible = upgradesVisible;
        if (!upgradesVisible) {
            return;
        }
        if (this.upgradeSlotsX == x && this.upgradeSlotsY == y) {
            return;
        }
        // TODO: I'm really not a fan of this solution. The problem stems from the fact that
        // the menu is in charge of the position of the slots in the GUI, even though it would
        // make a lot more sense if that was controlled from the screen.

        // Remove old upgrade slots
        for (int i = 0; i < upgradeSlots; i++) {
            slots.remove(slots.size() - 1);
        }
        this.upgradeSlotsX = x;
        this.upgradeSlotsY = y;
        addUpgradeSlots(this.upgradeSlotsX, this.upgradeSlotsY);
    }

    @FunctionalInterface
    private interface SlotVisibilityCondition {
        boolean isVisible();
    }
}
