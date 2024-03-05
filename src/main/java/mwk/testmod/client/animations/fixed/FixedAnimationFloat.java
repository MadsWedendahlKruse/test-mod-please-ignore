package mwk.testmod.client.animations.fixed;

import mwk.testmod.client.animations.core.FixedAnimation;

public class FixedAnimationFloat extends FixedAnimation<Float> {

    /**
     * Creates a new animation with a start value of 0, a target value of 1. See
     * {@link #FixedAnimation(float, Function, float, float)}.
     */
    public FixedAnimationFloat(float duration, Function function) {
        super(duration, function, 0.0F, 1.0F);
    }

    /**
     * Creates a new animation with the given duration, type and start and target values.
     * 
     * @param duration The duration of the animation in seconds. Must be greater than 0.
     * @param function The function to use for interpolating the animation's progress.
     * @param start The start value of the animation.
     * @param target The target value of the animation.
     * @throws IllegalArgumentException If the duration is less than or equal to 0.
     */
    public FixedAnimationFloat(float duration, Function function, float start, float target) {
        super(duration, function, start, target);
    }

    @Override
    protected Float add(Float t1, Float t2) {
        return t1 + t2;
    }

    @Override
    protected Float subtract(Float t1, Float t2) {
        return t1 - t2;
    }

    @Override
    protected Float multiply(Float t, float f) {
        return t * f;
    }
}
