package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.base.FixedAnimation.Function;
import mwk.testmod.client.animations.base.KeyframeManager;
import mwk.testmod.common.block.entity.base.crafter.SingleCrafterBlockEntity;
import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.entity.modules.EnergyModule.EnergyType;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.block.entity.modules.ProcessingModule;
import mwk.testmod.common.block.entity.modules.SoundModule;
import mwk.testmod.common.block.inventory.StampingPressMenu;
import mwk.testmod.common.item.misc.StampingDieItem;
import mwk.testmod.common.recipe.StampingRecipe;
import mwk.testmod.common.recipe.inputs.CatalystRecipeInput;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

public class StampingPressBlockEntity extends
        SingleCrafterBlockEntity<CatalystRecipeInput, StampingRecipe> {

    // NBT keys for syncing recipe to the client for rendering
    public static final String NBT_TAG_DIE = "die";
    public static final String NBT_TAG_INPUT = "input";
    public static final String NBT_TAG_OUTPUT = "output";

    // TODO: Config value
    public static final int DEFAULT_MAX_PROGRESS = 60;

    // TODO: Can we put all this animation stuff in a separate class?
    public static final int PISTON_ANIMATION_INDEX = 0;
    public static final int CONVEYOR_ANIMATION_INDEX = 1;

    public static final float PISTON_DOWN_OFFSET = -11F / 16F;
    public static final float PISTON_UP_OFFSET = 0.0F;
    public static final float PISTON_DOWN_DURATION = 0.15F;
    public static final float PISTON_HOLD_DURATION = 0.5F;
    public static final float PISTON_UP_DURATION = 0.75F;

    public static final float CONVEYOR_CENTER_OFFSET = 1.5F;

    private final KeyframeManager stampingAnimation;

    public StampingPressBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.STAMPING_PRESS_ENTITY_TYPE.get(), pos, state,
                TestModRecipeTypes.STAMPING.get(), DEFAULT_MAX_PROGRESS, 128);
        addModule(new EnergyModule(this, TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(),
                EnergyType.CONSUMER));
        addModule(new InventoryModule(this, 2, 1, 6, this::onInventoryChanged,
                this::isInputItemValid));
        addModule(new AutoIOModule());
        addModule(new SoundModule(this, TestModSounds.STAMPING_PRESS.get(),
                TestModSounds.STAMPING_PRESS_DURATION));
        // Two initial values, one for the animation for the piston, and one for the item being stamped
        stampingAnimation = new KeyframeManager(0, -CONVEYOR_CENTER_OFFSET);
        final float duration = DEFAULT_MAX_PROGRESS / 20.0F;
        // Item being stamped
        // We can the use the center as a reference point for when to swap the rendered item from
        // the input to the output. We therefore stop the item before it gets stamped just shy of
        // the center.
        stampingAnimation.addKeyframe(CONVEYOR_ANIMATION_INDEX, duration / 3, -0.001F);
        // As the piston goes down, the item moves a very tiny distance forward, so it reaches the
        // center of the conveyor. We can then swap the item from the input to the output
        stampingAnimation.addKeyframe(CONVEYOR_ANIMATION_INDEX, PISTON_DOWN_DURATION, 0);
        // Remain at the center until 2/3 of the way through the animation
        stampingAnimation.addKeyframe(CONVEYOR_ANIMATION_INDEX, duration / 3 - PISTON_DOWN_DURATION,
                0);
        // Finally, move the item to the output
        stampingAnimation.addKeyframe(CONVEYOR_ANIMATION_INDEX, duration / 3,
                CONVEYOR_CENTER_OFFSET);
        // Piston remains up until the item is moved to the center
        stampingAnimation.addKeyframe(PISTON_ANIMATION_INDEX, duration / 3, 0);
        stampingAnimation.addKeyframe(PISTON_ANIMATION_INDEX, PISTON_DOWN_DURATION,
                PISTON_DOWN_OFFSET, Function.EASE_IN_CUBIC);
        stampingAnimation.addKeyframe(PISTON_ANIMATION_INDEX, PISTON_HOLD_DURATION,
                PISTON_DOWN_OFFSET);
        stampingAnimation.addKeyframe(PISTON_ANIMATION_INDEX, PISTON_UP_DURATION, PISTON_UP_OFFSET,
                Function.EASE_OUT_CUBIC);

        stampingAnimation.start();
    }

    @Override
    protected boolean isInputItemValid(int slot, ItemStack stack) {
        boolean stampingDie = stack.getItem() instanceof StampingDieItem;
        // Stamping die is only valid in slot 0
        return super.isInputItemValid(slot, stack) && switch (slot) {
            case 0 -> stampingDie;
            case 1 -> !stampingDie;
            default -> false;
        };
    }

    @Override
    protected CatalystRecipeInput getRecipeInput() {
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            return new CatalystRecipeInput(inventory.getStackInSlot(0),
                    inventory.getStackInSlot(1));
        }
        return new CatalystRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    protected boolean isSameInput(CatalystRecipeInput input1, CatalystRecipeInput input2) {
        for (int i = 0; i < input1.size(); i++) {
            if (!ItemStack.matches(input1.getItem(i), input2.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean canProcessRecipe(StampingRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            ItemStack output = recipe.getOutputItem();
            int inputSlots = inventory.getInputSlots();
            return inventory.canInsertItemIntoSlot(inputSlots, output.getItem(), output.getCount());
        }
        return false;
    }

    @Override
    protected void processRecipe(StampingRecipe recipe) {
        if (inventory().isEmpty()) {
            return;
        }
        InventoryModule inventory = inventory().get();
        int inputSlots = inventory.getInputSlots();
        ItemStack output = recipe.getOutputItem();
        // Slot 0 is the stamping die (which isn't consumed)
        inventory.extractItem(1, 1, false);
        inventory.setStackInSlot(inputSlots, new ItemStack(output.getItem(),
                inventory.getStackInSlot(inputSlots).getCount() + output.getCount()));
    }

    @Override
    protected void onInventoryChanged(int slot) {
        super.onInventoryChanged(slot);
        if (inventory().isEmpty()) {
            return;
        }
        InventoryModule inventory = inventory().get();
        // Reset animation if the inputs change
        if (slot < inventory.getInputSlots() && inventory.getStackInSlot(slot).isEmpty()) {
            stampingAnimation.start();
        }
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        // Note to self: This is used both on LevelChunk load and Block Update
        CompoundTag tag = new CompoundTag();
        ItemStack stampingDie = getStampingDie();
        if (!stampingDie.isEmpty()) {
            tag.put(NBT_TAG_DIE, stampingDie.save(registries, new CompoundTag()));
        }
        ItemStack input = getInput();
        if (!input.isEmpty()) {
            tag.put(NBT_TAG_INPUT, input.save(registries, new CompoundTag()));
        }
        if (processing().isPresent()) {
            ProcessingModule<CatalystRecipeInput, StampingRecipe> processing = processing().get();
            StampingRecipe recipe = processing.getLastRecipe();
            if (recipe != null) {
                tag.put(NBT_TAG_OUTPUT,
                        recipe.getOutputItem().save(registries, new CompoundTag()));
            }
        }
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            Provider registries) {
        // Note to self: This is used on Block Update
        super.onDataPacket(net, pkt, registries);
        CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }
        if (tag.contains(NBT_TAG_OUTPUT) && processing().isPresent()) {
            ProcessingModule<CatalystRecipeInput, StampingRecipe> processing = processing().get();
            processing.setLastRecipe(new StampingRecipe(
                    Ingredient.of(ItemStack.parse(registries, tag.getCompound(NBT_TAG_DIE)).get()),
                    Ingredient.of(
                            ItemStack.parse(registries, tag.getCompound(NBT_TAG_INPUT)).get()),
                    ItemStack.parse(registries, tag.getCompound(NBT_TAG_OUTPUT)).get()));
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, Provider registries) {
        // Note to self: This is used on LevelChunk load
        super.handleUpdateTag(tag, registries);
        if (inventory().isEmpty()) {
            return;
        }
        InventoryModule inventory = inventory().get();
        if (tag.contains(NBT_TAG_DIE)) {
            inventory.setStackInSlot(0,
                    ItemStack.parse(registries, tag.getCompound(NBT_TAG_DIE)).get());
        }
        if (tag.contains(NBT_TAG_INPUT)) {
            inventory.setStackInSlot(1,
                    ItemStack.parse(registries, tag.getCompound(NBT_TAG_INPUT)).get());
        }
        if (tag.contains(NBT_TAG_OUTPUT) && processing().isPresent()) {
            ProcessingModule<CatalystRecipeInput, StampingRecipe> processing = processing().get();
            processing.setLastRecipe(new StampingRecipe(
                    Ingredient.of(ItemStack.parse(registries, tag.getCompound(NBT_TAG_DIE)).get()),
                    Ingredient.of(
                            ItemStack.parse(registries, tag.getCompound(NBT_TAG_INPUT)).get()),
                    ItemStack.parse(registries, tag.getCompound(NBT_TAG_OUTPUT)).get()));
        }
    }

    public ItemStack getStampingDie() {
        return inventory().map(inventory -> inventory.getStackInSlot(0)).orElse(ItemStack.EMPTY);
    }

    public ItemStack getInput() {
        if (processing().isPresent()) {
            ProcessingModule<CatalystRecipeInput, StampingRecipe> processing = processing().get();
            if (processing.getLastRecipe() != null) {
                return processing.getLastRecipe().getInputItem().getItems()[0];
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getOutput() {
        if (processing().isPresent()) {
            ProcessingModule<CatalystRecipeInput, StampingRecipe> processing = processing().get();
            if (processing.getLastRecipe() != null) {
                return processing.getLastRecipe().getOutputItem();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.STAMPING_PRESS.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new StampingPressMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_STAMPING_PRESS;
    }

    public void updateAnimations() {
        float prevPistonHeight = stampingAnimation.getValue(PISTON_ANIMATION_INDEX);
        stampingAnimation.update();
        float newPistonHeight = stampingAnimation.getValue(PISTON_ANIMATION_INDEX);
        if (prevPistonHeight > newPistonHeight
                && (newPistonHeight == PISTON_DOWN_OFFSET)) {
            Player player = Minecraft.getInstance().player;
            level.playSound(player, worldPosition, TestModSounds.STAMPING_PRESS_PISTON.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public float getPistonHeight() {
        return stampingAnimation.getValue(PISTON_ANIMATION_INDEX);
    }

    public float getConveyorPosition() {
        return stampingAnimation.getValue(CONVEYOR_ANIMATION_INDEX);
    }
}
