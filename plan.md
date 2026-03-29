# 项目规划

## 项目概述
- **项目名称**: map-m25
- **包名**: app.map_m25
- **类型**: 仿高德地图 Android 离线单机应用
- **目标SDK**: Android 16 (API 36)
- **最低SDK**: Android 16 (API 36)

## 技术栈
- JDK 25
- Gradle 9.4.1
- Kotlin 2.3.20
- Jetpack Compose + Material 3
- MVVM + Clean Architecture
- Hilt 依赖注入
- Room 数据库
- DataStore 键值存储
- Coil 图片加载
- Navigation Compose

## 版本规划

### 0.1.0 (初始版本)
- [x] 搭建项目基础框架（脚手架代码）
- [x] 实现日志系统
- [x] 地图显示基础功能
- [x] 位置定位功能（本地模拟）
- [x] 搜索功能（本地数据）
- [x] 收藏夹功能
- [x] 路线规划功能（本地）
- [x] 设置功能

### 0.1.1 (修复版本)
- [x] 修复 SwapVert 图标引用错误

### 0.2.0 (功能完善)
- [x] 地图交互优化
  - [x] 缩放手势支持
  - [x] 拖拽手势支持
  - [x] 旋转手势支持
- [x] UI细节打磨

### 0.3.0 (体验提升)
- [x] 收藏夹管理优化
- [x] 历史记录功能
- [x] 折叠屏/平板适配
- [x] 120Hz流畅支持

### 0.3.1 (修复版本)
- [x] 修复 SearchScreen 回调类型不匹配问题

### 0.5.0 (新功能)
- [x] 地图标记功能（添加、编辑、删除地图标记）
- [x] 足迹记录功能（记录用户轨迹）
- [x] 坐标转换功能（度分秒↔十进制）

### 0.4.1 (修复版本)
- [x] 修复 MapScreen.kt 中 Straighten 图标引用错误
- [x] 修复 Double 与 Float 类型不匹配问题

### 0.4.0 (功能增强)
- [x] 地图图层切换（普通、卫星、 terrain）
- [x] 指南针与比例尺叠加层
- [x] 距离测量工具
- [x] 位置分享功能

### 0.5.1 (修复/重构版本)
- [x] 代码架构优化
  - [x] 移除未使用的导入
- [x] 性能改进

### 0.6.0 (新功能)
- [x] 地图标注分类管理
- [x] 轨迹统计分析
- [x] 数据导出功能

### 0.7.0 (新功能)
- [x] 离线地图区域管理（创建、编辑、删除离线区域）
- [x] 热点POI推荐（基于本地数据的热门地点）
- [x] 地图快照保存功能（将当前地图视图保存为图片）


### 0.7.1 (重构优化版本)
- [x] 代码架构优化
  - [x] 移除未使用的导入
  - [x] 优化重复计算逻辑
  - [x] 修复潜在空指针风险
- [x] UI细节打磨
  - [x] 优化ScaleBarOverlay显示逻辑

### 0.8.0 (个性化定制)
- [x] 深色主题支持
- [x] 个性化地图样式（多种配色方案）
- [x] 语音播报功能（导航播报、位置提醒）
- [x] 地图注记显示控制（POI、道路名称、交通标识）

### 0.8.1 (修复版本)
- [x] 修复 SettingsViewModel 中 combine 函数类型推断问题
- [x] 优化 loadSettings 函数，使用 combine 合并多个 Flow 减少重复代码

### 0.9.0 (导航增强)
- [x] 偏航提醒功能（偏离规划路线时提醒）
- [x] 模拟导航功能（模拟导航过程）
- [x] 导航面板优化（显示剩余距离、时间、转向信息）

### 0.9.1 (修复版本)
- [x] 修复 MapNavHost.kt 中 RouteType 引用错误
- [x] 修复 MapNavHost.kt 中 @Composable 上下文错误
- [x] 修复 MapViewModel.kt 中 Pair 类型不匹配错误
- [x] 修复 NavigationPanel.kt 中图标引用错误 (TurnLeft/TurnRight/Alert/WarningRed)
- [x] 修复 RouteScreen.kt 中 ButtonDefaults 引用错误

### 0.9.2 (修复版本)
- [x] 修复 MapApplication 缺少 @HiltAndroidApp 注解导致闪退
- [x] 修复 NavigationPanel.kt 中图标引用错误

### 0.9.3 (重构优化版本)
- [x] 代码架构优化
  - [x] 移除未使用的导入和依赖
  - [x] 提取重复代码到共享util函数
  - [x] 优化MapViewModel loadSettings使用combine合并Flow
  - [x] 统一错误处理模式
- [x] 性能优化
  - [x] TtsManager UtteranceProgressListener优化
- [x] 代码质量提升
  - [x] 添加必要的空安全检查
  - [x] 优化长函数，提取子函数提高可读性
  - [x] 统一代码风格和命名规范

### 0.10.0 (新功能版本)
- [ ] 3D地图视角
  - [ ] 添加地图倾斜手势支持
  - [ ] 添加3D视角切换按钮
  - [ ] 优化地图渲染支持3D透视效果
- [ ] 轨迹回放功能
  - [ ] 实现轨迹动画回放
  - [ ] 添加播放控制（播放/暂停/停止/倍速）
  - [ ] 显示回放进度和时间
- [ ] 标注导入导出功能
  - [ ] 支持KML格式导入导出
  - [ ] 支持GPX格式导入导出
  - [ ] 添加导入导出UI入口

## 已完成版本

### 0.9.3 (2026-03-29)
重构优化版本，包含以下改进：
- 新增 DistanceUtils 工具类，统一距离计算逻辑
- 新增 FormatUtils 工具类，统一格式化逻辑
- 优化 MapViewModel.loadSettings() 使用嵌套 combine 合并多个 Flow
- 删除重复的 calculateDistance 函数
- 优化 TrackStatsScreen 空安全检查，移除 double-bang 操作符
- 移除未使用的导入

### 0.9.2 (2026-03-29)
修复版本，包含以下修复：
- 修复 MapApplication 缺少 @HiltAndroidApp 注解导致的点击桌面图标闪退问题
- 修复 NavigationPanel.kt 中不存在的图标引用（TurnLeft/TurnRight/Alert）

### 0.9.1 (2026-03-29)
修复版本，包含以下修复：
- 修复 MapNavHost.kt 中 RouteType 导入路径错误
- 修复 MapNavHost.kt 中 @Composable 上下文问题
- 修复 MapViewModel.kt 中 Triple 数据结构导致类型不匹配错误
- 修复 NavigationPanel.kt 中 WarningRed 颜色定义问题
- 修复 RouteScreen.kt 中 ButtonDefaults 缺失导入问题

### 0.9.0 (2026-03-29)
导航增强版本，包含以下改进：
- 新增偏航提醒功能，偏离规划路线时自动提醒
- 新增模拟导航功能，可模拟导航过程
- 新增导航面板优化，显示剩余距离、时间、转向信息

### 0.8.1 (2026-03-29)
修复版本，包含以下修复：
- 修复 SettingsViewModel 中 combine 函数类型推断导致的编译错误
- 重构 loadSettings 函数，使用 combine 合并多个设置 Flow，减少代码重复
- 修复ScaleBarOverlay中cos函数参数类型问题
- 修正距离显示逻辑（百米显示）
- 优化MapViewModel中calculateDistance函数减少重复计算

### 0.6.0-0.8.0 (早期版本)
- 0.6.0: 地图标注分类管理、轨迹统计分析、数据导出
- 0.7.0: 离线地图区域管理、热点POI推荐、地图快照保存功能
- 0.7.1: 代码架构优化和UI细节打磨
- 0.8.0: 深色主题支持、个性化地图样式、语音播报功能、地图注记显示控制

## 问题追踪

## 更新日志
