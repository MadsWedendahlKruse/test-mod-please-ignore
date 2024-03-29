package mwk.testmod.datagen.loot;

import java.util.Set;
import java.util.stream.Collectors;

import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TestModBlockLootTables extends BlockLootSubProvider {

    public TestModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // TODO: Find a way to do this automatically so I don't have to update
        // this file every time I add a new block.
        this.dropSelf(TestModBlocks.MACHINE_FRAME_BASIC.get());
        this.dropSelf(TestModBlocks.MACHINE_FRAME_REINFORCED.get());
        this.dropSelf(TestModBlocks.MACHINE_FRAME_ADVANCED.get());
        this.dropSelf(TestModBlocks.MACHINE_INPUT_PORT.get());
        this.dropSelf(TestModBlocks.MACHINE_OUTPUT_PORT.get());
        this.dropSelf(TestModBlocks.MACHINE_ENERGY_PORT.get());

        this.dropSelf(TestModBlocks.COPPER_COIL.get());

        this.dropSelf(TestModBlocks.INDUCTION_FURNACE.get());
        this.dropSelf(TestModBlocks.SUPER_ASSEMBLER.get());
        this.dropSelf(TestModBlocks.CRUSHER.get());

        this.dropOther(TestModBlocks.HOLOGRAM.get(), ItemStack.EMPTY.getItem());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // From tutorial:
        // https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.20.X/blob/12-datagen/src/main/java/net/kaupenjoe/tutorialmod/datagen/loot/ModBlockLootTables.java
        // return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        // RegistryObject doesn't exist for 1.20.4, so we'll use DeferredBlock instead?
        // TODO: I don't even know if this is necessary.
        return TestModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get)
                .collect(Collectors.toList());
    }
}
