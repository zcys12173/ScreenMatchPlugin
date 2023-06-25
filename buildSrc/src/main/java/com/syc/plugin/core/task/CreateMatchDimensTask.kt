package com.syc.plugin.core.task

import com.syc.plugin.LogUtil
import com.syc.plugin.core.DimensFileIO
import com.syc.plugin.core.ScreenMatchExtension
import com.syc.plugin.core.cache.runWhenModified
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class CreateMatchDimensTask @Inject constructor(private val config: ScreenMatchExtension) :
    DefaultTask() {
    init {
        group = "screenMatch"
        description = "create match dimens.xml file"
    }

    @TaskAction
    fun onAction() {
        if (config.matchSizes.isNullOrEmpty() || config.baseValue == null || config.matchType.isNullOrEmpty()) {
            throw IllegalArgumentException("请配置matchSizes、baseValue、matchType")
        }

        val baseDimensPath = "${project.projectDir}/src/main/res/values/dimens.xml"
        val createBlock = {
            val dimens = DimensFileIO.readDimens(baseDimensPath)
            config.matchSizes!!.forEach {
                val dimenIO = DimensFileIO()
                val dimensPath =
                    "${project.projectDir}/src/main/res/values-${config.matchType!!.toLowerCase()}${it}dp/dimens.xml"
                val ratio = it / config.baseValue!!.toFloat()
                dimenIO.clear(dimensPath)
                dimenIO.open(dimensPath)
                dimens.forEach { dimen ->
                    val regEx = "[0-9]+\\.?[0-9]*"
                    val matchResult = regEx.toRegex().find(dimen.value)
                    matchResult?.run {
                        val unit = dimen.value.substring(range.last + 1)
                        val newValue = value.toFloat() * ratio
                        //截取newValue小数点后4位有效数字
                        val formatValue = String.format("%.4f", newValue)
                        dimenIO.write(dimen.name, "$formatValue$unit")
                    }
                }
                dimenIO.close()
                LogUtil.log("生成文件:$dimensPath")
            }
        }
        if(config.useCache){
            File(baseDimensPath).runWhenModified {
                createBlock()
            }
        }else{
            createBlock()
        }

    }
}