package mwk.testmod.compat.jei.recipe_categories;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.helpers.IGuiHelper;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.client.gui.widgets.EnergyBar;
import mwk.testmod.client.gui.widgets.progress.ProgressArrowSingle;
import mwk.testmod.common.recipe.StampingRecipe;
import mwk.testmod.compat.jei.JEITestModRecipeTypes;
import mwk.testmod.compat.jei.recipe_categories.base.BaseRecipeCategory;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.world.item.ItemStack;

public class StampingRecipeCategory extends BaseRecipeCategory<StampingRecipe> {

    public static final int INPUT_SLOTS = 2;
    public static final int OUTPUT_SLOTS = 1;
    public static final int INPUT_X = 2 * PADDING + EnergyBar.WIDTH + 1;
    public static final int INPUT_Y = PADDING + 1;
    public static final int ARROW_X = (INPUT_X - 1) + PADDING + GuiUtils.ITEM_SLOT_SIZE;
    public static final int ARROW_Y = INPUT_Y + 2 + GuiUtils.ITEM_SLOT_SIZE / 2;
    public static final int ICON_X = ARROW_X + 2;
    public static final int ICON_Y = ARROW_Y + GuiUtils.ITEM_SLOT_SIZE;
    public static final int OUTPUT_X = ARROW_X + ProgressArrowSingle.WIDTH + PADDING;
    public static final int OUTPUT_Y = INPUT_Y + GuiUtils.ITEM_SLOT_SIZE / 2;

    public StampingRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, TestModBlocks.STAMPING_PRESS.get(), JEITestModRecipeTypes.STAMPING,
                ProgressArrowSingle::new, "stamping", INPUT_X, INPUT_Y, INPUT_SLOTS, OUTPUT_X,
                OUTPUT_Y, OUTPUT_SLOTS, ARROW_X, ARROW_Y, ICON_X, ICON_Y);
    }

    @Override
    protected List<ItemStack> getOutputStacks(StampingRecipe recipe) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        stacks.add(recipe.getResultItem());
        return stacks;
    }
}
