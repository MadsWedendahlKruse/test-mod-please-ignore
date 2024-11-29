package mwk.testmod.compat.jei.recipe_categories.base;

import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mwk.testmod.client.animations.LoopingAnimationFloat;
import mwk.testmod.client.animations.base.FixedAnimation.Function;
import mwk.testmod.client.gui.widgets.EnergyBar;
import mwk.testmod.client.gui.widgets.progress.ProgressArrow;
import mwk.testmod.client.gui.widgets.progress.ProgressIcon;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.client.utils.ItemSlotGridHelper;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

public abstract class BaseRecipeCategory<T extends Recipe<?>> implements IRecipeCategory<T> {

    public static final int PADDING = 4;
    public static final int MAX_PROGRESS = 18;
    public static final float PROGRESS_ANIMATION_DURATION = 1.5F;
    public static final float POWER_ANIMATION_DURATION = 15.0F;

    private final Block crafter;
    private final RecipeType<T> recipeType;
    private final IDrawable background;
    private final IDrawable icon;

    private final int inputX;
    private final int inputY;
    private final int inputSlots;
    private final int outputX;
    private final int outputY;
    private final int outputSlots;

    private final ProgressArrow progressArrow;
    private final ProgressIcon progressIcon;
    private final LoopingAnimationFloat progressAnimation;
    private final LoopingAnimationFloat powerAnimation;

    private final int contentsHeight;

    public BaseRecipeCategory(IGuiHelper guiHelper, Block crafter, RecipeType<T> recipeType,
            ProgressArrowFactory arrowFactory, String iconName, int inputX, int inputY,
            int inputSlots, int outputX, int outputY, int outputSlots, int arrowX, int arrowY,
            int iconX, int iconY) {
        this.crafter = crafter;
        this.recipeType = recipeType;
        this.inputX = inputX;
        this.inputY = inputY;
        this.inputSlots = inputSlots;
        this.outputX = outputX;
        this.outputY = outputY;
        this.outputSlots = outputSlots;
        this.progressArrow = arrowFactory.create(null, arrowX, arrowY);
        this.progressIcon =
                new ProgressIcon(ProgressIcon.createSprites(iconName), null, iconX, iconY);
        this.progressAnimation = new LoopingAnimationFloat(PROGRESS_ANIMATION_DURATION,
                Function.LINEAR, 0.0F, (float) MAX_PROGRESS);
        this.powerAnimation = new LoopingAnimationFloat(POWER_ANIMATION_DURATION, Function.LINEAR,
                0.0F, 1.0F);
        // Find the largest y-coordinate of the elements in the category
        int[] elementsYMax = {inputY + ItemSlotGridHelper.ROWS_3.getHeight(inputSlots),
                outputY + ItemSlotGridHelper.ROWS_3.getHeight(outputSlots),
                arrowY + progressArrow.getHeight(), iconY + ProgressIcon.HEIGHT};
        int yMax = 0;
        for (int y : elementsYMax) {
            if (y > yMax) {
                yMax = y;
            }
        }
        this.contentsHeight = yMax - PADDING;
        // Find the largest x-coordinate of the elements in the category
        int[] elementsXMax = {inputX + ItemSlotGridHelper.ROWS_3.getWidth(inputSlots),
                outputX + ItemSlotGridHelper.ROWS_3.getWidth(outputSlots),
                arrowX + progressArrow.getWidth(), iconX + ProgressIcon.WIDTH};
        int xMax = 0;
        for (int x : elementsXMax) {
            if (x > xMax) {
                xMax = x;
            }
        }
        this.background = guiHelper.createBlankDrawable(xMax + PADDING, yMax + PADDING);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                crafter.asItem().getDefaultInstance());
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return crafter.getName();
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    ItemSlotGridHelper.ROWS_3.getSlotPosition(inputX, inputY, i);
            builder.addSlot(RecipeIngredientRole.INPUT, slotPosition.x(), slotPosition.y())
                    .addIngredients(ingredients.get(i));
        }
        List<ItemStack> outputStacks = getOutputStacks(recipe);
        for (int i = 0; i < outputStacks.size(); i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    ItemSlotGridHelper.ROWS_3.getSlotPosition(outputX, outputY, i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, slotPosition.x(), slotPosition.y())
                    .addItemStack(outputStacks.get(i));
        }
    }

    protected abstract List<ItemStack> getOutputStacks(T recipe);

    private void drawEnergyBar(GuiGraphics guiGraphics) {
        // -2 to account for the border
        int fullBarHeight = Math.min(contentsHeight, EnergyBar.DEFAULT_HEIGHT) - 2;
        int barHeight = (int) ((1.0F - powerAnimation.getValue()) * fullBarHeight);
        int barX = PADDING;
        int barY = PADDING + 1;
        // Render item slot border around the energy bar
        GuiUtils.renderItemSlot(guiGraphics, barX, barY, EnergyBar.DEFAULT_WIDTH + 2,
                fullBarHeight + 2);
        guiGraphics.blitSprite(EnergyBar.SPRITE_EMPTY, EnergyBar.DEFAULT_WIDTH,
                EnergyBar.DEFAULT_HEIGHT, 0,
                EnergyBar.DEFAULT_HEIGHT - fullBarHeight, barX, barY, EnergyBar.DEFAULT_WIDTH,
                fullBarHeight);
        guiGraphics.blitSprite(EnergyBar.SPRITE_FULL, EnergyBar.DEFAULT_WIDTH,
                EnergyBar.DEFAULT_HEIGHT, 0,
                EnergyBar.DEFAULT_HEIGHT - barHeight, barX, barY + fullBarHeight - barHeight,
                EnergyBar.DEFAULT_WIDTH, barHeight);
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        progressAnimation.update();
        powerAnimation.update();
        background.draw(guiGraphics);
        drawEnergyBar(guiGraphics);
        for (int i = 0; i < inputSlots; i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    ItemSlotGridHelper.ROWS_3.getSlotPosition(inputX, inputY, i);
            GuiUtils.renderItemSlot(guiGraphics, slotPosition.x(), slotPosition.y());
        }
        for (int i = 0; i < outputSlots; i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    ItemSlotGridHelper.ROWS_3.getSlotPosition(outputX, outputY, i);
            GuiUtils.renderItemSlot(guiGraphics, slotPosition.x(), slotPosition.y());
        }
        int progress = progressAnimation.getValue().intValue();
        progressArrow.render(guiGraphics, progress, MAX_PROGRESS);
        progressIcon.render(guiGraphics, progress, MAX_PROGRESS);
    }

    @FunctionalInterface
    public interface ProgressArrowFactory {

        ProgressArrow create(ProcessingMenu menu, int x, int y);
    }

}
