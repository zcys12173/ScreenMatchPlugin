package com.syc.plugin.core.cache

import com.syc.plugin.LogUtil
import org.gradle.api.Project
import java.io.File
import java.nio.charset.Charset


object CacheManager{
    private lateinit var cacheFile:File

    private val cacheMap = mutableMapOf<String,Long>()

    private var hasLoadCache = false

    fun init(project: Project){
        cacheFile = File(project.buildDir,"screenMatch/cache.txt")
    }

    fun getFromCache(path:String):Long?{
        return if(!cacheFile.exists()){
            null
        }else{
            if(!hasLoadCache){
                loadCache()
                hasLoadCache = true
            }
            cacheMap[path]
        }
    }

    fun saveToLocalCache(path:String,timestamp:Long){
        if(!cacheFile.exists()){
            cacheFile.parentFile.mkdirs()
            cacheFile.createNewFile()
        }
        cacheFile.appendText("\n$path $timestamp")
        if(hasLoadCache){
            cacheMap[path] = timestamp
        }
    }


    private fun loadCache(){
        LogUtil.log("加载缓存: ${cacheFile.path}")
        cacheFile.readLines(Charset.defaultCharset()).forEach {
            val split = it.split(" ")
            if(split.size == 2){
                cacheMap[split[0]] = split[1].toLong()
            }
        }
    }

    fun release(){
        cacheMap.clear()
        hasLoadCache = false
    }
}

fun File.runWhenModified(block:File.()->Unit){
    val key = path
    val isModified = CacheManager.getFromCache(key) != lastModified()
    if(isModified){
        LogUtil.log("文件已经修改，需要重新处理:${absolutePath}，")
        block.invoke(this)
        CacheManager.saveToLocalCache(key,lastModified())
    }else{
        LogUtil.log("文件未修改，无需处理:$absolutePath")
    }
}