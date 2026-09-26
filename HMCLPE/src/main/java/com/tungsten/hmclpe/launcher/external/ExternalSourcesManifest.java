package com.tungsten.hmclpe.launcher.external;

import java.util.List;

public class ExternalSourcesManifest {

    public int version;
    public String generatedAt;
    public List<ExternalResource> launchSources;
    public List<ExternalResource> renderers;

    public static class ExternalResource {
        public String id;
        public String name;
        public String version;
        public String description;
        public String iconUrl;
        public String downloadUrl;
        public String pageUrl;
        public String minLauncherVersion;
        public String sha256;
    }
}
