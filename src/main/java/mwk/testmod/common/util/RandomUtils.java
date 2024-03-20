package mwk.testmod.common.util;

import java.util.Random;
import net.minecraft.util.RandomSource;

public class RandomUtils {

    private RandomUtils() {}

    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
}
