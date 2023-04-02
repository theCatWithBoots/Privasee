package com.example.privasee.ui.monitor

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.ui.users.addUser.AddUserCapturePhoto
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock.BlockScreen
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.controlled.UserAppControlledAdapter
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.controlled.UserAppControlledFragmentArgs
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.controlled.UserAppControlledFragmentDirections
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.uncontrolled.UserAppUncontrolledFragmentArgs
import com.example.privasee.ui.users.userInfoUpdate.userAppMonitoring.monitored.UserAppMonitoredFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


class AppAccessService : AccessibilityService() {

    private var packageNames: MutableList<String> = mutableListOf()
    private var controlledApps: MutableList<String> = mutableListOf()
    private var previousPackageName = "initial"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if(packageNames.size > 0) {

            if(event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                val currentlyOpenedApp = event.packageName.toString()


                val pm = applicationContext.packageManager
                val appInfo = pm.getPackageInfo(currentlyOpenedApp, PackageManager.GET_META_DATA)
                val appName = appInfo.applicationInfo.loadLabel(pm).toString()

                // Triggering only once, for repeated opens
                if (previousPackageName == currentlyOpenedApp) {
                    previousPackageName = currentlyOpenedApp

                } else {
                    previousPackageName = currentlyOpenedApp
                    // start intent service, start verifying etc
                    Log.d("tagimandos", "monitoring $appName")

                    //take hidden snapshots
                    val intent = Intent(this, MonitorService::class.java)
                    startService(intent)

                }

                if(controlledApps.size > 0) {
                    for(packageName in controlledApps) {
                        if(packageName == currentlyOpenedApp) {
                            Log.d("tagimandos", "lock screen on $appName")
                            val intent = Intent(this, BlockScreen::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val metadata = AccessibilityServiceInfo()
        metadata.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED

        val action = intent?.getStringExtra("action")
        val packageNames = intent?.getStringArrayListExtra("packageNames")

        if (packageNames != null) {

            if(action == "addMonitor") {
                Log.d("tagimandos", "add monitor $packageNames")
                for(packageName in packageNames)
                    this.packageNames.add(packageName)
            }

            if (action == "removeMonitor") {
                Log.d("tagimandos", "remove monitor $packageNames")
                for(packageName in packageNames)
                    this.packageNames.remove(packageName)
            }

            if (action == "addLock") {
                Log.d("tagimandos", "add lock$packageNames")
                for(packageName in packageNames) {
                    this.packageNames.add(packageName)
                    this.controlledApps.add(packageName)
                }
            }

            if (action == "removeLock") {
                Log.d("tagimandos", "remove lock $packageNames")
                for(packageName in packageNames) {
                    this.packageNames.remove(packageName)
                    this.controlledApps.remove(packageName)
                }
            }

            metadata.packageNames = packageNames.toTypedArray()
            serviceInfo = metadata
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("tagimandos", "On service connect")
    }

}