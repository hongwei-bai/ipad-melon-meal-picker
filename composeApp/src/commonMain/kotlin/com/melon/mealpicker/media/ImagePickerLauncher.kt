package com.melon.mealpicker.media

import androidx.compose.runtime.Composable

enum class ImageSourceType {
    CAMERA,
    GALLERY
}

interface ImagePickerLauncher {
    fun launch(source: ImageSourceType)
}

@Composable
expect fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher
