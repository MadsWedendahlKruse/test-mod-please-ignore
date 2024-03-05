package mwk.testmod.client.animations.dynamic;

import mwk.testmod.client.animations.core.DynamicAnimation;
import net.minecraft.world.phys.Vec3;

public class DynamicAnimationVector extends DynamicAnimation<Vec3> {

    public DynamicAnimationVector(float speed) {
        super(speed);
    }

    public DynamicAnimationVector(float speed, Vec3 start, Vec3 target) {
        super(speed, start, target);
    }

    @Override
    protected float distance(Vec3 t1, Vec3 t2) {
        return (float) t1.distanceTo(t2);
    }

    @Override
    protected Vec3 direction(Vec3 t1, Vec3 t2) {
        return t2.subtract(t1).normalize();
    }

    @Override
    protected Vec3 add(Vec3 t1, Vec3 t2) {
        return t1.add(t2);
    }

    @Override
    protected Vec3 subtract(Vec3 t1, Vec3 t2) {
        return t1.subtract(t2);
    }

    @Override
    protected Vec3 multiply(Vec3 t, float f) {
        return t.scale(f);
    }

    @Override
    protected Vec3 defaultValue() {
        return Vec3.ZERO;
    }
}
