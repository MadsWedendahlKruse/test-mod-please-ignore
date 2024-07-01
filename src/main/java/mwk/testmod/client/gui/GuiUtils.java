package mwk.testmod.client.gui;

import java.util.Locale;
import com.ibm.icu.text.NumberFormat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GuiUtils {

    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    public static class GuiTextElement {
        public final Component title;
        public final Component text;

        public GuiTextElement(String titleKey, String textKey, Object... args) {
            this.title = Component.translatable(titleKey);
            this.text = Component.translatable(textKey, args);
        }
    }

    public static void renderTextElements(GuiGraphics guiGraphics, Font font,
            GuiTextElement[] elements, int x, int y, int titleColor, int textColor,
            int lineHeight) {
        for (int i = 0; i < elements.length; i++) {
            GuiTextElement element = elements[i];
            guiGraphics.drawString(font, element.title, x, y + (2 * i) * lineHeight, titleColor,
                    true);
            guiGraphics.drawString(font, element.text, x, y + (2 * i + 1) * lineHeight, textColor,
                    false);
        }
    }
}
