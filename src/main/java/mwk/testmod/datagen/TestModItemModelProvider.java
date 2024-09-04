package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModItemModelProvider extends ItemModelProvider {

    public TestModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TestMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        TestModItems.ITEMS.getEntries().forEach(itemHolder -> {
            Item item = itemHolder.get();
            // Skip block items
            if (item instanceof BlockItem) {
                return;
            }
            // Skip items that already have a model
            if (existingFileHelper.exists(itemHolder.getId(), PackType.CLIENT_RESOURCES,
                    ".json", "models/item")) {
                return;
            }
            basicItem(item);
        });
    }
}
