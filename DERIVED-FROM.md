# DERIVED-FROM (开源协议归属声明)

**本项目是基于 HMCL-PE 2.0.8 (Tungstend) 的开源二改版本 (GPLv3 derived work)。**

- 本项目是**对上游源码的二次修改**，不宣称替代、续订或延续上游项目。
- 上游项目继续以 Tungstend 的名义存在，本项目与上游并列。
- 所有二次修改严格遵循 GPLv3，包括保留上游作者的完整版权声明。

上游项目 (all under GNU General Public License v3):

## Primary upstream (二改的直接父源)

- **HMCL-PE** (Hello Minecraft! Launcher : Pocket Edition)
  - Author: Tungstend (Tungsten, @念小六)
  - Repository: https://github.com/Tungstend/HMCL-PE
  - Base version forked from: 2.0.8 (release of 2024.02.08)
  - License: GPL v3 (see `LICENSE`)
  - Upstream status: 上游自主决定停止更新（final release 2.0.8），
    本项目与上游状态无关，仅使用其公开源码作为二改基础。

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
- Upstream "停止更新" 提示已改为二改身份说明
- 新增本项目的 `CHANGELOG.md`、`ROADMAP.md`、`CONTRIBUTING.md`
  以及本文件 `DERIVED-FROM.md`
- 详见 `docs/DERIVATION-ANALYSIS.md` 的二改方法分析

## How to contribute

See `CONTRIBUTING.md`. All contributions must remain under GPLv3 and must
not strip upstream attribution.
