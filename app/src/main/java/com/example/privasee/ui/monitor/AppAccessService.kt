package com.example.privasee.ui.monitor

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock.BlockScreen

class AppAccessService : AccessibilityService() {

    private var packageNames: MutableList<String> = mutableListOf()
    private var previousPackageName = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            val packageName = event.packageName.toString()

            // Triggering only once, for whitelisted apps
            if (previousPackageName == packageName) {
                previousPackageName = packageName
                val pm = applicationContext.packageManager
                val appInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                val appName = appInfo.applicationInfo.loadLabel(pm).toString()
                Log.d("testing", "$appName to foreground")

                val intent = Intent(this, BlockScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            // For blocklisted apps test
            previousPackageName = packageName
            val pm = applicationContext.packageManager
            val appInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            val appName = appInfo.applicationInfo.loadLabel(pm).toString()
            Log.d("testing", "$appName to foreground")

            val intent = Intent(this, BlockScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Load package names dynamically here and update packageNames list
        val metadata = AccessibilityServiceInfo()
        metadata.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        // Test on 2 apps, youtube and photos
        packageNames.add("com.google.android.youtube")
        packageNames.add("com.google.android.apps.photos")
        metadata.packageNames = packageNames.toTypedArray()
        serviceInfo = metadata
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}