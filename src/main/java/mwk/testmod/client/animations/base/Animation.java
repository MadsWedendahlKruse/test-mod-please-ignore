package mwk.testmod.client.animations.base;

import mwk.testmod.client.animations.AnimationClock;

/**
 * Abstract class for creating different types of animations. This is useful for creating smooth
 * transitions between states, e.g. moving a model or a button. The type T is the type of the
 * animation's progress, e.g. a float for a linear animation or a vector for a path animation.
 */
public abstract class Animation<T> {

    /**
     * The start value of the animation.
     */
    protected T startValue;
    /**
     * The target value of the animation.
     */
    protected T targetValue;
    /**
     * The current value of the animation. This is the value that changes over time and is used to
     * update the state of the animated object. This is usually somewhere between the start and
     * target values.
     */
    protected T currentValue;
    /**
     * The elapsed time of the animation in seconds.
     */
    protected float elapsedTime;
    /**
     * The duration of the animation in seconds. If the duration is less than or equal to 0, the
     * animation will keep running until manually stopped.
     */
    protected float duration;
    /**
     * The time difference between the last two updates in seconds. This is used to calculate the
     * progress of the animation and advance the state of the animated object.
     */
    protected float lastDeltaTime;
    /**
     * Whether the animation is enabled. When disabled, the animation will not update or start. This
     * is useful for creating animations that can be toggled on and off. The animation is enabled by
     * default.
     */
    protected boolean enabled = true;
    /**
     * Whether the animation is paused. When paused, the animation will not update. This is useful
     * for creating animations that can be paused and resumed. The animation is not paused by
     * default.
     */
    protected boolean paused = false;
    /**
     * Whether the animation has finished. When finished, the animation will not update. This is
     * useful for creating animations that have a finite duration. The animation is finished by
     * default.
     */
    protected boolean finished = true;

    /**
     * Creates a new animation with the given start and target values.
     *
     * @param start  The start value of the animation.
     * @param target The target value of the animation.
     */
    protected Animation(T start, T target) {
        setStartValue(start);
        setTargetValue(target);
    }

    /**
     * Starts the animation, resetting the elapsed time and marking the start timestamp. Does
     * nothing if the animation is disabled.
     */
    public void start() {
        if (!enabled) {
            return;
        }
        elapsedTime = 0.0F;
        finished = false;
    }

    /**
     * Stops the animation, marking it as finished. Does nothing if the animation is disabled.
     */
    public void stop() {
        if (!enabled) {
            return;
        }
        finished = true;
    }

    /**
     * Pauses the animation, preventing it from updating. Does nothing if the animation is disabled
     * or already paused.
     */
    public void pause() {
        if (!enabled || paused) {
            return;
        }
        paused = true;
    }

    /**
     * Resumes the animation, allowing it to update. Does nothing if the animation is disabled or
     * not paused. This sets last tick time to the current time.
     */
    public void resume() {
        if (!enabled || !paused) {
            return;
        }
        paused = false;
    }

    /**
     * Check if the animation is paused.
     *
     * @return true if the animation is paused, false otherwise.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Updates the animation's state, advancing the elapsed time. No effect if the animation is
     * disabled or already finished.
     *
     * @param deltaTime The time difference between the last two updates in seconds.
     * @param reverse   True to reverse the elapsed time, false to advance it.
     * @return The remaining time of the animation in seconds. This is primarily used by
     * {@link KeyframeManager} to allow smooth transitions between animations.
     */
    public float update(float deltaTime, boolean reverse) {
        if (!enabled || finished || paused) {
            return 0.0F;
        }
        lastDeltaTime = deltaTime;
        elapsedTime += (reverse ? -1 : 1) * deltaTime;
        return duration - elapsedTime;
    }

    /**
     * see {@link #update(float, boolean)}
     */
    public float update() {
        return update(AnimationClock.getInstance().getDeltaTime(), false);
    }

    /**
     * see {@link #update(float, boolean)}
     */
    public float update(boolean reverse) {
        return update(AnimationClock.getInstance().getDeltaTime(), reverse);
    }

    /**
     * see {@link #update(float, boolean)}
     */
    public float update(float deltaTime) {
        return update(deltaTime, false);
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
        if (duration > 0) {
            finished = elapsedTime >= duration;
        }
        return finished;
    }

    /**
     * Enables or disables the animation. When disabled, the animation will not update or start.
     *
     * @param enabled True to enable the animation, false to disable it.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the start value of the animation. This is the value that the animation will transition
     * from over time.
     */
    public void setStartValue(T startValue) {
        this.startValue = startValue;
        this.currentValue = startValue;
    }

    /**
     * Sets the target value of the animation. This is the value that the animation will transition
     * to over time.
     */
    public void setTargetValue(T targetValue) {
        this.targetValue = targetValue;
    }

    /**
     * Sets both the start and target values of the animation.
     */
    public void setStartAndTarget(T startValue, T targetValue) {
        setStartValue(startValue);
        setTargetValue(targetValue);
    }

    /**
     * Reset the animation to its initial state. This sets the start and target values to null and
     * the elapsed time to 0.
     */
    public void reset() {
        setStartValue(null);
        setTargetValue(null);
        elapsedTime = 0.0F;
    }

    /**
     * @return The result of adding t1 and t2.
     */
    protected abstract T add(T t1, T t2);

    /**
     * @return The result of subtracting t2 from t1.
     */
    protected abstract T subtract(T t1, T t2);

    /**
     * @return The result of multiplying t by f.
     */
    protected abstract T multiply(T t, float f);

    /**
     * Get the value of the animation. This is the value that changes over time and is used to
     * update the state of the animated object. The type T is the type of the animation's value,
     * e.g. a float for a linear animation or a vector for a path animation.
     * <p>
     * Any subclass is responsible for checking if the animation is enabled or finished and
     * returning the correct value.
     *
     * @return the value of the animation
     */
    public abstract T getValue();

    /**
     * @return The start value of the animation.
     */
    public T getStartValue() {
        return startValue;
    }

    /**
     * @return The target value of the animation.
     */
    public T getTargetValue() {
        return targetValue;
    }
}
