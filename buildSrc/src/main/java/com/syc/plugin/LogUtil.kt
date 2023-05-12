package com.syc.plugin

object LogUtil {
    var logEnabled = true
    fun log(text:String){
        if(logEnabled){
            println(text)
        }
    }
}