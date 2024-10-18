package mwk.testmod.client.utils;

public class ColorUtils {

    public static final int TEXT_BLACK = getTextColor(0, 0, 0);
    public static final int TEXT_WHITE = getTextColor(255, 255, 255);
    public static final int TEXT_GRAY = getTextColor(170, 170, 170);
    public static final int TEXT_RED = getTextColor(255, 0, 0);
    public static final int TEXT_GREEN = getTextColor(0, 255, 0);
    public static final int TEXT_BLUE = getTextColor(0, 0, 255);
    public static final int TEXT_YELLOW = getTextColor(255, 255, 0);
    public static final int TEXT_CYAN = getTextColor(0, 255, 255);
    public static final int TEXT_MAGENTA = getTextColor(255, 0, 255);

    public static int getTextColor(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
}
