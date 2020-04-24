package org.gbcraft.tyrodetector.help;

import java.util.UUID;

public class TimeHelperManager {
    private static TimeHelper timeHelper = new PlanTimeHelper();

    public static void registerTimeHelper(TimeHelper helper){
        if(null != helper) {
            timeHelper = helper;
        }
    }

    public static Long getPlayHours(UUID uuid){
        return timeHelper.getPlayHours(uuid);
    }

    public static Long getPlayHours(String name){
        return timeHelper.getPlayHours(name);
    }
}
