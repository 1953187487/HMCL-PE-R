> [!NOTE]
> **HMCL-PE (upstream, by Tungstend) has stopped maintenance.** This repository,
> **HMCL-PE-Renewed** ("HMCL-PE-R"), is a **community fork** that picks up where
> upstream left off. See [DERIVED-FROM.md](./DERIVED-FROM.md) for full GPLv3
> attribution and compliance statement. If you prefer an actively maintained
> project, please also consider [Fold Craft Launcher](https://fcl-team.github.io/)
> or [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher).

<div align="center">
    <img width="175" src="/HMCLPE/src/main/res/drawable/ic_craft_table.png"></img>
</div>

<h1 align="center">HMCL-PE-R</h1>
<p align="center">Hello Minecraft! Launcher : Pocket Edition — Renewed</p>

<div align="center">

![MainScreen](/.github/images/hmcl-pe-main-screen.jpg)

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Version](https://img.shields.io/badge/version-1.0.0-green?style=for-the-badge)
![License](https://img.shields.io/badge/license-GPLv3-blue?style=for-the-badge)
![Upstream](https://img.shields.io/badge/upstream-HMCL%20PE%202.0.8-orange?style=for-the-badge)

</div>

- **HMCL-PE-Renewed** is a community fork of
  [HMCL-PE 2.0.8](https://github.com/Tungstend/HMCL-PE) by Tungstend. It aims to
  keep Android Minecraft Java Edition launch support alive after upstream
  maintenance stopped, under the same GPLv3 license.
- **Run Minecraft Java Edition directly on your Android device**, with mod
  support and all versions of the Game.

- **Attribution**: this project is derived from HMCL-PE (Tungstend) and
  references [HMCL](https://github.com/HMCL-dev/HMCL) for desktop-lineage
  design. See [DERIVED-FROM.md](./DERIVED-FROM.md) and
  [docs/DERIVATION-ANALYSIS.md](./docs/DERIVATION-ANALYSIS.md).

<h1 align="center">Features</h1>

- [x] Run Minecraft on all versions
- [x] Run Forge, OptiFine, Fabric and others
- [x] Java 8 and Java 17
- [x] Mouse Virtual
- [x] Download Mods, ModPacks, Textures and Worlds.
- [x] Using Shaders (with VirGL)
- [x] Create custom controllers
- [x] Customizable launcher themes and colors
- [x] And much more!

<h1 align="center">Screenshots</h1>

![GameScreen1](/.github/images/hmcl-pe-in-game-1.jpg)
![GameScreen2](/.github/images/hmcl-pe-in-game-2.jpg)
![ModScreen](/.github/images/hmcl-pe-mods-menu.jpg)

<h1 align="center">Compilation</h1>

You can compile the software **using Android Studio**, with the **Android SDK & NDK** packages.
Clone the remote repository using the following URL:
```
https://github.com/HMCL-PE-Renewed/HMCL-PE-R
```

Set the release signing credentials before building:

```
export HMCLR_KEYSTORE_PASS=<your-keystore-password>
export HMCLR_KEY_ALIAS=<your-key-alias>
export HMCLR_KEY_PASS=<your-key-password>
```

Then compile with Gradle from Android Studio, or:

```
./gradlew assembleDebug
```

<h1 align="center">License</h1>

- **This fork** is licensed under **GPLv3** — see [LICENSE](./LICENSE).
- **Attribution**: derived from [HMCL-PE 2.0.8](https://github.com/Tungstend/HMCL-PE)
  by Tungstend. See [DERIVED-FROM.md](./DERIVED-FROM.md).
- **Reference upstream**: [HMCL](https://github.com/HMCL-dev/HMCL) (desktop),
  GPLv3 with additional clauses. See [docs/DERIVATION-ANALYSIS.md](./docs/DERIVATION-ANALYSIS.md).

<h1 align="center">Development</h1>

- [CHANGELOG.md](./CHANGELOG.md) — release history.
- [ROADMAP.md](./ROADMAP.md) — planned work.
- [CONTRIBUTING.md](./CONTRIBUTING.md) — how to help.
- [docs/MIGRATION-DESIGN.md](./docs/MIGRATION-DESIGN.md) — migration design.

The software is distributed under [GPL v3](https://www.gnu.org/licenses/gpl-3.0.html)
```
HMCL-PE is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HMCL-PE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with HMCL-PE.  If not, see <https://www.gnu.org/licenses/>.
```

<h1 align="center">Third Party Components</h1>

- [HMCL](https://github.com/huanghongxun/HMCL)

- [Boat and related projects](https://github.com/AOF-Dev/Boat)

- [PojavLauncher and related projects](https://github.com/PojavLauncherTeam/PojavLauncher)

- [Hin2n](https://github.com/switch-iot/hin2n)

- [authlib-injector](https://github.com/yushijinhun/authlib-injector)

- [nide8auth](https://login.mc-user.com:233/account/login)

- [forge-install-bootstrapper](https://github.com/bangbang93/forge-install-bootstrapper)

- [TouchInjector](https://github.com/Tungstend/TouchInjector)

<h1 align="center">Sponsor</h1>

<div align="center">

[![Sponsor](https://img.shields.io/badge/sponsor-30363D?style=for-the-badge&logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://afdian.net/@tungs)

</div>
