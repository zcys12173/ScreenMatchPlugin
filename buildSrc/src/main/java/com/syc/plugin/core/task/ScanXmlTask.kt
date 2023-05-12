package com.syc.plugin.core.task

import com.syc.plugin.LogUtil
import com.syc.plugin.core.DimensFileIO
import com.syc.plugin.core.ScreenMatchExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.FileSystems
import javax.inject.Inject

abstract class ScanXmlTask @Inject constructor(private val config: ScreenMatchExtension) : DefaultTask() {

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
        val scanProject = scan@{ project: Project ->
            if (project.extensions.findByName("android") == null) {
                LogUtil.log("${project.name}模块不是android模块，跳过扫描")
                return@scan
            }
            LogUtil.log("开始扫描${project.name}模块")
            val inputDir = "${project.projectDir}/src/main/res"
            File(inputDir).takeIf { it.exists() }?.run {
                loopResFiles(this)
            }
            LogUtil.log("扫描${project.name}模块完成")
        }
        if (config.onlyCurProject) {
            scanProject(project)
        } else {
            project.rootProject.subprojects.forEach {
                scanProject(it)
            }
        }

        if (scanResults.isNotEmpty()) {
            LogUtil.log("开始写入dimens.xml文件")
            writeToDimensFile(project)
            LogUtil.log("写入dimens.xml文件完成")
            scanResults.clear()
        }
    }


    private fun loopResFiles(resFile: File) {
        if (resFile.isDirectory) {
            resFile.listFiles()?.forEach {
                loopResFiles(it)
            }
        } else {
            if (filterFiles(resFile)) {
                scanFileContent(resFile)
            }
        }
    }

    /**
     * 扫码xml文件，并替换dp为引用方式 如：10dp -> @dimen/dp_10
     */
    private fun scanFileContent(file: File) {
        LogUtil.log("开始检测 ${file.absolutePath} 文件")
        var content = file.readText()
        if(ignoreContent(content)){
            return
        }
        val matchResult = regex.toRegex().findAll(content)
        var offset = 0
        matchResult.forEach {
            LogUtil.log("匹配到的内容：${it.value}")
            val type = if (it.value.contains("dp")) DimensUnit.DP else DimensUnit.SP
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
    private fun writeToDimensFile(project: Project) {
        val path = "${project.projectDir}/src/main/res/values/dimens.xml"
        val io = DimensFileIO()
        io.open(path)
        scanResults.forEach {
            io.write(it.getName(), it.value + it.unit.value)
        }
        io.close()
    }

    /**
     * 过滤文件，drawable和layout目录下的xml文件
     */
    private fun filterFiles(file: File): Boolean {
        return isValidXMl(file) && !isExclude(file)
    }

    /**
     * 是否为有效的xml文件
     */
    private fun isValidXMl(file:File) = file.name.endsWith(".xml") && (file.path.contains("drawable") || file.path.contains("layout"))

    /**
     * 是否应该被排除
     */
    private fun isExclude(file: File):Boolean{
        return config.excludes.any {
            val matcher = FileSystems.getDefault().getPathMatcher("glob:$it")
            matcher.matches(file.toPath())
        }

    }

    private fun ignoreContent(content: String): Boolean {
        return content.trim().endsWith("</vector>")//忽略矢量图xml文件
    }

    inner class ScanResult(val value: String, val unit: DimensUnit) {
        /**
         * 在dimens.xml中声明的name
         */
        fun getName(): String {
            return "${config.prefix}${unit.value}_${value.replace(".", "_")}"
        }

        /**
         * 在xml中引用的路径
         */
        fun getRefPath(): String {
            return "@dimen/${getName()}"
        }

    }

    enum class DimensUnit(val value: String) {
        DP("dp"), SP("sp")
    }
}