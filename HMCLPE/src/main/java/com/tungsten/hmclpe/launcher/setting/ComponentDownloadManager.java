package com.tungsten.hmclpe.launcher.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.utils.Architecture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ComponentDownloadManager {

    private static final String TAG = "ComponentDownload";
    private static final String PREFS_NAME = "HMCLPER_P_references";
    private static final String KEY_COMPONENTS_DOWNLOADED = "components_downloaded";

    public interface DownloadCallback {
        void onProgress(String componentName, int progress, String status);
        void onCompleted(boolean success);
    }

    public static class ComponentInfo {
        public String name;
        public String url;
        public String localPath;
        public boolean downloaded;
        public long size;

        public ComponentInfo(String name, String url, String localPath, boolean downloaded, long size) {
            this.name = name;
            this.url = url;
            this.localPath = localPath;
            this.downloaded = downloaded;
            this.size = size;
        }
    }

    private Context context;
    private DownloadCallback callback;

    public ComponentDownloadManager(Context context) {
        this.context = context;
    }

    public void setCallback(DownloadCallback callback) {
        this.callback = callback;
    }

    public ArrayList<ComponentInfo> getComponentList() {
        ArrayList<ComponentInfo> components = new ArrayList<>();
        
        String arch = getDeviceArchitecture();
        
        // Java components
        components.add(new ComponentInfo(
            "Java 8",
            getJavaDownloadUrl("8", arch),
            AppManifest.JAVA_DIR + "/default",
            isJava8Downloaded(),
            getJavaSize("8")
        ));
        
        components.add(new ComponentInfo(
            "Java 17",
            getJavaDownloadUrl("17", arch),
            AppManifest.JAVA_DIR + "/JRE17",
            isJava17Downloaded(),
            getJavaSize("17")
        ));
        
        components.add(new ComponentInfo(
            "Java 21",
            getJavaDownloadUrl("21", arch),
            AppManifest.JAVA_DIR + "/JRE21",
            isJava21Downloaded(),
            getJavaSize("21")
        ));
        
        components.add(new ComponentInfo(
            "Java 25",
            getJavaDownloadUrl("25", arch),
            AppManifest.JAVA_DIR + "/JRE25",
            isJava25Downloaded(),
            getJavaSize("25")
        ));
        
        // Launcher components
        components.add(new ComponentInfo(
            "Boat Launcher",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/boat-launcher.zip",
            AppManifest.BOAT_LIB_DIR,
            isBoatDownloaded(),
            50 * 1024 * 1024
        ));
        
        components.add(new ComponentInfo(
            "Pojav Launcher",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/pojav-launcher.zip",
            AppManifest.POJAV_LIB_DIR,
            isPojavDownloaded(),
            50 * 1024 * 1024
        ));
        
        components.add(new ComponentInfo(
            "Caciocavallo",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/caciocavallo.zip",
            AppManifest.CACIOCAVALLO_DIR,
            isCaciocavalloDownloaded(),
            20 * 1024 * 1024
        ));
        
        components.add(new ComponentInfo(
            "Caciocavallo 17",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/caciocavallo17.zip",
            AppManifest.CACIOCAVALLO17_DIR,
            isCaciocavallo17Downloaded(),
            20 * 1024 * 1024
        ));
        
        // Plugin components
        components.add(new ComponentInfo(
            "Forge Installer",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/forge-installer.zip",
            AppManifest.PLUGIN_DIR + "/installer",
            isPluginDownloaded("installer"),
            5 * 1024 * 1024
        ));
        
        components.add(new ComponentInfo(
            "Touch Injector",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/touch-injector.zip",
            AppManifest.PLUGIN_DIR + "/touch",
            isPluginDownloaded("touch"),
            10 * 1024 * 1024
        ));
        
        components.add(new ComponentInfo(
            "Authlib Injector",
            "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/authlib-injector.zip",
            AppManifest.PLUGIN_DIR + "/login/authlib-injector",
            isPluginDownloaded("login/authlib-injector"),
            5 * 1024 * 1024
        ));
        
        return components;
    }

    public boolean isJava8Downloaded() {
        File file = new File(AppManifest.JAVA_DIR + "/default");
        return file.exists() && new File(file, "version").exists();
    }

    public boolean isJava17Downloaded() {
        File file = new File(AppManifest.JAVA_DIR + "/JRE17");
        return file.exists() && new File(file, "version").exists();
    }

    public boolean isJava21Downloaded() {
        File file = new File(AppManifest.JAVA_DIR + "/JRE21");
        return file.exists() && new File(file, "version").exists();
    }

    public boolean isJava25Downloaded() {
        File file = new File(AppManifest.JAVA_DIR + "/JRE25");
        return file.exists() && new File(file, "version").exists();
    }

    public boolean isBoatDownloaded() {
        File file = new File(AppManifest.BOAT_LIB_DIR);
        return file.exists() && file.isDirectory();
    }

    public boolean isPojavDownloaded() {
        File file = new File(AppManifest.POJAV_LIB_DIR);
        return file.exists() && file.isDirectory();
    }

    public boolean isCaciocavalloDownloaded() {
        File dir = new File(AppManifest.CACIOCAVALLO_DIR);
        return dir.exists() && dir.isDirectory();
    }

    public boolean isCaciocavallo17Downloaded() {
        File dir = new File(AppManifest.CACIOCAVALLO17_DIR);
        return dir.exists() && dir.isDirectory();
    }

    public boolean isPluginDownloaded(String pluginPath) {
        File pluginDir = new File(AppManifest.PLUGIN_DIR + "/" + pluginPath);
        return pluginDir.exists() && pluginDir.isDirectory();
    }

    public void downloadMissingComponents() {
        new DownloadComponentsTask().execute();
    }

    public void downloadComponent(ComponentInfo component) {
        new DownloadSingleComponentTask(component).execute();
    }

    @SuppressLint("SetTextI18n")
    private class DownloadComponentsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<ComponentInfo> components = getComponentList();
            ArrayList<ComponentInfo> missing = new ArrayList<>();
            
            for (ComponentInfo comp : components) {
                if (!comp.downloaded) {
                    missing.add(comp);
                }
            }
            
            if (missing.isEmpty()) {
                return true;
            }
            
            for (ComponentInfo comp : missing) {
                if (callback != null) {
                    callback.onProgress(comp.name, 0, "Starting download...");
                }
                
        DownloadTaskListBean bean = new DownloadTaskListBean(
            comp.name,
            comp.url,
            comp.localPath + "/download.zip",
            ""
        );
                
                DownloadTask.DownloadFeedback feedback = new DownloadTask.DownloadFeedback() {
                    @Override
                    public void updateProgress(long curr, long max) {
                        int progress = (int) (100 * curr / max);
                        if (callback != null) {
                            callback.onProgress(comp.name, progress, "Downloading... " + progress + "%");
                        }
                    }

                    @Override
                    public void updateSpeed(String speed) {
                    }
                };
                
                try {
                    if (downloadFile(comp.url, bean.path)) {
                        if (callback != null) {
                            callback.onProgress(comp.name, 100, "Download complete");
                        }
                    } else {
                        Log.e(TAG, "Failed to download: " + comp.name);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error downloading " + comp.name, e);
                }
            }
            
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onCompleted(success);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private class DownloadSingleComponentTask extends AsyncTask<Void, Void, Boolean> {
        private ComponentInfo component;
        
        DownloadSingleComponentTask(ComponentInfo component) {
            this.component = component;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (callback != null) {
                callback.onProgress(component.name, 0, "Starting download...");
            }
            
            DownloadTaskListBean bean = new DownloadTaskListBean(
                component.name,
                component.url,
                component.localPath + "/download.zip",
                ""
            );
            
            DownloadTask.DownloadFeedback feedback = new DownloadTask.DownloadFeedback() {
                @Override
                public void updateProgress(long curr, long max) {
                    int progress = (int) (100 * curr / max);
                    if (callback != null) {
                        callback.onProgress(component.name, progress, "Downloading... " + progress + "%");
                    }
                }

                @Override
                public void updateSpeed(String speed) {
                }
            };
            
            try {
                if (downloadFile(component.url, bean.path)) {
                    if (callback != null) {
                        callback.onProgress(component.name, 100, "Download complete");
                    }
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading " + component.name, e);
            }
            
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onCompleted(success);
            }
        }
    }

    private boolean downloadFile(String urlStr, String destPath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        
        try {
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return false;
            }
            
            long totalSize = conn.getContentLength();
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(destPath);
            
            byte[] buffer = new byte[8192];
            long downloaded = 0;
            int bytesRead;
            
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                downloaded += bytesRead;
                
                if (callback != null && totalSize > 0) {
                    int progress = (int) (100 * downloaded / totalSize);
                    callback.onProgress("Component", progress, "Downloading... " + progress + "%");
                }
            }
            
            fos.close();
            is.close();
            return true;
        } finally {
            conn.disconnect();
        }
    }

    private String getDeviceArchitecture() {
        int arch = Architecture.getDeviceArchitecture();
        if (arch == Architecture.ARCH_ARM64) {
            return "arm64";
        } else if (arch == Architecture.ARCH_ARM) {
            return "arm";
        } else if (arch == Architecture.ARCH_X86_64) {
            return "x86_64";
        } else {
            return "x86";
        }
    }

    private String getJavaDownloadUrl(String version, String arch) {
        return "https://github.com/1953187487/HMCL-PE-R/releases/download/v1.0.4/java-" + version + "-" + arch + ".zip";
    }

    private long getJavaSize(String version) {
        return 200 * 1024 * 1024;
    }

    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            return (size / (1024 * 1024)) + " MB";
        } else {
            return (size / (1024 * 1024 * 1024)) + " GB";
        }
    }
}
