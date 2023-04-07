package com.example.privasee.ui.controlAccess

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.privasee.R
import com.example.privasee.ui.controlAccess.AppLockTimer.Companion.intent
import com.example.privasee.ui.monitor.Constants
import kotlinx.coroutines.Job
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MyForegroundServices :  LifecycleService() {

    private var screenTimer: Long? = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        screenTimer = intent?.getLongExtra("screenTimer", 1000)

        startTimer()

         /*   Thread {
                while (true) {

                    try {
                        if (snapshotTimerLong != null) {

                            Thread.sleep(snapshotTimerLong)
                            Log.e("Service", "Service is running...")

                            if(isRunning){
                                takePhoto()
                            }

                        }

                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    //counter--
                }
            }.start()*/


        val channelId = "Foreground Service ID"
        val channelName = "Privasee is Running"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.apply{
            lightColor = Color.BLUE
            importance = NotificationManager.IMPORTANCE_HIGH
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        /*val service = ContextCompat.getSystemService(this, MyForegroundService::class.java)
            ?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager*/

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification = Notification.Builder(this, channelId)
            .setContentText("Service is Running....")
            .setContentTitle("Privasee")
            .setSmallIcon(R.drawable.icon)
        startForeground(1001, notification.build())
        return super.onStartCommand(intent, flags, startId)

        //return START_NOT_STICKY

    }

    private fun startTimer() {

         var mCountDownTimer: CountDownTimer? = null
         var mTimerRunning = false
         var mTimeLeftInMillis : Long = TimeUnit.MINUTES.toMillis(screenTimer!!)

        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
               //mTimeLeftInMillis = millisUntilFinished
               // updateCountDownText()


                val sp = PreferenceManager.getDefaultSharedPreferences(this@MyForegroundServices)

                if((sp.getBoolean("IS_ACTIVITY_RUNNING", false))){ //continue broadcast
                    Log.i("TAG","Countdown seconds remaining:" + millisUntilFinished / 1000);

                    intent.putExtra("countdown",millisUntilFinished)
                    sendBroadcastMessage(intent)

                }else{
                    mCountDownTimer?.cancel() //stop timer
                }
                //sendBroadcast(intent)

            }

            override fun onFinish() {
                mTimerRunning = false
                    ControlAccessFragmentScreenTimeLimit.devicePolicyManager!!.lockNow()
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