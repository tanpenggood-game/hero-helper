package com.itplh.hero.util;

import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    public static void sleep(long time, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public static void sleep(int sleepMilliseconds) {
        sleep(sleepMilliseconds, TimeUnit.MILLISECONDS);
    }

}
