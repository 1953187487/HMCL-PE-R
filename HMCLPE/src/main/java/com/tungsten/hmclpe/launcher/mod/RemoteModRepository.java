package com.tungsten.hmclpe.launcher.mod;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface RemoteModRepository {

    enum Type {
        MOD,
        MODPACK,
        RESOURCE_PACK,
        WORLD,
        CUSTOMIZATION
    }

    Type getType();

    enum SortType {
        DATE_CREATED,
        POPULARITY,
        LAST_UPDATED,
        NAME,
        AUTHOR,
        TOTAL_DOWNLOADS,
        CATEGORY,
        GAME_VERSION
    }

    enum SortOrder {
        ASC,
        DESC
    }

    Stream<RemoteMod> search(String gameVersion, @Nullable Category category, int pageOffset, int pageSize, String searchFilter, SortType sortType, SortOrder sortOrder)
            throws IOException;

    Optional<RemoteMod.Version> getRemoteVersionByLocalFile(LocalModFile localModFile, Path file) throws IOException;

    RemoteMod getModById(String id) throws IOException;

    RemoteMod.File getModFile(String modId, String fileId) throws IOException;

    Stream<RemoteMod.Version> getRemoteVersionsById(String id) throws IOException;

    Stream<Category> getCategories() throws IOException;

    class Category {
        private final Object self;
        private final String id;
        private final List<Category> subcategories;

        public Category(Object self, String id, List<Category> subcategories) {
            this.self = self;
            this.id = id;
            this.subcategories = subcategories;
        }

        public Object getSelf() {
            return self;
        }

        public String getId() {
            return id;
        }

        public List<Category> getSubcategories() {
            return subcategories;
        }
    }

    String[] DEFAULT_GAME_VERSIONS = new String[]{
            "26.3", "26.2", "26.1",
            "25.1", "25.0",
            "24.3", "24.2", "24.1",
            "23.5", "23.4", "23.3", "23.2", "23.1",
            "22.5", "22.4", "22.3", "22.2", "22.1",
            "21.4", "21.3", "21.2", "21.1",
            "20.4", "20.3", "20.2", "20.1",
            "19.5", "19.4", "19.3", "19.2", "19.1",
            "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21",
            "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
            "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
            "1.18.2", "1.18.1", "1.18",
            "1.17.1", "1.17",
            "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16",
            "1.15.2", "1.15.1", "1.15",
            "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14",
            "1.13.2", "1.13.1", "1.13",
            "1.12.2", "1.12.1", "1.12",
            "1.11.2", "1.11.1", "1.11",
            "1.10.2", "1.10.1", "1.10",
            "1.9.4", "1.9.3", "1.9.2", "1.9.1", "1.9",
            "1.8.9", "1.8.8", "1.8.7", "1.8.6", "1.8.5", "1.8.4", "1.8.3", "1.8.2", "1.8.1", "1.8",
            "1.7.10", "1.7.9", "1.7.8", "1.7.7", "1.7.6", "1.7.5", "1.7.4", "1.7.3", "1.7.2",
            "1.6.4", "1.6.2", "1.6.1",
            "1.5.2", "1.5.1",
            "1.4.7", "1.4.6", "1.4.5", "1.4.4", "1.4.2",
            "1.3.2", "1.3.1",
            "1.2.5", "1.2.4", "1.2.3", "1.2.2", "1.2.1",
            "1.1",
            "1.0"
    };
}