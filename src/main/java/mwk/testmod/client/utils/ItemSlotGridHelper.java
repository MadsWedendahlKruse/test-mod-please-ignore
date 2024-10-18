package mwk.testmod.client.utils;

import java.util.ArrayList;
import java.util.List;

public class ItemSlotGridHelper {

    public record SlotPosition(int x, int y) {

    }

    public static final int SLOT_SIZE = 18;

    public static final ItemSlotGridHelper ROWS_1 = new ItemSlotGridHelper(1, SLOT_SIZE, SLOT_SIZE);
    public static final ItemSlotGridHelper ROWS_2 = new ItemSlotGridHelper(2, SLOT_SIZE, SLOT_SIZE);
    public static final ItemSlotGridHelper ROWS_3 = new ItemSlotGridHelper(3, SLOT_SIZE, SLOT_SIZE);

    private final int rows;
    private final int dx;
    private final int dy;

    public ItemSlotGridHelper(int rows, int dx, int dy) {
        this.rows = rows;
        this.dx = dx;
        this.dy = dy;
    }

    public int getX(int originX, int index) {
        return originX + (index / rows) * dx;
    }

    public int getY(int originY, int index) {
        return originY + (index % rows) * dy;
    }

    public SlotPosition getSlotPosition(int originX, int originY, int index) {
        return new SlotPosition(getX(originX, index), getY(originY, index));
    }

    public List<SlotPosition> getSlotPositions(int originX, int originY, int count) {
        List<SlotPosition> positions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            positions.add(getSlotPosition(originX, originY, i));
        }
        return positions;
    }

    public int getWidth(int count) {
        return dx * (int) Math.ceil((float) count / rows);
    }

    public int getHeight(int count) {
        return count >= rows ? dy * rows : dy * (count % rows);
    }
}
