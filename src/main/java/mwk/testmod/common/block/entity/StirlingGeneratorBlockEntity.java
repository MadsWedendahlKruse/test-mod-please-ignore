package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.AnimationClock;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.entity.modules.EnergyModule.EnergyType;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.block.entity.modules.SoundModule;
import mwk.testmod.common.block.inventory.StirlingGeneratorMenu;
import mwk.testmod.common.recipe.StirlingGeneratorRecipe;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class StirlingGeneratorBlockEntity extends
        GeneratorBlockEntity<SingleRecipeInput, StirlingGeneratorRecipe> {

    private static final float FLYWHEEL_SPEED = (float) (2.5 * Math.PI); // [rad/s]
    private float flywheelAngle;

    public StirlingGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.STIRLING_GENERATOR_ENTITY_TYPE.get(), pos, state,
                TestModRecipeTypes.STIRLING_GENERATOR.get(),
                TestModConfig.GENERATOR_REDSTONE_ENERGY_PER_TICK.get());
        addModule(new EnergyModule(this, TestModConfig.GENERATOR_ENERGY_CAPACITY_DEFAULT.get(),
                EnergyType.PRODUCER));
        addModule(new InventoryModule(this, 1, 0, 6, this::onInventoryChanged,
                this::isInputItemValid));
        addModule(new AutoIOModule());
        addModule(new SoundModule(this, null, (int) ((20 * Math.PI * 2 / FLYWHEEL_SPEED) / 2)));
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.STIRLING_GENERATOR.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new StirlingGeneratorMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_STIRLING_GENERATOR;
    }

    public void updateFlywheelAngle() {
        flywheelAngle += FLYWHEEL_SPEED * AnimationClock.getInstance().getDeltaTime();
    }

    public float getFlywheelAngle() {
        return flywheelAngle;
    }

    @Override
    protected SingleRecipeInput getRecipeInput() {
        ItemStack stack = inventory().map(inventory -> inventory.getStackInSlot(0))
                .orElse(ItemStack.EMPTY);
        return new SingleRecipeInput(stack);
    }

    @Override
    protected boolean isSameInput(SingleRecipeInput input1, SingleRecipeInput input2) {
        return ItemStack.matches(input1.getItem(0), input2.getItem(0));
    }

    @Override
    protected boolean canProcessRecipe(StirlingGeneratorRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            ItemStack result = recipe.getResultItem(null);
            return result.isEmpty() || inventory.canInsertItemIntoSlot(0, result.getItem(),
                    result.getCount());
        }
        return false;
    }

    @Override
    protected void processRecipe(StirlingGeneratorRecipe recipe) {
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                // TODO: Ingredients can have multiple items?
                ItemStack ingredient = ingredients.get(i).getItems()[0];
                inventory.extractItem(i, ingredient.getCount(), false);
            }
            ItemStack result = recipe.getResultItem(null);
            if (!result.isEmpty()) {
                inventory.setStackInSlot(0, new ItemStack(result.getItem(),
                        inventory.getStackInSlot(0).getCount() + result.getCount()));
            }
        }
    }
}
