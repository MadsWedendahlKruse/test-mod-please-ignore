package mwk.testmod.client.gui.widgets.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import mwk.testmod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * A button that is used to select a blueprint from a list of blueprints. The class is identical to
 * Button, except the sprite is different. TODO: Is there a better way to do this?
 */
public class BlueprintButton extends Button {

    public static final WidgetSprites SPRITES =
            new WidgetSprites(ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/button"),
                    ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                            "widget/button_highlighted"));

    public BlueprintButton(int pX, int pY, int pWidth, int pHeight, Component pMessage,
            Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, Button.DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY,
            float pPartialTick) {
        // Taken from Button#renderWidget
        Minecraft minecraft = Minecraft.getInstance();
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        pGuiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(),
                this.getY(), this.getWidth(), this.getHeight());
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = getFGColor();
        this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
