package mwk.testmod.compat;

import java.util.Collection;
import java.util.Collections;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mwk.testmod.TestMod;
import mwk.testmod.client.gui.screen.base.BaseMachineScreen;
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
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new IGlobalGuiHandler() {
            @Override
            public Collection<Rect2i> getGuiExtraAreas() {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen instanceof BaseMachineScreen machineScreen) {
                    return machineScreen.getGuiExtraAreas();
                }
                return Collections.emptyList();
            }
        });
    }
}
