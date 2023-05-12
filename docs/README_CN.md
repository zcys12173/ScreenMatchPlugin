# Android屏幕适配Gradle插件  

中文｜[English](https://github.com/zcys12173/ScreenMatchPlugin/blob/main/README.md)

用于Android屏幕适配的Gradle插件  

在coding写xml的时候只需要直接写入dp/sp(目前仅支持这两种)值，无需手动在dimens.xml定义，提升编码效率  

该插件会扫描(layout/layout_xxx/drawable/drawable_xxx)目录下的xml文件，替换成“@dimens/dp_xx”方式，在dimens.xml中插入扫描到的dimen值，然后再基于适配原则生成适配文件(如 values-swXXXdp/dimens.xml)

## 引用
根目录build.gradle  

```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.zcys12173.plugins:screen-match-plugin:1.0.0"
  }
}
```

## 配置
建议在整个工程的最底层依赖的module引入插件生成适配dimens.xml文件，这样方便其他的module引用  

下面代码示例都用“other-module”模块

other-module.gradle
```gradle
apply plugin: 'io.github.zcys12173.ScreenMatch'

screenMatch {
    baseValue = 360  //基准值，一般使用UI设计稿上的宽度dp
    matchSizes = [240,320,360,375,384,392,400,410,411,432,480,533,592,600,640,662,720,768,800,811,820,960,961,1024,1024,1280,1365] //要适配的尺寸dp
    autoRunWithPacking = true
    excludes = ['**/flutter/**','**/python-**/**','**/spark-**/**','**/video_player_android/**','**/webview_flutter_android/**']
}
```  

配置参数说明
```gradle
abstract class ScreenMatchExtension {
    var baseValue: Int? = null //基准值，一般使用UI设计稿上的宽度dp
    var matchSizes: Array<Int>? = null //要适配的尺寸dp
    var prefix: String = ""  //生成dimen的name的前缀,例:"<dimen name="{prefix}{dp/sp}_11">11dp</dimen> ",如果未设置，则默认未"{dp/sp}_11"
    var onlyCurProject:Boolean = false //是否只对当前module进行适配
    var matchType: String? = "SW" //生成适配dimens.xml文件夹类型，支持 SW（屏幕最小宽度）、W（窗口宽度）、H（窗口高度）
    var autoRunWithPacking:Boolean = false //打包apk时自动运行
    var taskName:String = "preBuild" // 适配Task 会运行在该任务之前。[autoRunWithPacking]为true的时候生效。默认preBuld
    var excludes:Array<String> = arrayOf() //排除的扫描文件夹或者文件。插件默认是扫描该工程下的所有的子工程
}
```  

## 使用

1.命令行  

```shell
./gradlew other-module:scanAndCreateDimens
```

2.可视化  
  
![Image text](https://raw.githubusercontent.com/zcys12173/ScreenMatchPlugin/main/images/task_position.png)  

3.Task说明  

scanAndCreateDimens:扫描+生成适配后的文件  

scanXmlFiles.      :扫描xml+生成基准的dimens.xml  

createMatchFiles   :根据基准的dimens.xml生成各种尺寸下的values-swXXXdp/dimens.xml文件  

建议直接使用scanAndCreateDimens任务。也可以先执行scanXmlFiles任务，然后在执行createMatchFiles任务  


## 自动集成-CI  
目前已经支持通过配置autoRunWithPacking来控制打包自动运行

## TODO

* [ ] 支持px,dpi
* [ ] 增加日志开关
