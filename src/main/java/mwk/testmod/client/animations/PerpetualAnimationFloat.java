package mwk.testmod.client.animations;

import mwk.testmod.client.animations.base.PerpetualAnimation;

public class PerpetualAnimationFloat extends PerpetualAnimation<Float> {

    public PerpetualAnimationFloat(float speed) {
        super(speed);
    }

    @Override
    protected Float multiply(Float t, float factor) {
        return t * factor;
    }
}
