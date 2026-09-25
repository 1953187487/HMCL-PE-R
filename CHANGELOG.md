# Changelog

All notable changes to HMCL-PE-Renewed are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and
this project adheres to [Semantic Versioning](https://semver.org/).

## [1.0.0] - 2026-09-25

### Added
- Community-derived fork of HMCL-PE 2.0.8 (Tungstend), released under GPLv3
  with full upstream attribution preserved. This is a derived work, not a
  successor to or continuation of the upstream project.
- `DERIVED-FROM.md` documenting upstream attribution and GPLv3 compliance.
- `docs/DERIVATION-ANALYSIS.md` with detailed analysis comparing:
  - HMCL 3.17 (desktop main branch)
  - HMCL 3.6 LTS (release/3.6)
  - HMCL-PE 2.0.8 (Android, upstream base)
- `docs/MIGRATION-DESIGN.md` describing the migration plan from HMCL-PE 2.0.8
  to HMCL-PE-R 1.0.0.
- `CONTRIBUTING.md` for community contributors.
- Fresh release signing configuration using `release.jks` and environment
  variables, replacing the upstream hardcoded password.
- Application ID `com.hmclpe.renewed`, version 1.0.0 / code 10000.
- `launcher_version.json` repointed to this fork's release channel.

### Changed
- Brand renamed to "HMCL-PE-R" / "Pocket Edition - Renewed".
- About-page strings updated to include upstream attribution to Tungstend.
- The upstream "HMCL-PE has stopped maintenance" warning dialog is replaced
  with a fork identity notice.
- `rootProject.name` in `settings.gradle` set to `HMCL-PE-Renewed`.

### Fixed
- Removed hardcoded release keystore password from `HMCLPE/build.gradle`;
  now read from environment (`HMCLR_KEYSTORE_PASS`, `HMCLR_KEY_ALIAS`,
  `HMCLR_KEY_PASS`).

### Known limitations (roadmap targets)
- Compile requires Android Studio with SDK 34 + NDK; not verified in the
  release container.
- No upstream sync mechanism yet; see `ROADMAP.md` for the planned
  `upstream-sync` workflow.
- No new features beyond branding at 1.0.0; feature work tracked in
  `ROADMAP.md`.
