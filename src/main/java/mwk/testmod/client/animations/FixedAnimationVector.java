package mwk.testmod.client.animations;

import mwk.testmod.client.animations.base.FixedAnimation;
import net.minecraft.world.phys.Vec3;

public class FixedAnimationVector extends FixedAnimation<Vec3> {

    public FixedAnimationVector(float duration, Function function, Vec3 start, Vec3 target) {
        super(duration, function, start, target);
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

}
