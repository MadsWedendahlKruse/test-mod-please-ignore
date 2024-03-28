package mwk.testmod.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TestModTags {

    public static class Items {

        public static final TagKey<Item> COAL_DUST = forgeDustTag("coal");
        public static final TagKey<Item> IRON_DUST = forgeDustTag("iron");
        public static final TagKey<Item> STEEL_DUST = forgeDustTag("steel");
        public static final TagKey<Item> STEEL_INGOT = forgeIngotTag("steel");

        private static TagKey<Item> forgeTag(String type, String material) {
            return ItemTags.create(new ResourceLocation("forge", type + "/" + material));
        }

        private static TagKey<Item> forgeDustTag(String material) {
            return forgeTag("dusts", material);
        }

        private static TagKey<Item> forgeIngotTag(String material) {
            return forgeTag("ingots", material);
        }
    }
}
