package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.base.FixedAnimation.Function;
import mwk.testmod.client.animations.base.KeyframeManager;
import mwk.testmod.common.block.entity.base.crafter.SingleCrafterBlockEntity;
import mwk.testmod.common.block.inventory.StampingPressMenu;
import mwk.testmod.common.item.misc.StampingDieItem;
import mwk.testmod.common.recipe.StampingRecipe;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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

public class StampingPressBlockEntity extends SingleCrafterBlockEntity<StampingRecipe> {

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

    private final KeyframeManager keyframeManager;

    public StampingPressBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.STAMPING_PRESS_ENTITY_TYPE.get(), pos, state,
                TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(), 20, 2, 1, 6, EMPTY_TANKS,
                EMPTY_TANKS, DEFAULT_MAX_PROGRESS, TestModRecipeTypes.STAMPING.get(),
                TestModSounds.STAMPING_PRESS.get(), TestModSounds.STAMPING_PRESS_DURATION);
        // One animation for the piston, and one for the item being stamped
        keyframeManager = new KeyframeManager(new float[]{0, -CONVEYOR_CENTER_OFFSET});
        final float duration = DEFAULT_MAX_PROGRESS / 20.0F;
        // Item being stamped
        // We can the use the center as a reference point for when to swap the rendered item from
        // the input to the output. We therefore stop the item before it gets stamped just shy of
        // the center.
        keyframeManager.addKeyframe(CONVEYOR_ANIMATION_INDEX, duration / 3, -0.001F);
        // As the piston goes down, the item moves a very tiny distance forward, so it reaches the
        // center of the conveyor. We can then swap the item from the input to the output
        keyframeManager.addKeyframe(CONVEYOR_ANIMATION_INDEX, PISTON_DOWN_DURATION, 0);
        // Remain at the center until 2/3 of the way through the animation
        keyframeManager.addKeyframe(CONVEYOR_ANIMATION_INDEX, duration / 3 - PISTON_DOWN_DURATION,
                0);
        // Finally, move the item to the output
        keyframeManager.addKeyframe(CONVEYOR_ANIMATION_INDEX, duration / 3, CONVEYOR_CENTER_OFFSET);
        // Piston remains up until the item is moved to the center
        keyframeManager.addKeyframe(PISTON_ANIMATION_INDEX, duration / 3, 0);
        keyframeManager.addKeyframe(PISTON_ANIMATION_INDEX, PISTON_DOWN_DURATION,
                PISTON_DOWN_OFFSET, Function.EASE_IN_CUBIC);
        keyframeManager.addKeyframe(PISTON_ANIMATION_INDEX, PISTON_HOLD_DURATION,
                PISTON_DOWN_OFFSET);
        keyframeManager.addKeyframe(PISTON_ANIMATION_INDEX, PISTON_UP_DURATION, PISTON_UP_OFFSET,
                Function.EASE_OUT_CUBIC);

        keyframeManager.start();
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
    protected boolean canProcessRecipe(StampingRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        ItemStack output = recipe.getOutput();
        return canInsertItemIntoSlot(inputSlots, output.getItem(), output.getCount());
    }

    @Override
    protected void processRecipe(StampingRecipe recipe) {
        ItemStack output = recipe.getOutput();
        // Slot 0 is the stamping die (which isn't consumed)
        inventory.extractItem(1, 1, false);
        inventory.setStackInSlot(inputSlots, new ItemStack(output.getItem(),
                inventory.getStackInSlot(inputSlots).getCount() + output.getCount()));
    }

    @Override
    protected void onInventoryChanged(int slot) {
        super.onInventoryChanged(slot);
        // Reset animation if the inputs change
        if (slot < inputSlots) {
            keyframeManager.start();
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        // Note to self: This is used both on LevelChunk load and Block Update
        CompoundTag tag = new CompoundTag();
        tag.put(NBT_TAG_DIE, getStampingDie().save(new CompoundTag()));
        tag.put(NBT_TAG_INPUT, getInput().save(new CompoundTag()));
        if (latestRecipe != null) {
            tag.put(NBT_TAG_OUTPUT, latestRecipe.getOutput().save(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        // Note to self: This is used on Block Update
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }
        if (tag.contains(NBT_TAG_OUTPUT)) {
            latestRecipe = new StampingRecipe(
                    Ingredient.of(ItemStack.of(tag.getCompound(NBT_TAG_DIE))),
                    Ingredient.of(ItemStack.of(tag.getCompound(NBT_TAG_INPUT))),
                    ItemStack.of(tag.getCompound(NBT_TAG_OUTPUT)));
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        // Note to self: This is used on LevelChunk load
        super.handleUpdateTag(tag);
        if (tag.contains(NBT_TAG_DIE)) {
            inventory.setStackInSlot(0, ItemStack.of(tag.getCompound(NBT_TAG_DIE)));
        }
        if (tag.contains(NBT_TAG_INPUT)) {
            inventory.setStackInSlot(1, ItemStack.of(tag.getCompound(NBT_TAG_INPUT)));
        }
        if (tag.contains(NBT_TAG_OUTPUT)) {
            latestRecipe = new StampingRecipe(
                    Ingredient.of(ItemStack.of(tag.getCompound(NBT_TAG_DIE))),
                    Ingredient.of(ItemStack.of(tag.getCompound(NBT_TAG_INPUT))),
                    ItemStack.of(tag.getCompound(NBT_TAG_OUTPUT)));
        }
    }

    public ItemStack getStampingDie() {
        return inventory.getStackInSlot(0);
    }

    public ItemStack getInput() {
        return inventory.getStackInSlot(1);
    }

    public ItemStack getOutput() {
        return latestRecipe != null ? latestRecipe.getOutput() : ItemStack.EMPTY;
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
        float prevPistonHeight = keyframeManager.getValue(PISTON_ANIMATION_INDEX);
        keyframeManager.update();
        float newPistonHeight = keyframeManager.getValue(PISTON_ANIMATION_INDEX);
        if (prevPistonHeight > newPistonHeight
                && (newPistonHeight == PISTON_DOWN_OFFSET)) {
            Player player = Minecraft.getInstance().player;
            level.playSound(player, worldPosition, TestModSounds.STAMPING_PRESS_PISTON.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public float getPistonHeight() {
        return keyframeManager.getValue(PISTON_ANIMATION_INDEX);
    }

    public float getConveyorPosition() {
        return keyframeManager.getValue(CONVEYOR_ANIMATION_INDEX);
    }
}
