package mwk.testmod.client.animations.base;

/**
 * A class for creating animations that run indefinitely. This is useful for creating animations
 * that should keep running until manually stopped, e.g. rotating a model.
 */
public abstract class PerpetualAnimation<T> extends Animation<T> {

    /**
     * The amount the animation's current value increases per second.
     */
    protected T rate;

    /**
     * Creates a new animation with the given rate.
     */
    protected PerpetualAnimation(T rate) {
        super(null, null);
        this.rate = rate;
    }

    @Override
    public final T getValue() {
        return multiply(rate, elapsedTime);
    }

    @Override
    protected final T add(T t1, T t2) {
        // Not used
        return null;
    }

    @Override
    protected final T subtract(T t1, T t2) {
        // Not used
        return null;
    }
}
