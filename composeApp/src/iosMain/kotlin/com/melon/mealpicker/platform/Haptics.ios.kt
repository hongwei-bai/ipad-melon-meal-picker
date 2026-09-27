package com.melon.mealpicker.platform

import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

actual fun playSuccessHaptic() {
    val generator = UINotificationFeedbackGenerator()
    generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
}
