package com.e104.realtime.common.util;

import java.time.LocalTime;

public final class TimeChecker {

    public static boolean isNight() {
        LocalTime now = LocalTime.now();
        return 21 <= now.getHour() || now.getHour() <= 9;
    }

    public static String now() {
        return LocalTime.now().toString();
    }
}
