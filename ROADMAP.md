# Roadmap for HMCL-PE-Renewed

This document outlines the planned development stages for the community fork
HMCL-PE-Renewed. Not all items are committed to a specific timeline.

## 1.0.x (current, in this repository)

- Fork bootstrap, brand rebranding, GPLv3 compliance documentation.
- **No new user-facing features** beyond replacing the "upstream stopped
  maintenance" notice with a fork identity notice.

## 1.1.0 (upcoming)

- **Fix stale dependency pins**:
  - AGP `7.2.2` -> latest compatible version.
  - `compileSdk 34` -> `35` or newer.
  - Java 1.8 compatibility flags -> Java 11 minimum.
- **Dependency audit**:
  - `com.github.gzu-liyujiang:Android_CN_OAID` is no longer maintained;
    evaluate `cn.com.gzu.liyujiang:oaid-sdk` or drop entirely.
  - `cat.ereza:customactivityoncrash:2.3.0` replaced upstream by
    `cat.ereza:customactivityoncrash:2.7.0+`; bump and verify behavior.
- **Compile on Linux (NDK r27+)**: ensure `./gradlew assembleRelease` works
  without Android Studio.

## 1.2.0

- **Sync with HMCL desktop 3.17 concepts**:
  - Adopt HMCL's `GameRepositoryLayout` v5 as the instance on-disk layout.
  - Align `VersionManifest`/`Library` parsing with HMCL 3.17's JSON schema.
- **Instance compatibility**:
  - Read `instance.json` produced by HMCL desktop and multi-instance
    launchers.
  - Support MultiMC instance manifest import (already present, extend).

## 1.3.0

- **Mod ecosystem parity**:
  - Add Modrinth download backend (already declared in `strings.xml`).
  - Fabric/Quilt/Forge installer parity with HMCL 3.17.
- **Controller v2**:
  - Persist user-defined layouts to `HMCLPE/src/main/assets/control/*.json`.
  - Import/export community layouts.

## 1.4.0

- **PojavLauncher / Boat upgrade**:
  - Refresh embedded Boat and PojavLauncher subprojects against current
    upstream.
  - Evaluate migration from `useLegacyPackaging=true` for ABI 64 support
    on newer Android 14+ devices.
- **VirGL stability fixes**:
  - Update `libvirglrenderer` and `libgbm` to the latest upstream commits
    to fix driver issues on Mali/GPU-vendor-specific hardware.

## 2.0.0 (major)

- **Kotlin migration**: convert core `launcher` package to Kotlin for
  long-term maintainability. The `auth`, `event`, `utils`, `skin`
  packages are small enough to migrate in one pass.
- **Compose UI**: replace XML layouts with Jetpack Compose for new screens
  (settings, about, launch dialog). Keep XML layouts for hot paths that
  must not regress.

## Not planned

- **iOS build**: HMCL-PE does not support iOS. If needed, this would be a
  separate project with a new license review.
- **Non-GPLv3 distribution**: any redistribution must comply with GPLv3
  and preserve upstream attribution. See `DERIVED-FROM.md`.
