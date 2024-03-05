package mwk.testmod.client.animations.core;

/**
 * A class for creating animations between two values with a fixed duration and a fixed function. To
 * some extent this is just an easing function in disguise.
 */
public abstract class FixedAnimation<T> extends Animation<T> {

    /**
     * The function to use for interpolating the animation's progress.
     */
    public enum Function {
        LINEAR, EASE_IN_CUBIC
    }

    protected Function function;

    /**
     * Creates a new animation with the given duration, function and start and target values.
     * 
     * @param duration The duration of the animation in seconds. Must be greater than 0.
     * @param function The function to use for interpolating the animation's progress.
     * @param start The start value of the animation.
     * @param target The target value of the animation.
     * @throws IllegalArgumentException If the duration is less than or equal to 0.
     */
    protected FixedAnimation(float duration, Function function, T start, T target) {
        super(start, target);
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        this.duration = duration;
        this.function = function;
    }

    @Override
    public T getValue() {
        if (!enabled || isFinished()) {
            return targetValue;
        }
        float progress = elapsedTime / duration;
        T diff = subtract(targetValue, startValue);
        currentValue = startValue;
        switch (function) {
            case EASE_IN_CUBIC:
                currentValue =
                        add(currentValue, multiply(diff, 1 - (float) Math.pow(1 - progress, 3)));
                break;
            case LINEAR:
            default:
                currentValue = add(currentValue, multiply(diff, progress));
                break;
        }
        return currentValue;
    }
}
