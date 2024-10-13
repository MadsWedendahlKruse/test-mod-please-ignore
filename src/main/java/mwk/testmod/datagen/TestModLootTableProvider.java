package mwk.testmod.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import mwk.testmod.datagen.loot.TestModBlockLootTables;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TestModLootTableProvider {

    public static LootTableProvider create(PackOutput output,
            CompletableFuture<Provider> provider) {
        return new LootTableProvider(output,
                Set.of(),
                List.of(new SubProviderEntry(TestModBlockLootTables::new,
                        LootContextParamSets.BLOCK)),
                provider);
    }
}
