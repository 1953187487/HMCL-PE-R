package com.tungsten.hmclpe.launcher.game;

public class GameJavaVersion {
    private final String component;
    private final int majorVersion;

    public GameJavaVersion() {
        this("", 0);
    }

    public GameJavaVersion(String component, int majorVersion) {
        this.component = component;
        this.majorVersion = majorVersion;
    }

    public String getComponent() {
        return component;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public static final GameJavaVersion JAVA_16 = new GameJavaVersion("java-runtime-alpha", 16);
    public static final GameJavaVersion JAVA_17 = new GameJavaVersion("java-runtime", 17);
    public static final GameJavaVersion JAVA_21 = new GameJavaVersion("java-runtime", 21);
    public static final GameJavaVersion JAVA_25 = new GameJavaVersion("java-runtime", 25);
    public static final GameJavaVersion JAVA_8 = new GameJavaVersion("jre-legacy", 8);

    /**
     * Map a Mojang version manifest Java entry to a supported JRE major version.
     *
     * <p>1.19.3 and 1.19.4 are the only releases that shipped with an
     * alpha-only Java 16 runtime, which 1.19.3 and later games reject; those
     * versions must run on Java 17 instead. Anything newer, including the Java 21
     * releases and the Java 25 releases starting with 1.26.2, runs on the major
     * version the manifest declares.
     */
    public static int resolveRequiredMajor(Version version) {
        int major = 8;
        if (version != null && version.getJavaVersion() != null) {
            major = version.getJavaVersion().getMajorVersion();
        }
        if (version != null && version.getMinimumLauncherVersion() < 9 && major > 8) {
            major = 8;
        }
        if (major == 16) {
            major = 17;
        }
        return major;
    }

    public static boolean isSupported(int major) {
        return major == 8 || major == 17 || major == 21 || major == 25;
    }
}
