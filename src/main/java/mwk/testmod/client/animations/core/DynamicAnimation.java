package mwk.testmod.client.animations.core;

/**
 * A class for creating animations between two values that can be updated dynamically.
 */
public abstract class DynamicAnimation<T> extends Animation<T> {

    /**
     * The speed of the animation. This is the distance the animation's current value moves towards
     * the target value per second.
     */
    protected float speed;

    /**
     * Creates a new animation with a speed of 1.0. See
     * {@link #DynamicAnimation(float, Object, Object)}.
     */
    protected DynamicAnimation(float speed) {
        this(speed, null, null);
    }

    /**
     * Creates a new animation with the given speed and start and target values.
     * 
     * @param speed The speed of the animation. Must be greater than 0. TODO: Must it be greater
     *        than 0?
     * @param start The start value of the animation.
     * @param target The target value of the animation.
     */
    protected DynamicAnimation(float speed, T start, T target) {
        super(start, target);
        this.speed = speed;
    }

    /**
     * TODO: could also be in Animation<T>
     * 
     * @return True if the start value has been set, false otherwise.
     */
    public boolean startValueSet() {
        return startValue != null;
    }

    /**
     * TODO: could also be in Animation<T>
     * 
     * @return True if the target value has been set, false otherwise.
     */
    public boolean targetValueSet() {
        return targetValue != null;
    }

    /**
     * @param t1 Usually the current value of the animation.
     * @param t2 Usually the target value of the animation.
     * @return The distance between t1 and t2.
     */
    protected abstract float distance(T t1, T t2);

    /**
     * @return The direction from t1 to t2. This is the direction that t1 should move towards t2.
     */
    protected abstract T direction(T t1, T t2);

    /**
     * @return The default value for the animation. This is the value that will be returned if the
     *         start or target value is not set.
     */
    protected abstract T defaultValue();

    protected boolean compare(T t1, T t2) {
        return t1.equals(t2);
    }

    @Override
    public T getValue() {
        if (!startValueSet()) {
            if (targetValueSet()) {
                return targetValue;
            }
            return defaultValue();
        }
        if (!targetValueSet()) {
            return startValue;
        }
        if (!enabled || isFinished()) {
            return targetValue;
        }
        T moveDirection = direction(currentValue, targetValue);
        float moveDistance =
                Math.min(speed * lastDeltaTime, 1) * distance(currentValue, targetValue);
        T move = multiply(moveDirection, moveDistance);
        currentValue = add(currentValue, move);
        return currentValue;
    }
}
