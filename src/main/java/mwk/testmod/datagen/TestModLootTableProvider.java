package mwk.testmod.datagen;

import java.util.List;
import java.util.Set;

import mwk.testmod.datagen.loot.TestModBlockLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TestModLootTableProvider {
    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(TestModBlockLootTables::new, LootContextParamSets.BLOCK)
        ));
    }
}
