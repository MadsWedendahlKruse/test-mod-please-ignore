package mwk.testmod.client.gui.widgets.buttons;

import mwk.testmod.TestMod;
import mwk.testmod.common.util.ColorUtils;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

/**
 * A button that can be toggled on and off. The button has a separate widget sprite for each state,
 * making it useful for buttons that need to visually change when toggled, e.g. a play button.
 */
public class OnOffButton extends ImageButton {

    private boolean on;
    private WidgetSprites onSprites;
    private WidgetSprites offSprites;
    private OnOffButton.OnPress onPressExtra;
    private String tooltipKey;

    protected static final Font font = Minecraft.getInstance().font;

    public OnOffButton(int size, OnOffButton.OnPress onPressExtra, String onName, String offName) {
        this(0, 0, size, size, onPressExtra, onName, offName, null);
    }

    public OnOffButton(int size, OnOffButton.OnPress onPressExtra, String onName, String offName,
            String tooltipKey) {
        this(0, 0, size, size, onPressExtra, onName, offName, tooltipKey);
    }

    public OnOffButton(int width, int height, OnOffButton.OnPress onPressExtra, String onName,
            String offName) {
        this(0, 0, width, height, onPressExtra, onName, offName, null);
    }

    public OnOffButton(int width, int height, OnOffButton.OnPress onPressExtra, String onName,
            String offName, String tooltipKey) {
        this(0, 0, width, height, onPressExtra, onName, offName, tooltipKey);
    }

    public OnOffButton(int x, int y, int width, int height, OnOffButton.OnPress onPressExtra,
            String onName, String offName, String tooltipKey) {
        this(x, y, width, height, onPressExtra,
                new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/" + onName),
                        new ResourceLocation(TestMod.MODID, "widget/" + onName + "_highlighted")),
                new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/" + offName),
                        new ResourceLocation(TestMod.MODID, "widget/" + offName + "_highlighted")),
                tooltipKey);
    }

    public OnOffButton(int x, int y, int width, int height, OnOffButton.OnPress onPressExtra,
            WidgetSprites onSprites, WidgetSprites offSprites, String tooltipKey) {
        super(x, y, width, height, offSprites, (pButton) -> {
            ((OnOffButton) pButton).on = !((OnOffButton) pButton).on;
        });
        this.on = false;
        this.onSprites = onSprites;
        this.offSprites = offSprites;
        this.onPressExtra = onPressExtra;
        this.tooltipKey = tooltipKey;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void toggle() {
        on = !on;
    }

    public void setOnPressedExtra(OnOffButton.OnPress onPressExtra) {
        this.onPressExtra = onPressExtra;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
        if (onPressExtra != null) {
            onPressExtra.onPress(this);
        }
    }

    private Component getHoverText() {
        if (tooltipKey != null) {
            MutableComponent onOffText;
            Style onOffStyle;
            if (on) {
                onOffText = Component.translatable(TestModLanguageProvider.KEY_WIDGET_TOOLTIP_ON);
                onOffStyle = Style.EMPTY.withColor(ColorUtils.TEXT_GREEN);
            } else {
                onOffText = Component.translatable(TestModLanguageProvider.KEY_WIDGET_TOOLTIP_OFF);
                onOffStyle = Style.EMPTY.withColor(ColorUtils.TEXT_RED);
            }
            return Component.translatable(tooltipKey, onOffText.withStyle(onOffStyle));
        }
        return null;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY,
            float pPartialTick) {
        WidgetSprites sprite = on ? onSprites : offSprites;
        ResourceLocation resourceLocation = sprite.get(isActive(), isHovered());
        pGuiGraphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width,
                this.height);
        if (isMouseOver(pMouseX, pMouseY)) {
            Component tooltip = getHoverText();
            if (tooltip != null) {
                pGuiGraphics.renderTooltip(font, tooltip, pMouseX, pMouseY);
            }
        }
    }

    public interface OnPress {
        void onPress(OnOffButton button);
    }
}
