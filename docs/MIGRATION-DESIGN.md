# HMCL-PE 2.0.8 → HMCL-PE-R 1.0.0 迁移设计

## 目标

把 Tungstend 的 HMCL-PE 2.0.8（已停止维护）迁移为一个可持续维护的社区
fork，同时保持 GPLv3 合规与向后兼容。

## 设计原则

1. **最小改动**：除品牌、版本、密钥之外，运行时逻辑不做修改。
2. **可回滚**：所有改动集中在少量文件（build.gradle、settings.gradle、
   strings.xml、launcher_version.json），便于 diff 与回滚。
3. **可演进**：为后续迁移到 HMCL 桌面 3.17 的设计预留空间（包名分区、
   独立 release 通道）。
4. **可审计**：所有二改决策记录在 `DERIVED-FROM.md` 与本文档。

## 变更影响矩阵

| 变更项 | 影响范围 | 风险等级 |
| ------ | -------- | -------- |
| applicationId 变更 | 存储路径、SharedPreferences、SAF URI | 低（安装后独立目录） |
| versionName/Code 变更 | 更新系统识别为独立 app | 低 |
| strings.xml 品牌更新 | 显示文案 | 低 |
| 签名密钥重生成 | 与上游 app 无法共存升级 | 中（需卸载重装） |
| 关于页新增 fork 说明 | 用户识别 | 低 |
| 移除硬编码密钥密码 | 影响 release 构建流程 | 中（需配置环境变量） |

## 数据存储兼容性

- 旧版用户目录：`/storage/emulated/0/Android/data/com.tungsten.hmclpe/`
- 新版用户目录：`/storage/emulated/0/Android/data/com.hmclpe.renewed/`
- **应用不能自动迁移旧目录**（Android Scoped Storage 限制），用户需要
  手动将实例目录复制到新位置。

## 关于与旧版并存的策略

由于 `applicationId` 变化，旧版 HMCL-PE 与本 fork 可以并存安装。

- 用户若已在用 HMCL-PE，可以并存试用本 fork。
- 迁移后建议卸载旧版以释放存储。

## 构建产物

- Debug APK：`HMCLPE/build/outputs/apk/debug/HMCLPE-debug.apk`
- Release APK：`HMCLPE/build/outputs/apk/release/HMCLPE-release.apk`

## 发布流程（1.0.0）

1. 生成新 release 密钥对：
   ```
   keytool -genkeypair -v \
       -keystore HMCLPE/release.jks \
       -alias renewed \
       -keyalg RSA -keysize 2048 -validity 10000
   ```
2. 设置环境变量：
   ```
   export HMCLR_KEYSTORE_PASS=<generated>
   export HMCLR_KEY_ALIAS=renewed
   export HMCLR_KEY_PASS=<generated>
   ```
3. 构建 release：`./gradlew assembleRelease`
4. 上传 APK 到 GitHub Releases 的 `v1.0.0` 标签下。

## 版本语义

- 主版本（1.0.0 → 2.0.0）：破坏性 API 或包名迁移。
- 次版本（1.0.0 → 1.1.0）：新增功能、依赖升级。
- 补丁版本（1.0.0 → 1.0.1）：仅 bug 修复。
