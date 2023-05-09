package com.syc.plugin.core

import groovy.util.Node
import groovy.util.NodeList
import groovy.util.XmlParser
import java.io.File
import java.lang.StringBuilder

class DimensFileIO{
    private lateinit var destFile:File
    private val content = StringBuilder()
    fun open(path: String) {
        destFile = createDimensFile(path)
        content.append(destFile.readText())
    }

    fun write(name:String, value: String) {
        val insetIndex = content.indexOf("</resources>")
            if (!content.contains("<dimen name=\"$name\">")) {
                content.insert(
                    insetIndex,
                    "\n <dimen name=\"$name\">$value</dimen> \n"
                )
            }

    }

    fun close() {
        destFile.writeText(content.toString())
    }

    fun clear(path: String){
        File(path).takeIf { it.exists() }?.delete()
        createDimensFile(path)
    }

    private fun createDimensFile(path: String):File {
        val file = File(path)
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
            file.writeText(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<resources>\n" +
                        "</resources>"
            )
        }
        return file
    }

    companion object{

        fun readDimens(path: String):MutableList<Dimen>{
            val list = mutableListOf<Dimen>()
            val dimensFile = File(path)
            if(dimensFile.exists()){
                val parser = XmlParser()
                val rootNode = parser.parse(dimensFile)
                rootNode.children().forEach {
                    val node = it as Node
                    val name = node.attribute("name") as String
                    val value = (node.value() as NodeList)[0] as String
                    list.add(Dimen(name,value))
                }
            }
            return list
        }
    }
}