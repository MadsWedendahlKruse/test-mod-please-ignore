package mwk.testmod.datagen.loot;

import java.util.Set;
import java.util.stream.Collectors;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TestModBlockLootTables extends BlockLootSubProvider {

    public TestModBlockLootTables(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        oreDrop(TestModBlocks.ILMENITE_ORE.get(), TestModItems.RAW_ILMENITE.get(), 1, 1);
        oreDrop(TestModBlocks.DEEPSLATE_ILMENITE_ORE.get(), TestModItems.RAW_ILMENITE.get(), 1, 1);

        // TODO: Find a way to do this automatically so I don't have to update
        // this file every time I add a new block.
        // Can do something like this maybe?
        TestModBlocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof MultiBlockPartBlock || block instanceof ConduitBlock) {
                dropSelf(block);
            }
        });

        dropOther(TestModBlocks.HOLOGRAM.get(), ItemStack.EMPTY.getItem());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return TestModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get)
                .collect(Collectors.toList());
    }

    /**
     * Drops the specified item when the block is broken. The item is dropped with a count between
     * min and max, and the Fortune enchantment is applied.
     *
     * @param block The block to drop the item from
     * @param item  The item to drop
     * @param min   The minimum number of items to drop
     * @param max   The maximum number of items to drop
     */
    private void oreDrop(Block block, Item item, int min, int max) {
        add(block, createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addOreBonusCount(
                                this.registries.holderOrThrow(Enchantments.FORTUNE))))));
    }
}
