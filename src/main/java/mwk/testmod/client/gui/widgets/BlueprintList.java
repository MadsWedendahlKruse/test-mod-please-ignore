package mwk.testmod.client.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BlueprintList extends AbstractScrollWidget {

    public static final WidgetSprites BACKGROUND_SPRITES =
            new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/button"),
                    new ResourceLocation(TestMod.MODID, "widget/button_highlighted"));
    // Same value as in AbstractScrollWidget, except this one is public
    public static final int SCROLL_BAR_WIDTH = 8;
    public static final int DEFAULT_PADDING = 4;
    public static final int DEFAULT_SPACING = 2;

    private ArrayList<BlueprintButton> buttons = new ArrayList<BlueprintButton>();
    private ResourceKey<MultiBlockBlueprint> blueprintKey = null;
    private int padding;
    private int spacing;

    public BlueprintList(int x, int y, int width, int height, Component message) {
        this(x, y, width, height, DEFAULT_PADDING, DEFAULT_SPACING, message);
    }

    public BlueprintList(int x, int y, int width, int height, int padding, int spacing,
            Component message) {
        super(x, y, width - SCROLL_BAR_WIDTH, height, message);
        this.padding = padding;
        this.spacing = spacing;
        addBlueprintButton(
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_HOLOGRAM_PROJECTOR_NONE),
                null);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.level.registryAccess().lookup(TestModBlueprints.BLUEPRINT_REGISTRY_KEY)
                .ifPresent(blueprintRegistry -> {
                    blueprintRegistry.listElements().forEach(blueprint -> {
                        addBlueprintButton(Component.translatable(blueprint.value().getName()),
                                blueprint.key());
                    });
                });
    }

    private void addBlueprintButton(Component buttonLabel,
            ResourceKey<MultiBlockBlueprint> blueprintKey) {
        buttons.add(new BlueprintButton(getX() + padding / 2, getY() + getInnerHeight(),
                width - padding, Button.DEFAULT_HEIGHT, buttonLabel, (pButton) -> {
                    this.blueprintKey = blueprintKey;
                }));
    }

    public ResourceKey<MultiBlockBlueprint> getBlueprintKey() {
        return blueprintKey;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }

    @Override
    protected int getInnerHeight() {
        // Add one extra spacing because there's also one at the top
        return buttons.size() * (Button.DEFAULT_HEIGHT + spacing) + spacing;
    }

    @Override
    protected double scrollRate() {
        return 1;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        for (Button button : buttons) {
            button.render(guiGraphics, mouseX, mouseY + (int) scrollAmount(), partialTick);
        }
    }

    public boolean buttonClicked(double pMouseX, double pMouseY, int pButton) {
        for (Button button : buttons) {
            if (button.mouseClicked(pMouseX, pMouseY + scrollAmount(), pButton)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (Button button : buttons) {
            if (button.mouseClicked(pMouseX, pMouseY + scrollAmount(), pButton)) {
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (Button button : buttons) {
            if (button.isMouseOver(pMouseX, pMouseY + scrollAmount())) {
                button.onPress();
            }
        }
    }

    @Override
    protected void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight) {
        ResourceLocation resourceLocation = BACKGROUND_SPRITES.get(this.isActive(), false);
        pGuiGraphics.blitSprite(resourceLocation, pX, pY, pWidth, pHeight);
    }
}
