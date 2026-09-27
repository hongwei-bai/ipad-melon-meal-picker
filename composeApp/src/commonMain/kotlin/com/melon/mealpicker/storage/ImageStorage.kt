package com.melon.mealpicker.storage

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

object ImageStorage {
    private val fileSystem = FileSystem.SYSTEM

    fun getMealsDirectoryPath(): String {
        val base = getAppStorageDirectory()
        val mealsDir = "$base/meals"
        val path = mealsDir.toPath()
        if (!fileSystem.exists(path)) {
            fileSystem.createDirectories(path)
        }
        return mealsDir
    }

    fun getAbsolutePath(relativePath: String): String {
        if (relativePath.startsWith("/") || relativePath.startsWith("file://")) {
            return relativePath
        }
        val base = getAppStorageDirectory()
        return "$base/$relativePath"
    }

    fun exists(relativePath: String): Boolean {
        if (relativePath.isBlank()) return false
        val absolutePath = getAbsolutePath(relativePath)
        val path = absolutePath.toPath()
        return try {
            fileSystem.exists(path)
        } catch (e: Exception) {
            false
        }
    }

    fun saveFile(relativePath: String, bytes: ByteArray) {
        val absolutePath = getAbsolutePath(relativePath)
        val path = absolutePath.toPath()
        path.parent?.let { parent ->
            if (!fileSystem.exists(parent)) {
                fileSystem.createDirectories(parent)
            }
        }
        fileSystem.write(path) {
            write(bytes)
        }
    }

    /**
     * Saves raw image bytes into Documents/meals/{id}.jpg using Okio.
     * Returns the sandboxed relative path "meals/{id}.jpg".
     */
    fun saveMealImage(id: String, imageBytes: ByteArray): String {
        getMealsDirectoryPath() // Ensure dir exists
        val relativePath = "meals/$id.jpg"
        saveFile(relativePath, imageBytes)
        return relativePath
    }

    /**
     * Unlinks/deletes the image file associated with a meal via Okio.
     */
    fun deleteMealImage(relativePath: String): Boolean {
        if (relativePath.isBlank()) return false
        val absolutePath = getAbsolutePath(relativePath)
        val path = absolutePath.toPath()
        return try {
            if (fileSystem.exists(path)) {
                fileSystem.delete(path)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Reads image bytes via Okio.
     */
    fun readMealImage(relativePath: String): ByteArray? {
        if (relativePath.isBlank()) return null
        val absolutePath = getAbsolutePath(relativePath)
        val path = absolutePath.toPath()
        return try {
            if (fileSystem.exists(path)) {
                fileSystem.read(path) {
                    readByteArray()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
