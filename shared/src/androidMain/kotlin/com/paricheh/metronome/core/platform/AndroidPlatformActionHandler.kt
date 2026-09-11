package com.paricheh.metronome.core.platform

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.paricheh.metronome.core.Store

class AndroidPlatformActionHandler(private val context: Context) : PlatformActionHandler {
    override fun openRatingPage() {
        if (Store.getCurrentStore() == Store.Myket) {
            openMyketRating()
        } else {
            openCafeBazaarRating()
        }
    }

    override fun showToast(text: String) {
        Toast(context).apply {
            setText(text)
            this.show()
        }
    }

    private fun openMyketRating() {
        try {
            val packageName = context.packageName
            val url = "myket://comment?id=$packageName"
            val intent = Intent().apply {
                setAction(Intent.ACTION_VIEW)
                setData(url.toUri())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {

        }
    }

    private fun openCafeBazaarRating() {
        try {
            val packageName = context.packageName
            val intent = Intent(Intent.ACTION_EDIT).apply {
                data = "bazaar://details?id=$packageName".toUri()
                setPackage("com.farsitel.bazaar")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to browser or other store if bazaar is not installed
            try {
                val packageName = context.packageName
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://cafebazaar.ir/app/$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (innerException: Exception) {
                // Ignore if everything fails
            }
        }
    }
}
