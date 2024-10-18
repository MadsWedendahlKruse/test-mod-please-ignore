package mwk.testmod.client.utils;

import net.minecraft.network.chat.Component;

public class TooltipUtils {

    public static Component getKeybindComponent(String key) {
        return Component.literal("<").withColor(ColorUtils.TEXT_WHITE)
                .append(Component.literal(key).withColor(ColorUtils.TEXT_YELLOW))
                .append(Component.literal(">").withColor(ColorUtils.TEXT_WHITE));
    }

}
