# Android屏幕适配Gradle插件  

[中文](https://github.com/zcys12173/ScreenMatchPlugin/blob/main/docs/README_CN.md)｜[English](https://github.com/zcys12173/ScreenMatchPlugin/blob/main/README.md)

用于Android屏幕适配的Gradle插件  

在coding写xml的时候只需要直接写入dp/sp(目前仅支持这两种)值，无需手动在dimens.xml定义，提升编码效率  

该插件会扫描(layout/layout_xxx/drawable/drawable_xxx)目录下的xml文件，替换成“@dimens/dp_xx”方式，在dimens.xml中插入扫描到的dimen值，然后再基于Andorid最小宽度适配原则生成适配文件(如 values-swXXXdp/dimens.xml)

## 引用
审核中...

## 配置
建议在整个工程的最底层依赖的module引入插件生成适配dimens.xml文件，这样方便其他的module引用  

下面代码示例都用“other-module”模块

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

2.可视化  
  
![Image text](https://raw.githubusercontent.com/zcys12173/ScreenMatchPlugin/main/images/task_position.png)  

3.Task说明  

scanAndCreateDimens:扫描+生成适配后的文件  

scanXmlFiles.      :扫描xml+生成基准的dimens.xml  

createMatchFiles   :根据基准的dimens.xml生成各种尺寸下的values-swXXXdp/dimens.xml文件  

建议直接使用scanAndCreateDimens任务。也可以先执行scanXmlFiles任务，然后在执行createMatchFiles任务  


## 自动集成-CI  

在根目录下的build.gradle中添加  

```gradle
    allprojects {
        ...
        
        afterEvaluate{
            Task matchTask = project.tasks.findByName("scanAndCreateDimens")
            if(matchTask){
                Task preBuildTask = project.tasks.findByName("preBuild")
                preBuildTask?.dependsOn(matchTask)
            }
        }
    }
```

后续会考虑直接把该逻辑放到插件中，增加开关配置

## TODO

* [ ] 支持px,dpi
* [ ] 插件内支持CI