# 乐家桌面 JSON 接口演示文档

所有接口示例数据均存放于 `src/lejia/src/main/assets/api/`，无需真实网络即可浏览完整结构。

## 接口目录

目录定义：`assets/api/index.json`

## 接口列表（25个）

| Key | 名称 | 方法 | 路径 | 示例文件 |
|---|---|---|---|---|
| recommend | 首页推荐 | POST | /api.php/home/recommend | api/recommend.json |
| topics | 精选专题 | POST | /api.php/home/topics | api/topics.json |
| classify | 影视分类 | POST | /api.php/movie/classify | api/classify.json |
| vodlist | 影片列表 | POST | /api.php/vod/list | api/vodlist.json |
| voddetail | 影片详情 | POST | /api.php/vod/detail | api/voddetail.json |
| tvstation | 直播频道 | POST | /api.php/tv/station | api/tvstation.json |
| liveepg | 直播节目单 | POST | /api.php/live/epg | api/liveepg.json |
| apps | 推荐应用 | POST | /api.php/app/recommend | api/apps.json |
| wallpaper | 壁纸列表 | POST | /api.php/wallpaper/list | api/wallpaper.json |
| update | 版本更新 | POST | /api.php/system/update | api/update.json |
| notice | 桌面公告 | POST | /api.php/system/notice | api/notice.json |
| user | 用户信息 | POST | /api.php/user/info | api/user.json |
| search | 搜索 | POST | /api.php/vod/search | api/search.json |
| playurl | 播放地址 | POST | /api.php/vod/playurl | api/playurl.json |
| history | 观看历史 | POST | /api.php/user/history | api/history.json |
| collect | 我的收藏 | POST | /api.php/user/collect | api/collect.json |
| config | 客户端配置 | GET | /api.php/system/config | api/config.json |
| filter | 筛选条件 | POST | /api.php/vod/filter | api/filter.json |
| p2p | P2P 源列表 | POST | /api.php/live/p2p | api/p2p.json |
| upload | 统计上报 | POST | /api.php/system/upload | api/upload.json |
| about | 关于信息 | GET | /api.php/system/about | api/about.json |
| menu | 桌面菜单 | GET | /api.php/home/menu | api/menu.json |
| ad | 广告位 | POST | /api.php/ad/list | api/ad.json |
| shortcut | 快捷入口 | GET | /api.php/home/shortcut | api/shortcut.json |

## 使用方式

1. 打开乐家桌面，切换到「JSON 接口」页查看目录。
2. 点击任意接口进入详情页，查看路径、方法、参数、响应 JSON、curl 示例。
3. 所有数据来源于 assets，无需联网即可完整预览。

## 注意

原项目中 `HomeActivity/Api/Constant` 等模块使用了加密逻辑，本模块仅做 JSON 结构演示，不复用敏感加密配置。
