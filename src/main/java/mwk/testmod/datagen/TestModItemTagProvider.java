package mwk.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import mwk.testmod.common.util.TestModTags;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModItemTagProvider extends ItemTagsProvider {

    public TestModItemTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTagContentsGetter,
            ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagContentsGetter, TestMod.MODID,
                existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        tagRawMetarials();
        tagDusts();
        tagIngots();
        tagOres();
    }

    private void tagRawMaterial(Item item, TagKey<Item> tag) {
        this.tag(tag).add(item);
        this.tag(TestModTags.Items.FORGE_RAW_MATERIALS).add(item);
    }

    private void tagRawMetarials() {
        tagRawMaterial(TestModItems.RAW_ILMENITE.get(), TestModTags.Items.RAW_ILMENITE);
    }

    private void tagDust(Item item, TagKey<Item> tag) {
        this.tag(tag).add(item);
        this.tag(TestModTags.Items.FORGE_DUSTS).add(item);
    }

    private void tagDusts() {
        tagDust(TestModItems.COAL_DUST.get(), TestModTags.Items.COAL_DUST);
        tagDust(TestModItems.IRON_DUST.get(), TestModTags.Items.IRON_DUST);
        tagDust(TestModItems.STEEL_DUST.get(), TestModTags.Items.STEEL_DUST);
        tagDust(TestModItems.ILMENITE_DUST.get(), TestModTags.Items.ILMENITE_DUST);
        tagDust(TestModItems.TITANIUM_DUST.get(), TestModTags.Items.TITANIUM_DUST);
    }

    private void tagIngot(Item item, TagKey<Item> tag) {
        this.tag(tag).add(item);
        this.tag(TestModTags.Items.FORGE_INGOTS).add(item);
    }

    private void tagIngots() {
        tagIngot(TestModItems.STEEL_INGOT.get(), TestModTags.Items.STEEL_INGOT);
        tagIngot(TestModItems.TITANIUM_INGOT.get(), TestModTags.Items.TITANIUM_INGOT);
    }

    private void tagOre(Item item, TagKey<Item>... tags) {
        this.tag(TestModTags.Items.FORGE_ORES).add(item);
        for (TagKey<Item> tag : tags) {
            this.tag(tag).add(item);
        }
    }

    private void tagOres() {
        tagOre(TestModBlocks.ILMENITE_ORE.asItem(), TestModTags.Items.ILMENITE_ORE,
                TestModTags.Items.FORGE_ORES_IN_GROUND_STONE,
                TestModTags.Items.FORGE_ORE_RATES_SINGULAR);
        tagOre(TestModBlocks.DEEPSLATE_ILMENITE_ORE.asItem(), TestModTags.Items.ILMENITE_ORE,
                TestModTags.Items.FORGE_ORES_IN_GROUND_DEEPSLATE,
                TestModTags.Items.FORGE_ORE_RATES_SINGULAR);
    }
}
