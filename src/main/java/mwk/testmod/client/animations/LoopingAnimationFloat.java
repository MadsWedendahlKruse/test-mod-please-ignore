package mwk.testmod.client.animations;

import mwk.testmod.client.animations.base.LoopingAnimation;

public class LoopingAnimationFloat extends LoopingAnimation<Float> {

    public LoopingAnimationFloat(float duration, Function function, Float start, Float target) {
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
