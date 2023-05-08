package com.syc.plugin.core

import java.io.File

fun createDimensFile(path: String) {
    val file = File(path)
    if (!file.exists()) {
        file.createNewFile()
        file.writeText(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n" +
                    "</resources>"
        )
    }
}