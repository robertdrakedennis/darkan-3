package com.undercut.cache.tools

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.type.sprites.Sprite
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import javax.imageio.ImageIO

fun main() {
    val baseDir = "./dumps/sprites"
    val baseDirectory = File(baseDir)
    val updatesDir = "./dumps/sprite-updates-${LocalDate.now()}"
    val updatesDirectory = File(updatesDir)

    if (!baseDirectory.exists()) {
        baseDirectory.mkdirs()
        println("Created base directory: $baseDir")
    }

    if (!updatesDirectory.exists()) {
        updatesDirectory.mkdirs()
        println("Created updates directory: $updatesDir")
    }

    var successCount = 0
    var newCount = 0
    var updatedCount = 0
    var unchangedCount = 0
    var errorCount = 0

    for (spriteId in 0..Cache.get().getReferenceTable(Index.SPRITES).highestEntry()) {
        try {
            val sprite = Sprite.get(spriteId)

            if (sprite.subImages.isNotEmpty()) {
                val hasMultiple = sprite.subImages.size > 1

                sprite.subImages.forEachIndexed { index, subImage ->
                    val filename = if (hasMultiple) "sprite_${spriteId}_sub_$index.png" else "sprite_$spriteId.png"
                    val relativeDir = if (hasMultiple) "sprite_$spriteId" else ""

                    val existingFile = if (relativeDir.isNotEmpty()) File(File(baseDirectory, relativeDir), filename) else File(baseDirectory, filename)

                    val newBytes = ByteArrayOutputStream().use { baos ->
                        ImageIO.write(subImage.img, "PNG", baos)
                        baos.toByteArray()
                    }

                    val status = compareAndWrite(existingFile, newBytes, updatesDirectory, relativeDir, filename)
                    when (status) {
                        WriteStatus.NEW -> newCount++
                        WriteStatus.UPDATED -> updatedCount++
                        WriteStatus.UNCHANGED -> unchangedCount++
                    }

                    // Also write back to the base directory so it stays current
                    val baseTargetDir = if (relativeDir.isNotEmpty()) File(baseDirectory, relativeDir).apply { mkdirs() } else baseDirectory
                    File(baseTargetDir, filename).writeBytes(newBytes)

                    // Handle expanded sprites
                    if (subImage.x != 0 || subImage.y != 0 ||
                        subImage.fullWidth != subImage.img.width ||
                        subImage.fullHeight != subImage.img.height) {

                        val expandedFilename = if (hasMultiple) "sprite_${spriteId}_sub_${index}_expanded.png" else "sprite_${spriteId}_expanded.png"
                        val existingExpandedFile = if (relativeDir.isNotEmpty()) File(File(baseDirectory, relativeDir), expandedFilename) else File(baseDirectory, expandedFilename)

                        val expandedImage = sprite.expandSprite(subImage)
                        val expandedBytes = ByteArrayOutputStream().use { baos ->
                            ImageIO.write(expandedImage, "PNG", baos)
                            baos.toByteArray()
                        }

                        val expandedStatus = compareAndWrite(existingExpandedFile, expandedBytes, updatesDirectory, relativeDir, expandedFilename)
                        when (expandedStatus) {
                            WriteStatus.NEW -> newCount++
                            WriteStatus.UPDATED -> updatedCount++
                            WriteStatus.UNCHANGED -> unchangedCount++
                        }

                        File(baseTargetDir, expandedFilename).writeBytes(expandedBytes)
                    }
                }

                successCount++
                if (successCount % 100 == 0)
                    println("Processed $successCount sprites...")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorCount++
            if (errorCount < 10)
                println("Error processing sprite $spriteId: ${e.message}")
        }
    }

    println("\nSprite dump completed!")
    println("Successfully processed: $successCount sprites")
    println("New sprites: $newCount")
    println("Updated sprites: $updatedCount")
    println("Unchanged sprites: $unchangedCount")
    println("Errors encountered: $errorCount")
    println("Base directory: ${baseDirectory.absolutePath}")
    println("Updates directory: ${updatesDirectory.absolutePath}")

    // Clean up empty updates directory if nothing changed
    if (newCount == 0 && updatedCount == 0) {
        updatesDirectory.deleteRecursively()
        println("No changes detected — removed empty updates directory.")
    }
}

private enum class WriteStatus { NEW, UPDATED, UNCHANGED }

private fun compareAndWrite(
    existingFile: File,
    newBytes: ByteArray,
    updatesDirectory: File,
    relativeDir: String,
    filename: String
): WriteStatus {
    if (!existingFile.exists()) {
        val targetDir = if (relativeDir.isNotEmpty()) File(updatesDirectory, relativeDir).apply { mkdirs() } else updatesDirectory
        File(targetDir, filename).writeBytes(newBytes)
        return WriteStatus.NEW
    }

    val existingBytes = existingFile.readBytes()
    if (existingBytes.contentEquals(newBytes)) {
        return WriteStatus.UNCHANGED
    }

    val targetDir = if (relativeDir.isNotEmpty()) File(updatesDirectory, relativeDir).apply { mkdirs() } else updatesDirectory
    File(targetDir, filename).writeBytes(newBytes)
    return WriteStatus.UPDATED
}
