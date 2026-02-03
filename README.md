# RTSP Player App (Android)

（已更新：签名流程与ZIP打包说明）

**功能概览**
- RTSP 播放器，基于 ExoPlayer + RTSP 扩展
- Material Design：采用官方 Material3 + Compose UI
- 支持设置：
  - 编解码：H264（默认）、H265
  - 分辨率：720p（默认）、1080p
  - 帧率：20、25、30（默认）、50、60
  - 账号/密码（默认 admin/admin）
  - IP、端口（默认 1945）、流路径（默认 stream）
- 显示并构造 RTSP 流地址
- 启动/停止播放按钮

---

**环境与依赖**
- 语言：Kotlin
- UI：Jetpack Compose（Material3）
- 播放：ExoPlayer 2.19.1 + RTSP 扩展
- 编译：Android Gradle Plugin ≥ 8.3，Gradle 8.7，JDK 17，compileSdk=34，minSdk=29（Android 10+）

---

**GitHub Actions 自动构建 APK（无需本地环境）**
- 项目内包含 `.github/workflows/build.yml` 与 Gradle Wrapper，可在 GitHub 上自动构建并产出 Debug 与 Release APK。
- 步骤：
  1. 将整个项目上传到你的 GitHub 仓库（新建仓库并推送）
  2. 进入仓库的 Actions 页面，运行 `Android CI Build` 工作流
  3. 在构建任务的 Artifacts 区域下载 `app-debug-apk` 或 `app-release-apk`

---

**签名与发布（本地/CI）**
- 本地生成 keystore（示例）：
```
keytool -genkey -v -keystore release.keystore -alias probe -keyalg RSA -keysize 2048 -validity 3650
```
- 复制模板文件：将 `signing.properties.template` 复制为 `signing.properties` 并填入你的真实值：
```
storeFile=release.keystore
storePassword=你的密码
keyAlias=probe
keyPassword=你的密码
```
- 构建发布版（签名）：
```
./gradlew assembleRelease
```
- CI（GitHub Actions）签名：将 keystore 以 Base64 存入仓库 Secrets：
  - `KEYSTORE_BASE64`：keystore 的 Base64 文本
  - `KEYSTORE_PASSWORD`：store 密码
  - `KEY_ALIAS`：密钥别名
  - `KEY_PASSWORD`：密钥密码
  工作流会自动：
  1. 解码 keystore 到 `release.keystore`
  2. 生成 `signing.properties`
  3. 运行 `assembleRelease` 并上传 `app-release-apk`

---

**ZIP 打包下载**
- 我会为你打包整个项目为 `rtsp-player-app.zip`，你可以直接下载并上传到 GitHub。

---

**注意事项**
- 播放器无法强制改变源端的编解码、分辨率或帧率；这些设置将拼接到 URL 的查询参数中，设备支持则生效。
- URL 示例：`rtsp://admin:admin@192.168.1.100:1945/stream?codec=h264&resolution=720p&fps=30`
- 请妥善保管你的 keystore 与密码，不要提交到公共仓库（.gitignore 已忽略）。

---

**二次开发建议**
- 持久化设置到 DataStore
- 适配不同摄像头品牌参数
- 错误提示、缓冲状态、重试/重连机制
- 横屏/全屏、音量控制/静音、截图等增强
