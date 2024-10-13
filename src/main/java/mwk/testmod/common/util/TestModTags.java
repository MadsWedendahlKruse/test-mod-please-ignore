package mwk.testmod.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TestModTags {

    public static class Items {

        // TODO: Do I need to make these myself?
        public static final TagKey<Item> FORGE_RAW_MATERIALS = forgeRawMaterialsTag("");
        public static final TagKey<Item> FORGE_DUSTS = forgeDustsTag("");
        public static final TagKey<Item> FORGE_INGOTS = forgeIngotsTag("");
        public static final TagKey<Item> FORGE_ORES = forgeTag("ores", "");
        public static final TagKey<Item> FORGE_ORES_IN_GROUND_STONE =
                forgeTag("ores_in_ground", "stone");
        public static final TagKey<Item> FORGE_ORES_IN_GROUND_DEEPSLATE =
                forgeTag("ores_in_ground", "deepslate");
        public static final TagKey<Item> FORGE_ORE_RATES_DENSE = forgeTag("ore_rates", "dense");
        public static final TagKey<Item> FORGE_ORE_RATES_SINGULAR =
                forgeTag("ore_rates", "singular");
        public static final TagKey<Item> FORGE_ORE_RATES_SPARSE = forgeTag("ore_rates", "sparse");
        public static final TagKey<Item> FORGE_STORAGE_BLOCKS = forgeTag("storage_blocks", "");

        public static final TagKey<Item> ILMENITE_ORE = forgeTag("ores", "ilmenite");

        public static final TagKey<Item> RAW_ILMENITE = forgeRawMaterialsTag("ilmenite");

        public static final TagKey<Item> COAL_DUST = forgeDustsTag("coal");
        public static final TagKey<Item> IRON_DUST = forgeDustsTag("iron");
        public static final TagKey<Item> STEEL_DUST = forgeDustsTag("steel");
        public static final TagKey<Item> ILMENITE_DUST = forgeDustsTag("ilmenite");
        public static final TagKey<Item> TITANIUM_DUST = forgeDustsTag("titanium");

        public static final TagKey<Item> STEEL_INGOT = forgeIngotsTag("steel");
        public static final TagKey<Item> TITANIUM_INGOT = forgeIngotsTag("titanium");

        private static TagKey<Item> forgeTag(String type, String material) {
            String path = type;
            if (!material.isEmpty()) {
                path += "/" + material;
            }
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
        }

        private static TagKey<Item> forgeRawMaterialsTag(String material) {
            return forgeTag("raw_materials", material);
        }

        private static TagKey<Item> forgeDustsTag(String material) {
            return forgeTag("dusts", material);
        }

        private static TagKey<Item> forgeIngotsTag(String material) {
            return forgeTag("ingots", material);
        }
    }

    public static class Blocks {

        public static final TagKey<Block> FORGE_ORES = forgeTag("ores", "");
        public static final TagKey<Block> FORGE_ORES_IN_GROUND_STONE =
                forgeTag("ores_in_ground", "stone");
        public static final TagKey<Block> FORGE_ORES_IN_GROUND_DEEPSLATE =
                forgeTag("ores_in_ground", "deepslate");
        public static final TagKey<Block> FORGE_ORE_RATES_DENSE = forgeTag("ore_rates", "dense");
        public static final TagKey<Block> FORGE_ORE_RATES_SINGULAR =
                forgeTag("ore_rates", "singular");
        public static final TagKey<Block> FORGE_ORE_RATES_SPARSE = forgeTag("ore_rates", "sparse");
        public static final TagKey<Block> FORGE_STORAGE_BLOCKS = forgeTag("storage_blocks", "");

        public static final TagKey<Block> ILMENITE_ORE = forgeOresTag("ilmenite");

        private static TagKey<Block> forgeTag(String type, String material) {
            String path = type;
            if (!material.isEmpty()) {
                path += "/" + material;
            }
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
        }

        private static TagKey<Block> forgeOresTag(String material) {
            return forgeTag("ores", material);
        }
    }
}
