package com.melon.mealpicker.storage

import java.io.File

actual fun getAppStorageDirectory(): String {
    val dir = File(System.getProperty("user.home"), ".mealquest")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir.absolutePath
}
