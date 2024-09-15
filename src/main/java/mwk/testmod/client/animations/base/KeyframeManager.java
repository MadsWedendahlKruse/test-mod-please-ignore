package mwk.testmod.client.animations.base;

import java.util.ArrayList;
import mwk.testmod.client.animations.AnimationClock;
import mwk.testmod.client.animations.FixedAnimationFloat;

/**
 * The KeyframeManager class allows multiple animations to be synchronized and run in parallel. Each
 * animation is represented by a track, and each track can have multiple keyframes.
 */
public class KeyframeManager {

    private final ArrayList<FixedAnimationFloat>[] tracks;
    private final float[] initialValues;
    private final int[] currentIndices;

    public KeyframeManager(int numTracks) {
        tracks = new ArrayList[numTracks];
        for (int i = 0; i < numTracks; i++) {
            tracks[i] = new ArrayList<>();
        }
        initialValues = new float[numTracks];
        currentIndices = new int[numTracks];
    }

    public KeyframeManager(float[] initialValues) {
        tracks = new ArrayList[initialValues.length];
        for (int i = 0; i < initialValues.length; i++) {
            tracks[i] = new ArrayList<>();
        }
        this.initialValues = initialValues;
        currentIndices = new int[initialValues.length];
    }

    public void addKeyframe(int track, float duration, float value,
            FixedAnimation.Function function) {
        if (track < 0 || track >= tracks.length) {
            throw new IllegalArgumentException("Invalid track index: " + track);
        }
        if (tracks[track].isEmpty()) {
            tracks[track].add(
                    new FixedAnimationFloat(duration, function, initialValues[track], value));
        } else {
            FixedAnimationFloat last = tracks[track].get(tracks[track].size() - 1);
            tracks[track].add(
                    new FixedAnimationFloat(duration, function, last.getTargetValue(), value));
        }
    }

    public void addKeyframe(int track, float duration, float value) {
        addKeyframe(track, duration, value, FixedAnimation.Function.LINEAR);
    }

    public void update(float deltaTime) {
        int finishedTracks = 0;
        for (int i = 0; i < tracks.length; i++) {
            if (currentIndices[i] < tracks[i].size()) {
                FixedAnimationFloat currentAnimation = tracks[i].get(currentIndices[i]);
                float remainingTime = currentAnimation.update(deltaTime);
                if (currentAnimation.isFinished()) {
                    currentIndices[i]++;
                    // If the animation is finished, the elapsed time would be
                    // greater than or equal to the duration, thus making the
                    // remaining time negative, hence the minus sign.
                    update(-remainingTime);
                }
            } else {
                finishedTracks++;
            }
        }
        if (finishedTracks == tracks.length) {
            // All tracks are finished, so reset them (?)
            start();
        }
    }

    public void update() {
        update(AnimationClock.getInstance().getDeltaTime());
    }

    public float getValue(int track) {
        if (track < 0 || track >= tracks.length) {
            throw new IllegalArgumentException("Invalid track index: " + track);
        }
        if (currentIndices[track] < tracks[track].size()) {
            return tracks[track].get(currentIndices[track]).getValue();
        }
        if (tracks[track].isEmpty()) {
            return initialValues[track];
        }
        return tracks[track].get(tracks[track].size() - 1).getTargetValue();
    }

    public void start() {
        for (int i = 0; i < tracks.length; i++) {
            currentIndices[i] = 0;
            for (FixedAnimationFloat animation : tracks[i]) {
                animation.start();
            }
        }
    }
}
