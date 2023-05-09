# ScreenMatchPlugin
用于Android屏幕适配的Gradle插件

## 引用
todo

## 配置
建议在整个工程的最底层依赖的module引入插件生成适配dimens.xml文件，这样方便其他的module引用。

```gradle

apply plugin: 'screen_match'

screenMatch {
    baseValue = 375
    matchSizes = [320,360,375,384,392,400,410,411]
}

```

