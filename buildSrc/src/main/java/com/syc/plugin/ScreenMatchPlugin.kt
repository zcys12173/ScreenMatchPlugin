package com.syc.plugin

import com.syc.plugin.core.ScreenMatchExtension
import com.syc.plugin.core.task.ScanTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScreenMatchPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        LogUtil.log("ScreenMatchPlugin apply")
        val config = project.extensions.create("screenMatch", ScreenMatchExtension::class.java)
        project.tasks.create("scan", ScanTask::class.java,true)
    }
}