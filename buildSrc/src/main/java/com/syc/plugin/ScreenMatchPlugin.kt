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
        val scanTask = project.tasks.create("scanXmlFiles", ScanXmlTask::class.java,false)
        val createTask = project.tasks.create("createMatchFiles", CreateMatchDimensTask::class.java,config)
        project.tasks.create("scanAndCreateDimens").apply {
            group = "screenMatch"
            description = "scan xml file and create matched dimens.xml"
            createTask.shouldRunAfter(scanTask)
            dependsOn(scanTask,createTask)
        }
    }
}