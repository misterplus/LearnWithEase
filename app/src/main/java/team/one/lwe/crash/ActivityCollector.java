package team.one.lwe.crash;

import java.util.ArrayList;
import java.util.List;

import team.one.lwe.ui.activity.LWEUI;

public class ActivityCollector {

    private static final List<LWEUI> activityList = new ArrayList<>();

    public static void addActivity(LWEUI activity) {
        activityList.add(activity);
    }

    public static void removeActivity(LWEUI activity) {
        activityList.remove(activity);
    }

    public static void finishAll() {
        for (LWEUI activity : activityList) {
            activityList.remove(activity);
            activity.finish();
        }
    }

}
