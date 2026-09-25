> [!NOTE]
> **本项目是基于 [HMCL-PE 2.0.8](https://github.com/Tungstend/HMCL-PE)（Tungstend）的**开源二改**（derived work）。**
>
> HMCL-PE 上游目前已停止更新。本仓库是**对上游源码的二次修改**，不是上游的续订或官方延续。
> 完整协议合规与作者归属声明见 [DERIVED-FROM.md](./DERIVED-FROM.md)。
>
> **协议**：本项目与上游均为 **GPLv3**。所有二次修改在 GPLv3 下发布，保留全部上游作者版权声明。
> 二次修改的产物同样受 GPLv3 约束（含传染性 copyleft 要求）。

<div align="center">
    <img width="175" src="/HMCLPE/src/main/res/drawable/ic_craft_table.png"></img>
</div>

<h1 align="center">HMCL-PE-R</h1>
<p align="center">基于 HMCL-PE 2.0.8 的开源二改版本 (GPLv3 derived work)</p>

<div align="center">

![MainScreen](/.github/images/hmcl-pe-main-screen.jpg)

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Version](https://img.shields.io/badge/version-1.0.0-green?style=for-the-badge)
![License](https://img.shields.io/badge/license-GPLv3-blue?style=for-the-badge)
![Upstream](https://img.shields.io/badge/upstream-HMCL%20PE%202.0.8-orange?style=for-the-badge)

</div>

- **HMCL-PE-R** 是基于 [HMCL-PE 2.0.8](https://github.com/Tungstend/HMCL-PE) 的**开源二改**（GPLv3 derived work）。
  上游由 Tungstend 维护，本项目是社区对其源码的二次修改，与上游项目并列存在，不宣称替代或延续上游。
- **运行 Minecraft Java Edition on Android**，通过复用上游已经实现的 PojavLauncher + Boat + VirGL 运行时。
- **遵守 GPLv3**：本项目完全沿用上游 GPLv3 协议，所有二次修改均在 GPLv3 下分发，
  保留上游全部版权声明与衍生声明。详见 [DERIVED-FROM.md](./DERIVED-FROM.md)。
- **致谢上游**：HMCL-PE (Tungstend)、[HMCL](https://github.com/HMCL-dev/HMCL)（huangyuhui）、
  [Boat](https://github.com/AOF-Dev/Boat)、[PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)。

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
