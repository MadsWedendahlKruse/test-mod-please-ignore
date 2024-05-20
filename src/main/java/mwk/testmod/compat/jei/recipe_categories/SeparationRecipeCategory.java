package mwk.testmod.compat.jei.recipe_categories;

import java.util.List;
import mezz.jei.api.helpers.IGuiHelper;
import mwk.testmod.client.gui.widgets.EnergyBar;
import mwk.testmod.client.gui.widgets.progress.ProgressArrow1To3;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.common.recipe.SeparationRecipe;
import mwk.testmod.compat.jei.JEITestModRecipeTypes;
import mwk.testmod.compat.jei.recipe_categories.base.BaseRecipeCategory;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.world.item.ItemStack;

public class SeparationRecipeCategory extends BaseRecipeCategory<SeparationRecipe> {

    public static final int INPUT_SLOTS = 1;
    public static final int OUTPUT_SLOTS = 3;
    public static final int INPUT_X = 2 * PADDING + EnergyBar.WIDTH + 1;
    public static final int INPUT_Y = PADDING + 1;
    public static final int ARROW_X = (INPUT_X - 1) + PADDING + RenderUtils.ITEM_SLOT_SIZE;
    public static final int ARROW_Y = INPUT_Y + 2;
    public static final int ICON_X = ARROW_X + 11;
    public static final int ICON_Y = ARROW_Y + 17;
    public static final int OUTPUT_X = ARROW_X + ProgressArrow1To3.WIDTH + PADDING;
    public static final int OUTPUT_Y = INPUT_Y;

    public SeparationRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, TestModBlocks.SEPARATOR.get(), JEITestModRecipeTypes.SEPARATION,
                ProgressArrow1To3::new, "separation", INPUT_X, INPUT_Y + RenderUtils.ITEM_SLOT_SIZE,
                INPUT_SLOTS, OUTPUT_X, OUTPUT_Y, OUTPUT_SLOTS, ARROW_X, ARROW_Y, ICON_X, ICON_Y);
    }

    @Override
    protected List<ItemStack> getOutputStacks(SeparationRecipe recipe) {
        return recipe.getOutputs();
    }

}
