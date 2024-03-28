package mwk.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import mwk.testmod.common.util.TestModTags;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
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
    protected void addTags(Provider arg0) {
        this.tag(TestModTags.Items.COAL_DUST).add(TestModItems.COAL_DUST.get());
        this.tag(TestModTags.Items.IRON_DUST).add(TestModItems.IRON_DUST.get());
        this.tag(TestModTags.Items.STEEL_DUST).add(TestModItems.STEEL_DUST.get());
    }
}
