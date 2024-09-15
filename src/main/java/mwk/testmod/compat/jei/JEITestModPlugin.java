package mwk.testmod.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mwk.testmod.TestMod;
import mwk.testmod.client.gui.screen.CrusherScreen;
import mwk.testmod.client.gui.screen.InductionFurnaceScreen;
import mwk.testmod.client.gui.screen.SeparatorScreen;
import mwk.testmod.client.gui.screen.StampingPressScreen;
import mwk.testmod.client.gui.screen.base.MachineScreen;
import mwk.testmod.client.gui.screen.base.ProcessingScreen;
import mwk.testmod.client.gui.screen.config.GuiConfig;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.client.gui.widgets.progress.ProgressArrow;
import mwk.testmod.client.gui.widgets.progress.ProgressArrowFactory;
import mwk.testmod.client.gui.widgets.progress.ProgressIcon;
import mwk.testmod.compat.jei.recipe_categories.CrushingRecipeCategory;
import mwk.testmod.compat.jei.recipe_categories.SeparationRecipeCategory;
import mwk.testmod.compat.jei.recipe_categories.StampingRecipeCategory;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class JEITestModPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TestMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CrushingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new SeparationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new StampingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        RecipeManager recipeManager = minecraft.level.getRecipeManager();
        registration.addRecipes(JEITestModRecipeTypes.CRUSHING,
                recipeManager.getAllRecipesFor(TestModRecipeTypes.CRUSHING.get()).stream()
                        .map(recipe -> recipe.value()).toList());
        registration.addRecipes(JEITestModRecipeTypes.SEPARATION,
                recipeManager.getAllRecipesFor(TestModRecipeTypes.SEPARATION.get()).stream()
                        .map(recipe -> recipe.value()).toList());
        registration.addRecipes(JEITestModRecipeTypes.STAMPING,
                recipeManager.getAllRecipesFor(TestModRecipeTypes.STAMPING.get()).stream()
                        .map(recipe -> recipe.value()).toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                TestModBlocks.INDUCTION_FURNACE.get().asItem().getDefaultInstance(),
                RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(TestModBlocks.CRUSHER.get().asItem().getDefaultInstance(),
                JEITestModRecipeTypes.CRUSHING);
        registration.addRecipeCatalyst(TestModBlocks.SEPARATOR.get().asItem().getDefaultInstance(),
                JEITestModRecipeTypes.SEPARATION);
        registration.addRecipeCatalyst(
                TestModBlocks.STAMPING_PRESS.get().asItem().getDefaultInstance(),
                JEITestModRecipeTypes.STAMPING);
    }

    private Collection<Rect2i> getRecipeClickAreas(GuiConfig config) {
        // TODO: This method could be somewhere else. Also we're assuming the progress arrows are
        // spaced vertically.
        ArrayList<Rect2i> clickAreas = new ArrayList<>();
        clickAreas.add(new Rect2i(config.progressIconX(), config.progressIconY(),
                ProgressIcon.WIDTH, ProgressIcon.HEIGHT));
        for (int i = 0; i < config.progressArrows(); i++) {
            ProgressArrow arrow = ProgressArrowFactory.create(config.progressArrowType(), null,
                    config.progressArrowX(),
                    config.progressArrowY() + i * config.progressArrowSpacing());
            clickAreas.add(
                    new Rect2i(arrow.getX(), arrow.getY(), arrow.getWidth(), arrow.getHeight()));
        }
        return clickAreas;
    }

    private <T extends ProcessingScreen<?>> void registerClickArea(
            IGuiHandlerRegistration registration, Class<? extends T> containerScreenClass,
            GuiConfig config, RecipeType<?> recipeType) {
        for (Rect2i clickArea : getRecipeClickAreas(config)) {
            registration.addRecipeClickArea(containerScreenClass, clickArea.getX(),
                    clickArea.getY(), clickArea.getWidth(), clickArea.getHeight(), recipeType);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new IGlobalGuiHandler() {
            @Override
            public Collection<Rect2i> getGuiExtraAreas() {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen instanceof MachineScreen machineScreen) {
                    return machineScreen.getGuiExtraAreas();
                }
                return Collections.emptyList();
            }
        });
        registerClickArea(registration, InductionFurnaceScreen.class, GuiConfigs.INDUCTION_FURNACE,
                RecipeTypes.BLASTING);
        registerClickArea(registration, SeparatorScreen.class, GuiConfigs.SEPARATOR,
                JEITestModRecipeTypes.SEPARATION);
        registerClickArea(registration, CrusherScreen.class, GuiConfigs.CRUSHER,
                JEITestModRecipeTypes.CRUSHING);
        registerClickArea(registration, StampingPressScreen.class, GuiConfigs.STAMPING_PRESS,
                JEITestModRecipeTypes.STAMPING);
    }
}
