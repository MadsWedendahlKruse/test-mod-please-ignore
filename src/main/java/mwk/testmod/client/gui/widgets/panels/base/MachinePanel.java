package mwk.testmod.client.gui.widgets.panels.base;

import mwk.testmod.TestMod;
import mwk.testmod.client.animations.FixedAnimationFloat;
import mwk.testmod.client.animations.base.FixedAnimation.Function;
import mwk.testmod.common.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A base class for panel widgets to supplement the GUI of a machine. Typically, they would be
 * placed on the sides of the GUI and fold out when the player clicks on them. This could be e.g. a
 * panel where the player can insert upgrades into the machine.
 */
public abstract class MachinePanel extends AbstractWidget {

    public static final ResourceLocation SPRITE_LEFT =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/panel_left");
    public static final ResourceLocation SPRITE_RIGHT =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/panel_right");

    public static final int DEFAULT_ICON_WIDTH = 16;
    public static final int DEFAULT_ICON_HEIGHT = 16;
    public static final int DEFAULT_ICON_PADDING = 5;

    protected static final Font font = Minecraft.getInstance().font;
    public static final int LINE_SPACING = 1;
    public static final int LINE_HEIGHT = font.lineHeight + LINE_SPACING;

    public static final float ANIMATION_WIDTH_DURATION = 0.2f;
    public static final float ANIMATION_HEIGHT_DURATION = 0.2f;

    private boolean open = false;
    private final int widthOpen;
    private final int heightOpen;
    private final int widthClosed;
    private final int heightClosed;
    // Animations for the panel opening and closing
    private final FixedAnimationFloat animationWidth;
    private final FixedAnimationFloat animationHeight;
    private PanelSide side;
    private int initialX;
    private final float[] color;
    private final ResourceLocation icon;
    private final int iconWidth;
    private final int iconHeight;
    private final int iconPaddingX;
    private final int iconPaddingY;
    // Top left corner of the panel in screen coordinates
    protected int screenX;
    protected int screenY;

    public MachinePanel(int widthOpen, int heightOpen, Component message, float[] color,
            ResourceLocation icon) {
        this(widthOpen, heightOpen, message, color, icon, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT,
                DEFAULT_ICON_PADDING, DEFAULT_ICON_PADDING);
    }

    public MachinePanel(int widthOpen, int heightOpen, Component message, float[] color,
            ResourceLocation icon, int iconPaddingX, int iconPaddingY) {
        this(widthOpen, heightOpen, message, color, icon, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT,
                iconPaddingX, iconPaddingY);
    }

    public MachinePanel(int widthOpen, int heightOpen, Component message, float[] color,
            ResourceLocation icon, int iconWidth, int iconHeight, int iconPaddingX,
            int iconPaddingY) {
        this(iconWidth + 2 * iconPaddingX, iconHeight + 2 * iconPaddingY,
                2 * iconPaddingX + widthOpen, iconHeight + 3 * iconPaddingY + heightOpen, message,
                color, icon, iconWidth, iconHeight, iconPaddingX, iconPaddingY);
    }

    public MachinePanel(int widthClosed, int heightClosed, int widthOpen, int heightOpen,
            Component message, float[] color, ResourceLocation icon, int iconWidth, int iconHeight,
            int iconPaddingX, int iconPaddingY) {
        super(0, 0, widthClosed, heightClosed, message);
        // Make sure there's room for the message
        this.widthOpen =
                Math.max(widthOpen, font.width(message.getString()) + iconWidth + 4 * iconPaddingX);
        this.heightOpen = heightOpen;
        this.widthClosed = widthClosed;
        this.heightClosed = heightClosed;
        animationWidth = new FixedAnimationFloat(ANIMATION_WIDTH_DURATION, Function.EASE_OUT_CUBIC);
        animationWidth.setStartAndTarget((float) widthClosed, (float) widthClosed);
        animationHeight =
                new FixedAnimationFloat(ANIMATION_HEIGHT_DURATION, Function.EASE_OUT_CUBIC);
        animationHeight.setStartAndTarget((float) heightClosed, (float) heightClosed);
        this.icon = icon;
        this.color = color;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.iconPaddingX = iconPaddingX;
        this.iconPaddingY = iconPaddingY;
    }

    public void setPosition(int x, int y, PanelSide side) {
        switch (side) {
            case LEFT -> super.setPosition(x - widthClosed, y);
            case RIGHT -> super.setPosition(x, y);
        }
        initialX = getX();
        this.side = side;
    }

    public void setScreenPosition(int x, int y) {
        screenX = x;
        screenY = y;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isOpenFully() {
        return isOpen() && animationWidth.isFinished() && animationHeight.isFinished();
    }

    public void close() {
        open = false;
        animationWidth.setStartAndTarget((float) widthOpen, (float) widthClosed);
        animationHeight.setStartAndTarget((float) heightOpen, (float) heightClosed);
        if (animationWidth.isFinished()) {
            animationWidth.start();
        }
        if (animationHeight.isFinished()) {
            animationHeight.start();
        }
    }

    public int getHeightOpen() {
        return heightOpen;
    }

    public int getWidthOpen() {
        return widthOpen;
    }

    /**
     * This method is responisble for rendering something unique to the panel when it's open. This
     * could be e.g. a list of upgrades that the player can insert into the machine, or stats about
     * the power consumption/generation of the machine.
     */
    protected abstract void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick);

    /**
     * @return The x coordinate of the top left corner of the area that becomes visible when the
     * panel is open. This is the recommended point to start rendering the content of the open panel
     * from.
     */
    protected int getOpenLeft() {
        return getX() + iconPaddingX;
    }

    /**
     * @return The y coordinate of the top left corner of the area that becomes visible when the
     * panel is open. This is the recommended point to start rendering the content of the open panel
     * from.
     */
    protected int getOpenTop() {
        return getY() + 2 * iconPaddingY + iconHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            animationWidth.start();
            animationHeight.start();
            open = !open;
            if (open) {
                // When opening the menu we can delay the height animation until the width animation
                // is finished. This looks cool :)
                animationHeight.pause();
                animationWidth.setStartAndTarget((float) widthClosed, (float) widthOpen);
                animationHeight.setStartAndTarget((float) heightClosed, (float) heightOpen);
            } else {
                animationWidth.setStartAndTarget((float) widthOpen, (float) widthClosed);
                animationHeight.setStartAndTarget((float) heightOpen, (float) heightClosed);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        animationWidth.update();
        if (animationWidth.isFinished() && animationHeight.isPaused()) {
            animationHeight.resume();
        }
        animationHeight.update();
        setWidth(animationWidth.getValue().intValue());
        setHeight(animationHeight.getValue().intValue());
        if (side == PanelSide.LEFT) {
            setX(initialX - (getWidth() - widthClosed));
        }
        ResourceLocation sprite = switch (side) {
            case LEFT -> SPRITE_LEFT;
            case RIGHT -> SPRITE_RIGHT;
        };
        guiGraphics.setColor(color[0], color[1], color[2], color[3]);
        guiGraphics.blitSprite(sprite, getX(), getY(), width, height);
        guiGraphics.setColor(1, 1, 1, 1);
        guiGraphics.blitSprite(icon, getX() + iconPaddingX, getY() + iconPaddingY, iconWidth,
                iconHeight);
        if (!open && isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, getMessage(), mouseX, mouseY);
        }
        if (open && animationWidth.isFinished()) {
            guiGraphics.drawString(font, getMessage().getString(),
                    getX() + iconWidth + 2 * iconPaddingX, getY() + iconPaddingY + 4,
                    ColorUtils.TEXT_WHITE);
        }
        // Only render open if the animations are finished
        // TODO: Reveal it gradually? Would be cool, but not sure if it's worth the effort
        if (isOpenFully()) {
            renderOpen(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }
}
