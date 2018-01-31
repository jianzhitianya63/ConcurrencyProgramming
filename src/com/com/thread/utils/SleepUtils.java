package com.com.thread.utils;

import java.util.concurrent.TimeUnit;

public class SleepUtils {
    private TimeUnit seconds;

    public static  void second(long seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
