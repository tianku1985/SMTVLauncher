# 乐家桌面开发文档

## 项目定位

在 `src/` 下新建 `lejia/` 模块，实现一个类似乐家桌面的 TV 桌面应用，包含：

- 一键切换主题（全局即时生效，持久化）
- 全量 JSON 接口 Demo（25 个接口，全部本地 assets）
- 兼容 Android 4.4（minSdkVersion 19）
- 开发文档（API.md / THEME.md / DEVELOPMENT.md）

## 模块结构

```
src/lejia/
├── build.gradle
├── proguard-rules.pro
├── docs/
│   ├── API.md
│   ├── DEVELOPMENT.md
│   └── THEME.md
└── src/main/
    ├── AndroidManifest.xml
    ├── assets/
    │   ├── themes.json
    │   └── api/*.json
    ├── java/com/lejia/launcher/
    │   ├── HomeActivity.java
    │   ├── ApiDetailActivity.java
    │   ├── LauncherApp.java
    │   ├── ThemeManager.java
    │   ├── Themeable.java
    │   ├── Prefs.java
    │   ├── JsonUtils.java
    │   ├── ApiRepository.java
    │   ├── AppRepository.java
    │   ├── model/*.java
    │   ├── ui/Ui.java, AppGridAdapter.java
    │   └── page/*.java
    └── res/
        ├── mipmap-*/ic_launcher.png
        └── values/{strings,styles}.xml
```

## 技术栈

- Android 原生 API（无 AndroidX、无 Jetpack Compose）
- 纯代码 UI（LinearLayout/FrameLayout/ScrollView/GridView/RadioGroup）
- org.json（Android 自带）
- SharedPreferences
- 异步应用扫描（Thread + Handler）

## 兼容性

- minSdkVersion: 19（Android 4.4）
- targetSdkVersion: 23
- compileSdkVersion: 30
- buildToolsVersion: 28.0.3
- 支持 landscape（TV/大屏）
- usesCleartextTraffic: true（便于本地演示）
- 支持无触屏设备（uses-feature touchscreen required=false）

## 构建

在 `src/` 目录下：

```bash
./gradlew :lejia:assembleDebug
./gradlew :lejia:assembleRelease
```

或在 Android Studio 中打开 `src/` 工程，选择 `lejia` 模块。

## GitHub 自动编译

工作流文件：`.github/workflows/lejiadesk.yml`

触发条件：
- push 到 master/main
- pull_request 到 master/main
- workflow_dispatch

构建流程（沿用原项目思路）：
1. JDK 17 安装 Android SDK（platforms;android-30, build-tools;28.0.3）
2. 切换 JDK 8
3. 使用 Gradle 5.1.1（需 JDK 8）构建 `:lejia:assembleRelease`
4. 使用 build-tools 28.0.3 zipalign/apksigner 签名（复用仓库 keystore）
5. 上传 Artifact 并发布到 Release（LejiaLauncher-latest）

## 使用说明

1. 安装 APK 后可作为桌面（HOME/LAUNCHER）
2. 首页：推荐应用、热门推荐、精选专题、公告
3. 应用页：全量应用网格、搜索
4. 影音页：分类、影片、直播、详情（均基于 JSON）
5. 主题页：一键换肤、预览色板
6. 设置页：配置、文档、关于
7. JSON 接口页：目录、详情、响应、curl

## 注意事项

- 所有接口数据仅用于演示，不发起真实请求
- 应用列表扫描需 `QUERY_ALL_PACKAGES` 权限（Android 11+ 部分 ROM 需手动授权，但仍可浏览）
- 避免使用 AndroidX API，保证 Android 4.4 兼容
- 纯代码 UI，不依赖额外资源文件，便于移植
