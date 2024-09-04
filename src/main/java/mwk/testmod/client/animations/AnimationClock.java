package mwk.testmod.client.animations;

import mwk.testmod.TestMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;

/**
 * To avoid passing delta time to every animation, we can use a singleton clock that keeps track of
 * the time since the last update. This clock is updated every client tick and can be fetched by
 * animations when needed.
 */
@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public class AnimationClock {

    private float deltaTime;
    private long lastTickTime;

    private static final AnimationClock INSTANCE = new AnimationClock();

    private AnimationClock() {
        deltaTime = 0;
    }

    public static AnimationClock getInstance() {
        return INSTANCE;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastTickTime) / 1000.0F;
        lastTickTime = currentTime;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Not sure if the stage is actually relevant, we just don't want to update the clock more
        // than once per frame
        if (event.getStage() == Stage.AFTER_SKY) {
            INSTANCE.update();
        }
    }

}
