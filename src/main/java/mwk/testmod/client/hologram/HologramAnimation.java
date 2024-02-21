package mwk.testmod.client.hologram;

public class HologramAnimation {
    private long startTime; // Time when the animation started
    private float elapsedTime; // Time elapsed since the animation started
    private float duration; // Total duration of the animation
    private boolean enabled = true; // Whether the animation is enabled
    private boolean finished = false; // Whether the animation has finished

    /**
     * Constructs a HologramAnimation with a specified duration.
     * 
     * @param duration Duration of the animation in seconds.
     */
    public HologramAnimation(float duration) {
        this.duration = duration;
    }

    /**
     * Starts the animation, resetting the elapsed time and marking the start timestamp. Does
     * nothing if the animation is disabled.
     */
    public void start() {
        if (!enabled) {
            return;
        }
        startTime = System.currentTimeMillis();
        elapsedTime = 0.0F;
    }

    /**
     * Updates the animation's state, advancing the elapsed time. No effect if the animation is
     * disabled or already finished.
     */
    public void update() {
        if (!enabled || finished) {
            return;
        }
        elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0F;
    }

    /**
     * Checks if the animation has finished.
     * 
     * @return true if the animation is finished or not enabled, false otherwise.
     */
    public boolean isFinished() {
        if (!enabled || finished) {
            return true;
        }
        finished = elapsedTime >= duration;
        return finished;
    }

    /**
     * Gets the current progress of the animation.
     * 
     * @return A float representing the normalized progress (0.0 to 1.0).
     */
    public float getProgress() {
        if (!enabled || finished) {
            // Full progress if disabled or finished
            return 1.0F;
        }
        return Math.min(elapsedTime / duration, 1.0F);
    }

    /**
     * Enables or disables the animation. When disabled, the animation will not update or start.
     * 
     * @param enabled True to enable the animation, false to disable it.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

