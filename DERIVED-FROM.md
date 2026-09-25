# DERIVED-FROM

This project (HMCL-PE-Renewed, "HMCL-PE-R") is a **community fork** derived from
two upstream open source projects, all under the **GNU General Public License v3**:

## Primary upstream

- **HMCL-PE** (Hello Minecraft! Launcher : Pocket Edition)
  - Author: Tungstend (Tungsten, @念小六)
  - Repository: https://github.com/Tungstend/HMCL-PE
  - Base version forked from: 2.0.8 (release of 2024.02.08)
  - License: GPL v3 (see `LICENSE`)
  - Status upstream: maintenance stopped (final release 2.0.8)

## Reference upstream (design + desktop code lineage)

- **HMCL** (Hello Minecraft! Launcher) - desktop edition
  - Author: huangyuhui (huanghongxun2008@126.com) and contributors
  - Repository: https://github.com/HMCL-dev/HMCL
  - Referenced branches: `main` (v3.17) and `release/3.6` (LTS)
  - License: GPL v3 with additional clauses (see `LICENSE`)

## Derived-work statement (GPLv3 Section 7)

HMCL (desktop) is released under GPLv3 with the following **Additional Term**:

> "You may not use or otherwise exploit this Program for creating, promoting,
> developing or marketing any commercial service that is or has an objective
> or effect similar to the HMCL service."

**Compliance**: HMCL-PE-R is a free community project intended to run the
Minecraft Java Edition on Android devices for personal, non-commercial use.
Any commercial redistribution must comply with GPLv3 and preserve this
attribution notice.

## What was kept from upstream

- GPLv3 LICENSE file preserved verbatim (see `LICENSE`)
- All author copyright notices in source files retained
- Project structure preserved (`HMCLPE`, `Boat`, `PojavLauncher`, `FilePicker`, `ZipTools`)
- Core launch pipeline (Boat + PojavLauncher + VirGL) preserved

## What was changed in this fork (v1.0.0)

- Application ID renamed to `com.hmclpe.renewed`
- Version string reset to `1.0.0` (versionCode `10000`)
- Brand name updated to "HMCL-PE-R" / "Pocket Edition - Renewed"
- Release signing config reworked to use local `release.jks` and environment
  variables (upstream hardcoded keystore password is removed)
- Upstream "stopped maintenance" warning replaced with fork identity notice
- This repository's own `CHANGELOG.md`, `ROADMAP.md`, `CONTRIBUTING.md`
  and `DERIVED-FROM.md` (this file) added
- See `docs/DERIVATION-ANALYSIS.md` for detailed migration analysis

## How to contribute

See `CONTRIBUTING.md`. All contributions must remain under GPLv3 and must
not strip upstream attribution.
