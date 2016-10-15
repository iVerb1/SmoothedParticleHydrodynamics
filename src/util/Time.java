package util;

import org.lwjgl.Sys;

/**
 * Created by Jeroen van Wijgerden on 7-3-2015.
 */
public class Time {

    private static long lastTime;
    private static float delta;
    private static Float nextDelta;

    public Time() {   }

    public static void initialize() {
        lastTime = 1000 * Sys.getTime() / Sys.getTimerResolution();
    }

    public static float getDelta() {
        return delta;
    }

    public static long getTime() {
        return Sys.getTime() / Sys.getTimerResolution();
    }

    public static void tick() {
        long time = (1000 * Sys.getTime()) / Sys.getTimerResolution();

        if (nextDelta == null) {
            delta = ((float) (time - lastTime)) / 1000;
        }
        else {
            delta = nextDelta;
            nextDelta = null;
        }

        lastTime = time;
    }

    public static void setNextDelta(float d) {
        nextDelta = d;
    }

}
