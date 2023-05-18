# Android Screen Adaptation Gradle Plugin

English｜
[中文](https://github.com/zcys12173/ScreenMatchPlugin/blob/main/docs/README_CN.md)

Gradle plugin for screen adaptation in Android.

When coding XML, you only need to directly write values in dp/sp (currently only supports these two types) without manually defining them in dimens.xml, which improves coding efficiency.

This plugin scans XML files in the (layout/layout_xxx/drawable/drawable_xxx) directories and replaces them with the "@dimens/dp_xx" format. It inserts the scanned dimen values into dimens.xml and generates adaptation files based on the Android minimum width principle (e.g., values-swXXXdp/dimens.xml).

## Dependency
root/build.gradle
```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.zcys12173.plugins:screen-match-plugin:1.0.1"
  }
}
```
## Usage
It is recommended to include the plugin for generating adaptation dimens.xml files in the bottom-level module that is depended upon by the entire project. This makes it easier for other modules to reference.

Here's an example code for the "other-module" module:

other-module.gradle
```gradle

apply plugin: 'io.github.zcys12173.ScreenMatch'

screenMatch {
    baseValue = 375
    matchSizes = [320,360,375,384,392,400,410,411]
}

```
Available configuration options:

```gradle
abstract class ScreenMatchExtension {
    var baseValue: Int? = null // Base value, usually the width in dp from the UI design draft
    var matchSizes: Array<Int>? = null // Sizes in dp to be adapted
    var prefix: String = ""  // Prefix for generating the name of dimen, e.g., "<dimen name="{prefix}{dp/sp}_11">11dp</dimen> ". If not set, the default is "{dp/sp}_11"
    var onlyCurProject: Boolean = false // Whether to adapt only the current module
    var matchType: String? = "SW" // Type of the generated dimens.xml folder, supports SW (smallest width), W (window width), H (window height)
    var autoRunWithPacking: Boolean = false // Automatically run during apk packaging
    var taskName: String = "preBuild" // The adaptation task will run before this task. Only effective when [autoRunWithPacking] is true. Default: preBuild
    var excludes: Array<String> = arrayOf() // Excluded scanning folders or files. The plugin defaults to scanning all subprojects under the project
    var logEnabled: Boolean = false // Whether to print logs
}

```
## Run task
1.Command Line
```shell
./gradlew other-module:scanAndCreateDimens  
```

2.Visual

![Image text](https://raw.githubusercontent.com/zcys12173/ScreenMatchPlugin/main/images/task_position.png)

3.Task Explanation

scanAndCreateDimens: Scans and generates adapted files.

scanXmlFiles: Scans XML files and generates the base dimens.xml.

createMatchFiles: Generates values-swXXXdp/dimens.xml files based on the base dimens.xml.

It is recommended to directly use the scanAndCreateDimens task. You can also execute the scanXmlFiles task first, and then the createMatchFiles task.
