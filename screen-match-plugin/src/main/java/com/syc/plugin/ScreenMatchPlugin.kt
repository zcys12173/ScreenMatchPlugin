package com.syc.plugin

import com.syc.plugin.core.ScreenMatchExtension
import com.syc.plugin.core.task.CreateMatchDimensTask
import com.syc.plugin.core.task.ScanXmlTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScreenMatchPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        LogUtil.log("ScreenMatchPlugin apply")
        val config = project.extensions.create("screenMatch", ScreenMatchExtension::class.java)
        val scanTask = project.tasks.create("scanXmlFiles", ScanXmlTask::class.java,config)
        val createTask = project.tasks.create("createMatchFiles", CreateMatchDimensTask::class.java,config)
        val scanAndCreate = project.tasks.create("scanAndCreateDimens").apply {
            group = "screenMatch"
            description = "scan xml file and create matched dimens.xml"
            createTask.shouldRunAfter(scanTask)
            dependsOn(scanTask,createTask)
        }
        project.afterEvaluate{
            if(config.autoRunWithPacking){
                project.tasks.findByName(config.taskName)?.run {
                    LogUtil.log("开启打包自动生成适配文件")
                    dependsOn(scanAndCreate)
                }
            }
        }
    }
}