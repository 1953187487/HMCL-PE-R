# 二改方法分析：HMCL 桌面 → HMCL-PE-R 手机版

本文档说明本次二改所依据的三份上游源码，以及从 HMCL-PE 2.0.8 迁移到
HMCL-PE-R 1.0.0 的具体方法与决策依据。

## 一、上游源码清单

| 上游项目         | 分支           | 版本     | 语言/栈                | 用途                       |
| ---------------- | -------------- | -------- | ---------------------- | -------------------------- |
| HMCL-PE          | main           | 2.0.8    | Java + Android Gradle  | **二改直接父源**（Android） |
| HMCL (desktop)   | main           | 3.17     | Java 17 + JavaFX       | 参考设计规范与模块划分     |
| HMCL (desktop)   | release/3.6    | 3.6 LTS  | Java 8 + JavaFX        | 参考 Java 8 兼容性         |

三份源码均已克隆到工作区，路径：

- `/workspace/HMCL-PE-src` — HMCL-PE 2.0.8
- `/workspace/HMCL-src` — HMCL 桌面 3.17
- `/workspace/HMCL-3.6` — HMCL 桌面 3.6 LTS

## 二、为什么选择 HMCL-PE 而非 HMCL 桌面作为父源

### 2.1 HMCL 桌面（JavaFX）无法直接二改为手机版

- 技术栈不兼容：桌面版使用 JavaFX 3 构建 UI，Android 平台不原生支持。
- 启动流程不兼容：桌面版直接调用本地 `java` 进程，Android 需要通过
  Boardwalk/PojavLauncher 在 Android 进程内虚拟化 Java 环境。
- 文件路径模型不兼容：桌面版假设 POSIX 路径与文件系统布局，Android
  有 Scoped Storage、SAF、shared external 限制。

**结论**：直接二改 HMCL 桌面到 Android 实际是重写。因此父源选择
HMCL-PE。

### 2.2 HMCL-PE 的可用性

HMCL-PE 已由 Tungstend 完成了以下基础工作：

- 通过嵌入 `Boat`（Android Termux）与 `PojavLauncher` 在 Android 内
  虚拟化 Java 环境。
- 通过 VirGL 桥接 OpenGL 到手机 GPU。
- 提供完整的登录、实例、模组、控制器 UI。
- 使用 `HMCLPE/src/main/assets/app_runtime/java/` 分发预编译的 OpenJDK。

这直接满足「Android 上跑 Minecraft Java 版」的核心诉求。本次二改以
其作为父源。

## 三、二改方法总览

### 3.1 保留（不改动）

- **上游 LICENSE**：保留 GPLv3 完整文本。
- **Boat/PojavLauncher/ZipTools/FilePicker 子模块**：这些是运行时
  核心，本次不做改动以降低风险。
- **代码包名 `com.tungsten.hmclpe`**：保留上游包名以最小化改动；
  新代码放在 `com.hmclpe.renewed` 下（后续迁移时可将老代码一并搬移）。
- **上游 Java 文件的版权头**：全部保留。

### 3.2 修改（品牌与元数据）

| 项                    | 上游值                              | 二改值                                     |
| --------------------- | ----------------------------------- | ------------------------------------------ |
| applicationId         | `com.tungsten.hmclpe`               | `com.hmclpe.renewed`                       |
| versionCode           | `208`                               | `10000`                                    |
| versionName           | `2.0.8`                             | `1.0.0`                                    |
| rootProject.name      | `Hello Minecraft Launcher Pocket Edition` | `HMCL-PE-Renewed`                   |
| `app_name` string     | `HMCL-PE`                           | `HMCL-PE-R`                                |
| 关于页上游作者        | Tungsten / 念小六                   | 保留 + 新增 fork 身份说明                  |

### 3.3 修改（安全与工程化）

- 上游 `HMCLPE/build.gradle` 中明文硬编码了 release 签名密钥密码
  `666666`。二改版本改为从环境变量 `HMCLR_KEYSTORE_PASS` /
  `HMCLR_KEY_ALIAS` / `HMCLR_KEY_PASS` 读取，避免密钥泄漏进 git 历史。
- 移除上游内置 `key-store.jks`，改为独立 `release.jks`。

### 3.4 新增（工程文档）

- `DERIVED-FROM.md` — 上游归属与 GPLv3 合规声明。
- `CHANGELOG.md` — 语义化版本变更日志。
- `ROADMAP.md` — 后续开发路线图。
- `CONTRIBUTING.md` — 贡献指南。
- `docs/DERIVATION-ANALYSIS.md` — 本文档。
- `docs/MIGRATION-DESIGN.md` — 迁移设计说明。

## 四、参考 HMCL 桌面 3.17 的关键差异

对比三份源码，HMCL 桌面 3.17 相比 HMCL-PE 2.0.8 有以下**未来迁移目标**
（不阻塞 1.0.0 发布，记录在 `ROADMAP.md`）：

### 4.1 实例布局升级

- HMCL 桌面 3.17 已引入 `GameRepositoryLayout` v5，实例目录结构与
  HMCL-PE 的老布局不同。
- 迁移策略：新增 layout 版本判定层，运行时决定使用哪个格式。

### 4.2 认证与账户管理

- HMCL 桌面 3.17 的 `Accounts` 类使用 `AccountMetadataStore` +
  `AccountPrivateData` 分离存储元数据与私钥。
- HMCL-PE 使用 `Accounts.java`（单体类）+ `launcher_profiles.json`。
- 迁移策略：先做兼容读取，再统一迁移。

### 4.3 语言包（i18n）

- HMCL 桌面使用 `.properties` 语言包，10 种语言。
- HMCL-PE 使用 Android 标准 `values-*/strings.xml`，20+ 语言。
- 迁移策略：以 Android 语言资源为主，桌面端语言包作为内容参考补齐。

### 4.4 下载后端

- HMCL 桌面支持 `DownloadProviders`（国内/国际切换）、多源加速、
  `DownloadSource` 抽象。
- HMCL-PE 主要走 MCBBS / CurseForge / Modrinth 单一路径。
- 迁移策略：抽象 `DownloadSource` 接口，兼容两套后端。

## 五、二改合规检查清单

在分发 1.0.0 之前，本 fork 通过以下合规检查：

- [x] GPLv3 LICENSE 完整保留
- [x] 上游作者 Tungstend 版权在 `DERIVED-FROM.md` 与关于页保留
- [x] HMCL 桌面 GPL 附加条款（禁止商业化复刻服务）声明保留
- [x] 未移除或修改任何 Java 文件内的 `/* Copyright (C) ... */` 注释
- [x] 未硬编码上游签名密钥，改用环境变量
- [x] 品牌名与 `applicationId` 已改为 fork 独立标识
- [x] 新增文档明确声明"这是一个 HMCL-PE 的二改 fork"

## 六、风险与后续

### 已知风险

1. **上游版权风险**：`Boat` 与 `PojavLauncher` 子模块的许可证需要单独
   审阅；本次沿用上游处理方式，未做修改。
2. **签名密钥**：用户自行生成 release.jks 才能发布；1.0.0 只提供
   调试构建。
3. **编译环境**：本容器无 Android SDK，未在容器内编译验证。发布前
   必须在 Android Studio 完整编译通过。

### 建议的下一步

- 在 Android Studio 建立独立工作副本，运行 `./gradlew assembleDebug`
  验证编译。
- 生成新的 release.jks 密钥对。
- 参照 `ROADMAP.md` 的 1.1.0 事项做依赖升级。
