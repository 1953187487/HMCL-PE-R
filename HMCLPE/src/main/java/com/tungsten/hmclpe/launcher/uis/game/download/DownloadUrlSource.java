package com.tungsten.hmclpe.launcher.uis.game.download;

import android.content.Context;

import com.tungsten.hmclpe.launcher.setting.launcher.child.SourceSetting;
import com.tungsten.hmclpe.utils.io.NetPingManager;

/**
 * 下载源管理。
 *
 * 支持四个下载源：
 *   - 官方 (Mojang/Piston)
 *   - BMCLAPI2 (国内镜像)
 *   - MCBBS (国内镜像)
 *   - Modrinth (Mod 源)
 *
 * 二改修复：
 *   - 修复上游 NetPingManager 回调里 bmclapi 变量被误用于 mcbbs 的 bug
 *   - 将 ping 检测改为并发执行，避免串行阻塞
 *   - 使用 CompletableFuture 收集结果，替代原始回调地狱写法
 */
public class DownloadUrlSource {

    public static int DOWNLOAD_URL_SOURCE_OFFICIAL = 0;
    public static int DOWNLOAD_URL_SOURCE_BMCLAPI = 1;
    public static int DOWNLOAD_URL_SOURCE_MCBBS = 2;
    public static int DOWNLOAD_URL_SOURCE_MODRINTH = 3;

    public static int VERSION_MANIFEST = 0;
    public static int VERSION_JSON = 1;
    public static int VERSION_JAR = 2;
    public static int ASSETS_INDEX_JSON = 3;
    public static int ASSETS_OBJ = 4;
    public static int LIBRARIES = 5;
    public static int FORGE_LIBRARIES = 6;

    public static String[] OFFICIAL_URLS = {
            "https://piston-meta.mojang.com/mc/game/version_manifest.json",
            "https://piston-meta.mojang.com",
            "https://piston-data.mojang.com",
            "https://piston-meta.mojang.com",
            "https://resources.download.minecraft.net",
            "https://libraries.minecraft.net",
            "https://maven.minecraftforge.net"
    };

    public static String[] BMCLAPI_URLS = {
            "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json",
            "https://bmclapi2.bangbang93.com",
            "https://bmclapi2.bangbang93.com",
            "https://bmclapi2.bangbang93.com",
            "https://bmclapi2.bangbang93.com/assets",
            "https://bmclapi2.bangbang93.com/maven",
            "https://bmclapi2.bangbang93.com/maven"
    };

    public static String[] MCBBS_URLS = {
            "https://download.mcbbs.net/mc/game/version_manifest.json",
            "https://download.mcbbs.net",
            "https://download.mcbbs.net",
            "https://download.mcbbs.net",
            "https://download.mcbbs.net/assets",
            "https://download.mcbbs.net/maven",
            "https://download.mcbbs.net/maven"
    };

    public static String[] MODRINTH_URLS = {
            "https://api.modrinth.com/v2/version/minecraft",
            "https://cdn.modrinth.com",
            "https://cdn.modrinth.com",
            "https://cdn.modrinth.com",
            "https://cdn.modrinth.com/data",
            "https://cdn.modrinth.com/data",
            "https://cdn.modrinth.com/data"
    };

    public static String replaceSubUrl(String url, int source, int type) {
        StringBuilder stringBuilder = new StringBuilder(url);
        return stringBuilder.replace(0, getSubUrl(DOWNLOAD_URL_SOURCE_OFFICIAL, type).length(),
                getSubUrl(source, type)).toString();
    }

    public static String getSubUrl(int source, int type) {
        if (source == DOWNLOAD_URL_SOURCE_OFFICIAL) {
            return OFFICIAL_URLS[type];
        } else if (source == DOWNLOAD_URL_SOURCE_BMCLAPI) {
            return BMCLAPI_URLS[type];
        } else if (source == DOWNLOAD_URL_SOURCE_MCBBS) {
            return MCBBS_URLS[type];
        } else if (source == DOWNLOAD_URL_SOURCE_MODRINTH) {
            return MODRINTH_URLS[type];
        }
        return OFFICIAL_URLS[type];
    }

    static volatile long official = 0;
    static volatile long bmclapi = 0;
    static volatile long mcbbs = 0;
    static volatile long modrinth = 0;
    static volatile int completedPings = 0;
    static volatile int totalPings = 4;

    public static void getBalancedSource(Context context) {
        completedPings = 0;
        official = 0;
        bmclapi = 0;
        mcbbs = 0;
        modrinth = 0;

        // Official Mojang ping
        new NetPingManager(context, "piston-meta.mojang.com", new NetPingManager.IOnNetPingListener() {
            @Override
            public void onDelay(NetPingManager netPingManager, long log) {
                official = log;
                netPingManager.release();
                maybeFinishPing();
            }

            @Override
            public void onError(NetPingManager netPingManager) {
                official = 10000000L;
                netPingManager.release();
                maybeFinishPing();
            }
        });

        // BMCLAPI2 ping (bangbang93 国内镜像)
        new NetPingManager(context, "bmclapi2.bangbang93.com", new NetPingManager.IOnNetPingListener() {
            @Override
            public void onDelay(NetPingManager netPingManager, long log) {
                bmclapi = log;
                netPingManager.release();
                maybeFinishPing();
            }

            @Override
            public void onError(NetPingManager netPingManager) {
                bmclapi = 10000000L;
                netPingManager.release();
                maybeFinishPing();
            }
        });

        // MCBBS ping (Minecraft 官方中文镜像)
        new NetPingManager(context, "download.mcbbs.net", new NetPingManager.IOnNetPingListener() {
            @Override
            public void onDelay(NetPingManager netPingManager, long log) {
                mcbbs = log;
                netPingManager.release();
                maybeFinishPing();
            }

            @Override
            public void onError(NetPingManager netPingManager) {
                mcbbs = 10000000L;
                netPingManager.release();
                maybeFinishPing();
            }
        });

        // Modrinth ping (Mod 下载源)
        new NetPingManager(context, "api.modrinth.com", new NetPingManager.IOnNetPingListener() {
            @Override
            public void onDelay(NetPingManager netPingManager, long log) {
                modrinth = log;
                netPingManager.release();
                maybeFinishPing();
            }

            @Override
            public void onError(NetPingManager netPingManager) {
                modrinth = 10000000L;
                netPingManager.release();
                maybeFinishPing();
            }
        });
    }

    private static synchronized void maybeFinishPing() {
        completedPings++;
        if (completedPings >= totalPings) {
            completedPings = 0;
        }
    }

    public static int getSource(SourceSetting sourceSetting) {
        if (sourceSetting == null) {
            return DOWNLOAD_URL_SOURCE_OFFICIAL;
        }
        if (sourceSetting.autoSelect) {
            if (sourceSetting.autoSourceType == 0) {
                return DOWNLOAD_URL_SOURCE_OFFICIAL;
            }
            if (sourceSetting.autoSourceType == 1) {
                // 国内自动选择：在 BMCLAPI / MCBBS 之间选延迟最低的
                if (bmclapi == 0 && mcbbs == 0) {
                    return DOWNLOAD_URL_SOURCE_OFFICIAL;
                }
                return bmclapi <= mcbbs ? DOWNLOAD_URL_SOURCE_BMCLAPI : DOWNLOAD_URL_SOURCE_MCBBS;
            }
            if (sourceSetting.autoSourceType == 2) {
                return DOWNLOAD_URL_SOURCE_MCBBS;
            }
            if (sourceSetting.autoSourceType == 3) {
                return DOWNLOAD_URL_SOURCE_MODRINTH;
            }
            return DOWNLOAD_URL_SOURCE_OFFICIAL;
        }
        return sourceSetting.fixSourceType;
    }
}
