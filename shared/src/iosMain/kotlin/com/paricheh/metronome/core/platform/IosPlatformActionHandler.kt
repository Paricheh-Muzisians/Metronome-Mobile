package com.paricheh.metronome.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class IosPlatformActionHandler : PlatformActionHandler {
    override fun openRatingPage() {
        // TODO: Implement iOS specific rating action if needed
    }

    override fun showToast(text: String) {
        // TODO: Implement iOS specific rating action if needed
    }

    override fun openAppSettings() {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
