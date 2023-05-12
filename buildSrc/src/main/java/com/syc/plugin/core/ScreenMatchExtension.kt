package com.syc.plugin.core

abstract class ScreenMatchExtension {
    var baseValue: Int? = null //基准值，一般使用UI设计稿上的宽度dp
    var matchSizes: Array<Int>? = null //要适配的尺寸dp
    var prefix: String = ""  //生成dimen的name的前缀,例:"{prefix}{dp/sp}_100 ",如果未设置，则默认未"{dp/sp}_100"
    var onlyCurProject:Boolean = false //是否只对当前module进行适配
    var matchType: String? = "SW" //暂时仅支持"SW"(最少宽度)，后续会支持"W，H"
    var autoRunWithPacking:Boolean = false //打包apk时自动运行
    var taskName:String = "preBuild" // 适配Task 会运行在该任务之前。[autoRunWithPacking]为true的时候生效
    var excludes:Array<String> = arrayOf() //扫描xml的时候排除的文件夹
    var logEnabled:Boolean = false //是否打印日志
}