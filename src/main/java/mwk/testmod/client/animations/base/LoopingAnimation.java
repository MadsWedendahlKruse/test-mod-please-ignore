package mwk.testmod.client.animations.base;

/**
 * A class for creating animations that loop, i.e. start over when they reach the end.
 */
public abstract class LoopingAnimation<T> extends FixedAnimation<T> {

    protected LoopingAnimation(float duration, Function function, T start, T target) {
        super(duration, function, start, target);
    }

    @Override
    public T getValue() {
        if (isFinished()) {
            elapsedTime = 0;
            finished = false;
        }
        T superValue = super.getValue();
        return superValue;
    }

}
