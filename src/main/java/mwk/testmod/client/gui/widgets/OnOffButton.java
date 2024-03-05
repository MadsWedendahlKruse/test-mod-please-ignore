package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A button that can be toggled on and off. The button has a separate widget sprite for each state,
 * making it useful for buttons that need to visually change when toggled, e.g. a play button.
 */
@OnlyIn(Dist.CLIENT)
public class OnOffButton extends ImageButton {

    private boolean on;
    private WidgetSprites onSprites;
    private WidgetSprites offSprites;
    private OnOffButton.OnPress onPressExtra;

    public OnOffButton(int size, OnOffButton.OnPress onPressExtra, String onName, String offName) {
        this(0, 0, size, size, onPressExtra, onName, offName);
    }

    public OnOffButton(int width, int height, OnOffButton.OnPress onPressExtra, String onName,
            String offName) {
        this(0, 0, width, height, onPressExtra, onName, offName);
    }

    public OnOffButton(int x, int y, int width, int height, OnOffButton.OnPress onPressExtra,
            String onName, String offName) {
        this(x, y, width, height, onPressExtra,
                new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/" + onName),
                        new ResourceLocation(TestMod.MODID, "widget/" + onName + "_highlighted")),
                new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/" + offName),
                        new ResourceLocation(TestMod.MODID, "widget/" + offName + "_highlighted")));
    }

    public OnOffButton(int x, int y, int width, int height, OnOffButton.OnPress onPressExtra,
            WidgetSprites onSprites, WidgetSprites offSprites) {
        super(x, y, width, height, offSprites, (pButton) -> {
            ((OnOffButton) pButton).on = !((OnOffButton) pButton).on;
        });
        this.on = false;
        this.onSprites = onSprites;
        this.offSprites = offSprites;
        this.onPressExtra = onPressExtra;
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

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY,
            float pPartialTick) {
        WidgetSprites sprite = on ? onSprites : offSprites;
        ResourceLocation resourceLocation = sprite.get(isActive(), isHovered());
        pGuiGraphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width,
                this.height);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(OnOffButton button);
    }
}
