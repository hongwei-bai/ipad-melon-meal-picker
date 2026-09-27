package com.melon.mealpicker.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher {
    return remember {
        DesktopImagePickerLauncher(onImagePicked)
    }
}

private class DesktopImagePickerLauncher(
    private val onImagePicked: (ByteArray) -> Unit
) : ImagePickerLauncher {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun launch(source: ImageSourceType) {
        scope.launch {
            val dialog = FileDialog(null as Frame?, "Choose Meal Photo", FileDialog.LOAD)
            dialog.setFilenameFilter { _, name ->
                val lower = name.lowercase()
                lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")
            }
            dialog.isVisible = true

            val directory = dialog.directory
            val file = dialog.file
            if (directory != null && file != null) {
                val selectedFile = File(directory, file)
                if (selectedFile.exists()) {
                    try {
                        val originalImage = ImageIO.read(selectedFile)
                        if (originalImage != null) {
                            val scaled = scaleDown(originalImage, 1200)
                            val baos = ByteArrayOutputStream()
                            ImageIO.write(scaled, "jpg", baos)
                            onImagePicked(baos.toByteArray())
                        } else {
                            onImagePicked(selectedFile.readBytes())
                        }
                    } catch (e: Exception) {
                        onImagePicked(selectedFile.readBytes())
                    }
                }
            }
        }
    }

    private fun scaleDown(img: BufferedImage, maxDimension: Int): BufferedImage {
        val width = img.width
        val height = img.height
        if (width <= maxDimension && height <= maxDimension) {
            return img
        }
        val ratio = minOf(maxDimension.toDouble() / width, maxDimension.toDouble() / height)
        val targetWidth = (width * ratio).toInt().coerceAtLeast(1)
        val targetHeight = (height * ratio).toInt().coerceAtLeast(1)

        val output = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val g2 = output.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2.drawImage(img, 0, 0, targetWidth, targetHeight, null)
        g2.dispose()
        return output
    }
}
