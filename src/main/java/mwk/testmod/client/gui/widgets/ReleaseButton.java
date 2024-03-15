package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A button that calls a method when released.
 */
public class ReleaseButton extends Button {

    private OnRelease onRelease;
    private boolean heldDown = false;
    private WidgetSprites sprite;

    public ReleaseButton(int size, Button.OnPress onPress, ReleaseButton.OnRelease onRelease,
            String name) {
        this(size, size, onPress, onRelease, name);
    }

    public ReleaseButton(int width, int height, Button.OnPress onPress,
            ReleaseButton.OnRelease onRelease, String name) {
        this(0, 0, width, height, onPress, onRelease, name);
    }

    public ReleaseButton(int x, int y, int width, int height, Button.OnPress onPress,
            ReleaseButton.OnRelease onRelease, String name) {
        this(x, y, width, height, CommonComponents.EMPTY, onPress, onRelease, name);
    }

    public ReleaseButton(int x, int y, int width, int height, Component message,
            Button.OnPress onPress, ReleaseButton.OnRelease onRelease, String name) {
        this(x, y, width, height, message, onPress, onRelease, DEFAULT_NARRATION, name);
    }

    public ReleaseButton(int x, int y, int width, int height, Component message,
            Button.OnPress onPress, ReleaseButton.OnRelease onRelease,
            Button.CreateNarration createNarration, String name) {
        super(x, y, width, height, message, (button) -> {
            onPress.onPress((Button) button);
            ((ReleaseButton) button).heldDown = true;
        }, createNarration);
        this.onRelease = onRelease;
        this.sprite = new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/" + name),
                new ResourceLocation(TestMod.MODID, "widget/" + name + "_highlighted"));
    }

    public boolean isHeldDown() {
        return heldDown;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.onRelease.onRelease(this);
        this.heldDown = false;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY,
            float pPartialTick) {
        ResourceLocation resourceLocation = sprite.get(isActive(), isHovered());
        pGuiGraphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width,
                this.height);
    }

    public interface OnRelease {
        void onRelease(ReleaseButton button);
    }
}
