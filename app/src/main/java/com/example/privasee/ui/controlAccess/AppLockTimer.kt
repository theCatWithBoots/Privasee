package com.example.privasee.ui.controlAccess

import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.privasee.ui.monitor.AppAccessService
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock.BlockScreen
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock.BlockScreen2
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList



class AppLockTimer :  LifecycleService() {



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

       // val sp = PreferenceManager.getDefaultSharedPreferences(this)

      //  if(sp.getBoolean("isLockerActive", false))
        val timer = (intent?.getStringExtra("Timer"))?.toInt()
        val packageNames = (intent?.getStringArrayListExtra("controlledAppPackageNames"))

        startTimer(timer!!, packageNames)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(timer: Int, packageNames: java.util.ArrayList<String>?) {

         val startTimeInMillis = timer?.toLong() //sp.getLong("theTime", 0)
         var mCountDownTimer: CountDownTimer? = null
         var mTimerRunning = false
         var mTimeLeftInMillis : Long = TimeUnit.MINUTES.toMillis(startTimeInMillis!!)

        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
               //mTimeLeftInMillis = millisUntilFinished
               // updateCountDownText()

                    Log.i("TAG","Countdown seconds remaining:" + millisUntilFinished / 1000);

                    intent.putExtra("countdown",millisUntilFinished)
                    sendBroadcastMessage(intent)

            }

            override fun onFinish() {

                mTimerRunning = false

            /*    val intent = Intent(this@AppLockTimer, AppAccessService::class.java)
                intent.putExtra("action", "addLock")
                intent.putStringArrayListExtra("packageNames", ArrayList(packageNames))
                this@AppLockTimer.startService(intent)*/

                val intent = Intent(this@AppLockTimer, BlockScreen2::class.java)
                intent.putStringArrayListExtra("packageNames", ArrayList(packageNames))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }
        }.start()
        mTimerRunning = true
    }

    private fun sendBroadcastMessage(intent: Intent) {
        if (intent != null) {
            //val intent = Intent(string)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    override fun onDestroy() {
        this.stopSelf()
        super.onDestroy()
        Log.e("Service", "Service Stopped...")

    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    companion object {
        val COUNTDOWN_BR = "com.example.privasee.ui.monitor"
        var intent = Intent(COUNTDOWN_BR)
    }

}