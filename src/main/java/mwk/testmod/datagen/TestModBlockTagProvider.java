package mwk.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.util.TestModTags;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModBlockTagProvider extends BlockTagsProvider {

    public TestModBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TestMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        // TODO: Works for now, might have to do something different in the future?
        TestModBlocks.BLOCKS.getEntries().forEach(entry -> {
            if (entry.get() instanceof MultiBlockPartBlock) {
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(entry.get());
                this.tag(BlockTags.NEEDS_IRON_TOOL).add(entry.get());
            }
        });

        addTagsOre(TestModBlocks.ILMENITE_ORE.get(), TestModTags.Blocks.ILMENITE_ORE,
                TestModTags.Blocks.FORGE_ORES_IN_GROUND_STONE,
                TestModTags.Blocks.FORGE_ORE_RATES_SINGULAR, BlockTags.NEEDS_STONE_TOOL);
        addTagsOre(TestModBlocks.DEEPSLATE_ILMENITE_ORE.get(), TestModTags.Blocks.ILMENITE_ORE,
                TestModTags.Blocks.FORGE_ORES_IN_GROUND_DEEPSLATE,
                TestModTags.Blocks.FORGE_ORE_RATES_SINGULAR, BlockTags.NEEDS_STONE_TOOL);
    }

    private void addTagsOre(Block block, TagKey<Block>... tags) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        this.tag(TestModTags.Blocks.FORGE_ORES).add(block);
        for (TagKey<Block> tag : tags) {
            this.tag(tag).add(block);
        }
    }
}
