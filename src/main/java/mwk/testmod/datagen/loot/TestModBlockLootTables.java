package mwk.testmod.datagen.loot;

import java.util.Set;
import java.util.stream.Collectors;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
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

    public TestModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        oreDrop(TestModBlocks.ILMENITE_ORE.get(), TestModItems.RAW_ILMENITE.get(), 1, 1);
        oreDrop(TestModBlocks.DEEPSLATE_ILMENITE_ORE.get(), TestModItems.RAW_ILMENITE.get(), 1, 1);

        // TODO: Find a way to do this automatically so I don't have to update
        // this file every time I add a new block.
        // Can do something like this maybe?
        TestModBlocks.BLOCKS.getEntries().forEach(entry -> {
            if (entry.get() instanceof MultiBlockPartBlock) {
                dropSelf(entry.get());
            }
        });

        dropOther(TestModBlocks.HOLOGRAM.get(), ItemStack.EMPTY.getItem());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return TestModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get)
                .collect(Collectors.toList());
    }

    private void oreDrop(Block block, Item item, int min, int max) {
        add(block, createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));
    }
}
