# ScreenMatchPlugin
用于Android屏幕适配的Gradle插件，生成的文件是基于Andorid最小宽度适配原则

## 引用
审核中...

## 配置
建议在整个工程的最底层依赖的module引入插件生成适配dimens.xml文件，这样方便其他的module引用。（下面代码示例都用“other-module”模块）

other-module.gradle
```gradle

    apply plugin: 'screen_match'

    screenMatch {
        baseValue = 375
        matchSizes = [320,360,375,384,392,400,410,411]
    }

```
可配置参数
```gradle
    abstract class ScreenMatchExtension {
        var baseValue: Int? = null //基准值，一般使用UI设计稿上的宽度dp
        var matchSizes: Array<Int>? = null //要适配的尺寸dp
        var prefix: String = ""  //生成dimen的name的前缀,例:"{prefix}{dp/sp}_100 ",如果未设置，则默认未"{dp/sp}_100"
        var onlyCurProject:Boolean = false //是否只对当前module进行适配
    }
```
## 使用
1.命令行
```shell
    ./gradlew other-module:scanAndCreateDimens
```
