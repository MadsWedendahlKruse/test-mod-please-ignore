package mwk.testmod.client.animations;

import mwk.testmod.client.animations.base.DynamicAnimation;

public class DynamicAnimationFloat extends DynamicAnimation<Float> {

    public DynamicAnimationFloat(float speed) {
        super(speed);
    }

    public DynamicAnimationFloat(float speed, float start, float target) {
        super(speed, start, target);
    }

    @Override
    protected float distance(Float t1, Float t2) {
        return Math.abs(t2 - t1);
    }

    @Override
    protected Float direction(Float t1, Float t2) {
        return Math.signum(t2 - t1);
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

    @Override
    protected Float defaultValue() {
        return 0.0F;
    }

    @Override
    protected boolean compare(Float t1, Float t2) {
        return Float.compare(t1, t2) == 0;
    }
}
