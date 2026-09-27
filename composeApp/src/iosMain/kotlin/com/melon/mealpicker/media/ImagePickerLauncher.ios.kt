package com.melon.mealpicker.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher {
    return remember {
        IosImagePickerLauncher(onImagePicked)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class IosImagePickerLauncher(
    private val onImagePicked: (ByteArray) -> Unit
) : ImagePickerLauncher {

    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            if (image != null) {
                val scaled = scaleDownImage(image, 1200.0)
                val jpegData: NSData? = UIImageJPEGRepresentation(scaled, 0.85)
                if (jpegData != null) {
                    val bytes = ByteArray(jpegData.length.toInt())
                    if (bytes.isNotEmpty()) {
                        memcpy(bytes.refTo(0), jpegData.bytes, jpegData.length)
                        onImagePicked(bytes)
                    }
                }
            }
            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
        }
    }

    override fun launch(source: ImageSourceType) {
        val rootVc = getRootViewController() ?: return
        val picker = UIImagePickerController()
        picker.delegate = delegate

        val sourceType = when (source) {
            ImageSourceType.CAMERA -> {
                if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                } else {
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                }
            }
            ImageSourceType.GALLERY -> UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        }

        picker.sourceType = sourceType
        rootVc.presentViewController(picker, true, null)
    }

    private fun getRootViewController(): UIViewController? {
        val keyWindow = UIApplication.sharedApplication.windows.firstOrNull { 
            (it as? UIWindow)?.isKeyWindow() == true 
        } as? UIWindow ?: UIApplication.sharedApplication.keyWindow
        var top = keyWindow?.rootViewController
        while (top?.presentedViewController != null) {
            top = top.presentedViewController
        }
        return top
    }

    private fun scaleDownImage(image: UIImage, maxDimension: Double): UIImage {
        val originalWidth = image.size.useContents { width }
        val originalHeight = image.size.useContents { height }

        if (originalWidth <= maxDimension && originalHeight <= maxDimension) {
            return image
        }

        val ratio = minOf(maxDimension / originalWidth, maxDimension / originalHeight)
        val newWidth = originalWidth * ratio
        val newHeight = originalHeight * ratio
        val newSize = CGSizeMake(newWidth, newHeight)

        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
        val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return resizedImage ?: image
    }
}
