package mwk.testmod.compat.jei.recipe_categories;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.helpers.IGuiHelper;
import mwk.testmod.client.gui.GuiUtils;
import mwk.testmod.client.gui.widgets.EnergyBar;
import mwk.testmod.client.gui.widgets.progress.ProgressArrowSingle;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.compat.jei.JEITestModRecipeTypes;
import mwk.testmod.compat.jei.recipe_categories.base.BaseRecipeCategory;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.world.item.ItemStack;

public class CrushingRecipeCategory extends BaseRecipeCategory<CrushingRecipe> {

    public static final int INPUT_SLOTS = 1;
    public static final int OUTPUT_SLOTS = 1;
    public static final int INPUT_X = 2 * PADDING + EnergyBar.WIDTH + 1;
    public static final int INPUT_Y = PADDING + 1;
    public static final int ARROW_X = (INPUT_X - 1) + PADDING + GuiUtils.ITEM_SLOT_SIZE;
    public static final int ARROW_Y = INPUT_Y + 2;
    public static final int ICON_X = ARROW_X + 2;
    public static final int ICON_Y = ARROW_Y + GuiUtils.ITEM_SLOT_SIZE;
    public static final int OUTPUT_X = ARROW_X + ProgressArrowSingle.WIDTH + PADDING;
    public static final int OUTPUT_Y = INPUT_Y;

    public CrushingRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, TestModBlocks.CRUSHER.get(), JEITestModRecipeTypes.CRUSHING,
                ProgressArrowSingle::new, "crushing", INPUT_X, INPUT_Y, INPUT_SLOTS, OUTPUT_X,
                OUTPUT_Y, OUTPUT_SLOTS, ARROW_X, ARROW_Y, ICON_X, ICON_Y);
    }

    @Override
    protected List<ItemStack> getOutputStacks(CrushingRecipe recipe) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        stacks.add(recipe.getResultItem());
        return stacks;
    }

}
