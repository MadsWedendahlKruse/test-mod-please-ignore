package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModItemModelProvider extends ItemModelProvider {

    public TestModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TestMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(TestModItems.RAW_ILMENITE.get());

        basicItem(TestModItems.COAL_DUST.get());
        basicItem(TestModItems.IRON_DUST.get());
        basicItem(TestModItems.STEEL_DUST.get());
        basicItem(TestModItems.ILMENITE_DUST.get());
        basicItem(TestModItems.TITANIUM_DUST.get());

        basicItem(TestModItems.STEEL_INGOT.get());
        basicItem(TestModItems.TITANIUM_INGOT.get());
    }
}
