package com.undercut.cache.tools

import com.undercut.cache.type.enums.EnumType
import com.undercut.cache.type.structs.StructType
import java.io.File

fun main(args: Array<String>) {
    val enumsFile = File("./developer-info/enums.txt")
    val structsFile = File("./developer-info/structs.txt")

    enumsFile.bufferedWriter().use { writer ->
        for (i in 0..EnumType.getParser().getMaxId()) {
            try {
                val type = EnumType.get(i)
                writer.write("${type.id}:\n")
                writer.write("${type.values}\n")
                writer.write("\n")
            } catch (e: Exception) {

            }
        }
    }

    structsFile.bufferedWriter().use { writer ->
        for (i in 0..StructType.getParser().getMaxId()) {
            try {
                val type = StructType.get(i)
                writer.write("${type.id}:\n")
                writer.write("${type.params}\n")
                writer.write("\n")
            } catch (e: Exception) {

            }
        }
    }
}