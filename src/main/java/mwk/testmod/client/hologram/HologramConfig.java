package mwk.testmod.client.hologram;

import org.joml.Quaternionf;

public class HologramConfig {
        public static final Quaternionf ROTATE_Z_180 = new Quaternionf(0.0F, 0.0F,
                        (float) Math.sin(Math.PI / 2.0F), (float) Math.cos(Math.PI / 2.0F));
        public static final Quaternionf ROTATE_Y_180 = new Quaternionf(0.0F,
                        (float) Math.sin(Math.PI / 2.0F), 0.0F, (float) Math.cos(Math.PI / 2.0F));

        public static final float HOLOGRAM_ANIMATION_DURATION = 0.15F;

        public static final float[] WHITE = new float[] {1.0F, 1.0F, 1.0F};
        public static final float[] CYAN = new float[] {0.2F, 1.0F, 1.0F};
        public static final float[] RED = new float[] {1.0F, 0.0F, 0.0F};
        public static final float[] GREEN = new float[] {0.0F, 1.0F, 0.0F};
        public static final float[] YELLOW = new float[] {1.0F, 1.0F, 0.0F};
        public static final int TEXT_WHITE = 16777215;

        // TOOD: What does this number actually mean?
        public static final int PACKED_LIGHT_COORDS = 15728880;

        private HologramConfig() {}
}
