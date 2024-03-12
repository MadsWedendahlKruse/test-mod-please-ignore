package mwk.testmod.client.gui.widgets.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;

public class PanelManager {

    private enum Side {
        LEFT, RIGHT
    }

    private Map<Side, ArrayList<MachinePanel>> panels =
            new HashMap<Side, ArrayList<MachinePanel>>();
    private Map<Side, MachinePanel> activePanels = new HashMap<Side, MachinePanel>();

    private int menuLeft;
    private int menuTop;
    private int menuWidth;
    private int menuHeight;
    private int leftOffset;
    private int topOffset;

    public PanelManager(int menuLeft, int menuTop, int menuWidth, int menuHeight, int leftOffset,
            int topOffset) {
        this.menuLeft = menuLeft;
        this.menuTop = menuTop;
        this.menuWidth = menuWidth;
        this.menuHeight = menuHeight;
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
        for (Side side : Side.values()) {
            panels.put(side, new ArrayList<MachinePanel>());
        }
    }

    public void addPanel(MachinePanel panel) {
        // TODO: Find a better way to decide which side to put the panel on
        if (panels.get(Side.LEFT).size() <= panels.get(Side.RIGHT).size()) {
            panels.get(Side.LEFT).add(panel);
        } else {
            panels.get(Side.RIGHT).add(panel);
        }
    }

    public int getX(Side side) {
        int x = menuLeft + leftOffset;
        if (side == Side.RIGHT) {
            x += menuWidth;
        }
        return x;
    }

    public void renderSide(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
            Side side) {
        int y = menuTop + topOffset;
        ArrayList<MachinePanel> panels = this.panels.get(side);
        if (activePanels.get(side) != null && !activePanels.get(side).isOpen()) {
            activePanels.put(side, null);
        }
        for (MachinePanel panel : panels) {
            if (panel.isOpen() && activePanels.get(side) != panel) {
                if (activePanels.get(side) != null) {
                    activePanels.get(side).close();
                }
                activePanels.put(side, panel);
            }
            panel.setPosition(getX(side), y, side == Side.LEFT);
            panel.render(guiGraphics, mouseX, mouseY, partialTick);
            y += panel.getHeight();
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Side side : Side.values()) {
            renderSide(guiGraphics, mouseX, mouseY, partialTick, side);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (Side side : Side.values()) {
            for (MachinePanel panel : panels.get(side)) {
                panel.mouseClicked(mouseX, mouseY, button);
            }
        }
    }
}
