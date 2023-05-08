package com.syc.plugin.core.task

import com.syc.plugin.LogUtil
import com.syc.plugin.core.createDimensFile
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class ScanTask @Inject constructor(private val isOnlyCurProject: Boolean) : DefaultTask() {

    //匹配xml中的dp和sp 如：10dp 10sp
    private val regex = "\"[0-9]+\\.?[0-9]*(dp|sp)\""

    //从xml中扫描到的所有结果，稍后会写入到dimens.xml文件中，使用Set去重
    private val scanResults = mutableSetOf<ScanResult>()

    init {
        group = "screenMatch"
        description = "scan res files and generate dimens.xml file"

    }

    @TaskAction
    fun action() {
        val handle = { project:Project ->
            LogUtil.log("开始处理${project.name}模块")
            val inputDir = "${project.projectDir}/src/main/res"
            File(inputDir).takeIf { it.exists() }?.run {
                loopFiles(this)
                if (scanResults.isNotEmpty()) {
                    writeToDimensFile()
                    scanResults.clear()
                }
            }
        }
        if(isOnlyCurProject){
            handle(project)
        }else{
            project.rootProject.subprojects.forEach {
                handle(it)
            }
        }
    }


    private fun loopFiles(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                loopFiles(it)
            }
        } else {
            if (isTargetFile(file)) {
                println(file.absolutePath)
                scanFileContent(file)
            }
        }
    }

    /**
     * 扫码xml文件，并替换dp为引用方式 如：10dp -> @dimen/dp_10
     */
    private fun scanFileContent(file: File) {
        LogUtil.log("开始检测 ${file.absolutePath} 文件")
        var content = file.readText()
        val matchResult = regex.toRegex().findAll(content)
        var offset = 0
        matchResult.forEach {
            LogUtil.log("匹配到的内容：${it.value}")
            val type = if (it.value.contains("dp")) DimensType.DP else DimensType.SP
            val value = it.value.substring(1, it.value.length - 3)
            val startIndex = it.range.first + 1 + offset //去掉引号
            val endIndex = it.range.last + offset //去掉引号
            if (content.substring(startIndex, endIndex) == value + type.value) {
                val element = ScanResult(value, type)
                scanResults.add(element)
                offset += element.getRefPath().length - it.value.length + 2  //因为匹配出得字符含有双引号，所以要加2
                content = content.replaceRange(startIndex, endIndex, element.getRefPath())
            } else {
                LogUtil.log("验证未通过")
            }
        }
        file.writeText(content)
    }


    /**
     * 把扫描到的dp 写入到dimens.xml中
     */
    private fun writeToDimensFile() {

        val dimensFile = File("${project.projectDir}/src/main/res/values/dimens.xml")
        createDimensFile(dimensFile.path)
        val content = dimensFile.readText()
        val insetIndex = content.indexOf("</resources>")
        val sb = StringBuilder(content)
        scanResults.forEach {
            if (!sb.contains("<dimen name=\"${it.getName()}\">")) {
                sb.insert(
                    insetIndex,
                    "\n <dimen name=\"${it.getName()}\">${it.value.toFloat()}${it.type.value}</dimen> \n"
                )
            }

        }
        dimensFile.writeText(sb.toString())
    }

    /**
     * 过滤文件，drawable和layout目录下的xml文件
     */
    private fun isTargetFile(file: File): Boolean {
        return file.name.endsWith(".xml") && (file.path.contains("drawable") || file.path.contains("layout"))
    }

    data class ScanResult(val value: String, val type: DimensType) {
        /**
         * 在dimens.xml中声明的name
         */
        fun getName(): String {
            return "${type.value}_${value.replace(".", "_")}"
        }

        /**
         * 在xml中引用的路径
         */
        fun getRefPath(): String {
            return "@dimen/${getName()}"
        }

    }

    enum class DimensType(val value: String) {
        DP("dp"), SP("sp")
    }
}