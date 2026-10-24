# 乐家桌面主题系统

## 概述

乐家桌面内置「一键切换主题」功能，支持全局即时生效、自动持久化、弱引用通知。

## 主题配置

配置文件：`assets/themes.json`

字段说明：

| 字段 | 说明 |
|---|---|
| id | 主题唯一标识 |
| name | 显示名称 |
| slogan | 简短描述 |
| bgStart/bgEnd | 顶部/背景渐变 |
| surface/surfaceAlt | 卡片背景色 |
| accent/accentAlt | 强调色/渐变色 |
| textPrimary/textSecondary | 主/次文本色 |
| stroke | 边框色 |
| navActive | 导航激活色 |
| badge | 标记色 |
| dark | 是否深色主题 |

## 内置主题（6套）

- 曜石黑金（obsidian）
- 极光霓虹（aurora）
- 赤霞烈焰（crimson）
- 翠玉清新（emerald）
- 紫罗兰梦（violet）
- 暮色橘光（sunset）

## 使用方式

1. 首页点击「一键换肤」或顶部「一键换肤」按钮
2. 切换到「主题」页，可手动选择任意主题，或点击「一键切换下一套」
3. 支持按菜单键（MENU）快速换肤
4. 切换后全局即时刷新，重启后仍保持上次选择

## 技术实现

- `ThemeManager`：单例，负责加载、切换、持久化（SharedPreferences）、通知
- `Themeable`：Activity/Page 实现接口接收换肤事件
- `Prefs`：轻量配置存储
- `Ui`：纯代码绘制圆角、渐变、焦点状态，兼容 Android 4.4

## 注意

主题仅影响乐家桌面模块（`com.lejia.launcher`），不影响原 `src/app` 模块。
