package net.zhuruoling.omms.central.system;

import oshi.PlatformEnum;
import oshi.SystemInfo;

public class OperatingSystem {
    public static final PlatformEnum platformEnum;
    public static boolean IS_WINDOWS;
    public static boolean IS_LINUX;
    public static boolean IS_MACOS;

    static {
        platformEnum = SystemInfo.getCurrentPlatform();
        IS_WINDOWS = platformEnum == PlatformEnum.WINDOWS;
        IS_LINUX = platformEnum == PlatformEnum.LINUX || platformEnum == PlatformEnum.ANDROID;
        IS_MACOS = platformEnum == PlatformEnum.MACOS;
        if (!IS_WINDOWS && !IS_LINUX && !IS_MACOS) {
            throw new UnsupportedOperationException("Operating system not supported: " + platformEnum.getName());
        }
    }
}
