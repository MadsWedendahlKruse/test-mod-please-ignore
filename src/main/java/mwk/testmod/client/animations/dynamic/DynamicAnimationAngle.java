package mwk.testmod.client.animations.dynamic;

public class DynamicAnimationAngle extends DynamicAnimationFloat {

    public DynamicAnimationAngle(float speed) {
        super(speed);
    }

    @Override
    protected float distance(Float t1, Float t2) {
        // TODO: Auto-generated, not sure if its the best way
        float angle = t2 - t1;
        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    @Override
    protected Float direction(Float t1, Float t2) {
        return 1.0F;
    }
}
