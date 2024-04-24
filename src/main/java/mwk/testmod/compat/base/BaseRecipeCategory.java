package mwk.testmod.compat.base;

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
import mwk.testmod.client.gui.widgets.ProgressArrow;
import mwk.testmod.client.gui.widgets.ProgressIcon;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import mwk.testmod.common.util.inventory.ItemSlotGridHelper;
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
    public static final LoopingAnimationFloat PROGRESS_ANIMATION =
            new LoopingAnimationFloat(1.5F, Function.LINEAR, 0.0F, (float) MAX_PROGRESS);
    public static final LoopingAnimationFloat POWER_ANIMATION =
            new LoopingAnimationFloat(15.0F, Function.LINEAR, 0.0F, 1.0F);

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
    public IDrawable getBackground() {
        return background;
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
        int fullBarHeight = Math.min(contentsHeight, EnergyBar.HEIGHT) - 2;
        int barHeight = (int) ((1.0F - POWER_ANIMATION.getValue()) * fullBarHeight);
        int barX = PADDING;
        int barY = PADDING + 1;
        // Render item slot border around the energy bar
        RenderUtils.renderItemSlot(guiGraphics, barX, barY, EnergyBar.WIDTH + 2, fullBarHeight + 2);
        guiGraphics.blitSprite(EnergyBar.SPRITE_EMPTY, EnergyBar.WIDTH, EnergyBar.HEIGHT, 0,
                EnergyBar.HEIGHT - fullBarHeight, barX, barY, EnergyBar.WIDTH, fullBarHeight);
        guiGraphics.blitSprite(EnergyBar.SPRITE_FULL, EnergyBar.WIDTH, EnergyBar.HEIGHT, 0,
                EnergyBar.HEIGHT - barHeight, barX, barY + fullBarHeight - barHeight,
                EnergyBar.WIDTH, barHeight);
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        PROGRESS_ANIMATION.update();
        POWER_ANIMATION.update();
        drawEnergyBar(guiGraphics);
        for (int i = 0; i < inputSlots; i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    ItemSlotGridHelper.ROWS_3.getSlotPosition(inputX, inputY, i);
            RenderUtils.renderItemSlot(guiGraphics, slotPosition.x(), slotPosition.y());
        }
        for (int i = 0; i < outputSlots; i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    ItemSlotGridHelper.ROWS_3.getSlotPosition(outputX, outputY, i);
            RenderUtils.renderItemSlot(guiGraphics, slotPosition.x(), slotPosition.y());
        }
        int progress = PROGRESS_ANIMATION.getValue().intValue();
        progressArrow.render(guiGraphics, progress, MAX_PROGRESS);
        progressIcon.render(guiGraphics, progress, MAX_PROGRESS);
    }

    @FunctionalInterface
    public interface ProgressArrowFactory {
        ProgressArrow create(CrafterMachineMenu menu, int x, int y);
    }

}
