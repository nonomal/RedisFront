package org.dromara.redisfront.commons.utils;

import java.util.prefs.Preferences;

/**
 * @author Karl Tauber
 */
@Deprecated
public class PrefUtils {

    private PrefUtils() {
    }
    private static Preferences state;

    public static Preferences getState() {
        return state;
    }

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
    }


}
