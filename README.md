# map-m25

仿高德地图 Android 离线单机应用

## 功能特性

- 地图显示与交互
- 位置定位
- 地点搜索
- 收藏夹管理
- 路线规划（驾车、步行、骑行）
- 设置功能

## 技术栈

- Kotlin 2.3.20
- Jetpack Compose + Material 3
- MVVM + Clean Architecture
- Hilt 依赖注入
- Room 数据库
- DataStore 键值存储
- Coil 图片加载
- Navigation Compose

## 版本

当前版本: 0.2.0

### 0.2.0 (2026-03-29)
- 新增地图旋转手势支持（双指旋转）
- 添加旋转角度状态管理和重置功能

### 0.1.1 (2026-03-29)
- 修复 SwapVert 图标引用错误

### 0.1.0 (2026-03-28)
初始版本发布

## 构建

```bash
./gradlew assembleDebug
```

## 开发

本应用为离线单机使用，不包含任何网络功能。
