package com.example.privasee.ui.monitor

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
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
import com.example.privasee.DbQueryIntentService
import com.example.privasee.R
import com.example.privasee.ui.controlAccess.ControlAccessFragment
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock.BlockScreen
import kotlinx.android.synthetic.main.fragment_monitor.*
import kotlinx.coroutines.Job
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MonitorService :  LifecycleService() {

    private  lateinit var timer: CountDownTimer
    private var imageCapture:ImageCapture?=null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    lateinit var job: Job
    lateinit var filelist: MutableList<Bitmap>
    var faceLockCounter = 0

    var isRunning: Boolean = true

    var isSnapshotDone = false

    var appname = ""


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        outputDirectory = getOutputDirectory()
        outputDirectory = File(".$outputDirectory")
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (intent != null) {
            appname = intent.getStringExtra("appName").toString()
        }


        startCamera()

        Thread {
             do {

                try {
                    Log.e("Service", "Service is running...")
                        takePhoto()

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                 Thread.sleep(1000)
             } while (!isSnapshotDone)
            }.start()

        return super.onStartCommand(intent, flags, startId)

    }

    private fun takePhoto(){

        val imageCapture = imageCapture?:return
        var fileName = SimpleDateFormat(Constants.FILE_NAME_FORMAT,
            Locale.getDefault())
            .format(System
                .currentTimeMillis()) + ".jpg"

        //var path = getOutputDirectory()
        var pathSnapshot = "$outputDirectory/Snapshots"
        var fullpath = File(pathSnapshot)

        val photoFile = File(
            "$fullpath",fileName)

        if (!fullpath.exists()) {
            fullpath.mkdirs()
        }

        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo Saved"

                /*    Toast.makeText(
                        this@MonitorService,
                        "$msg $savedUri",
                        Toast.LENGTH_LONG).
                    show()*/

                  faceDetection(photoFile.toString())

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG,
                        "onError: ${exception.message}",
                        exception )
                }

            }
        )

    }

    private fun faceDetection(string: String){

        //start python
        if (!Python.isStarted()) Python.start(AndroidPlatform(this))
        val py = Python.getInstance()

        //val imageFile = string
        val bitmap = BitmapFactory.decodeFile(string)
        val imageString = getStringImage(bitmap)

        val pyobj = py.getModule("face_detection") //give name of python file
        val obj = pyobj.callAttr("main", imageString) //call main method
        val str = obj.toString()

        if(str == "No face detected"){
           // val imageStringSplit = string.substring(string.lastIndexOf("/")+1); //split file path, take last(file)

           // Toast.makeText(this, "No face detected", Toast.LENGTH_LONG).show()

            val intent = Intent(this, DbQueryIntentService::class.java)
            intent.putExtra("image", string)
            intent.putExtra("query", "insertRecord")
            intent.putExtra("appName", appname)
          //  intent.putExtra("status", "No face Detected")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            ContextCompat.startForegroundService(this, intent)
            isSnapshotDone = true
            this.stopSelf()

        }else{
            //convert it to byte array
            val data = Base64.decode(str, Base64.DEFAULT)
            //now convert it to bitmap
            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)


           // createDirectoryAndSaveFile(bmp, string)
            faceRecognition(bmp, string)
        }

    }

    private fun faceRecognition(bitmap: Bitmap, stringImage: String){

        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        //start python
        if (!Python.isStarted()) Python.start(AndroidPlatform(this))
        val py = Python.getInstance()

        filelist = mutableListOf()

        var pathFd = "$outputDirectory/face recognition"
        var fullpath = File(pathFd)

        if(!fullpath.exists()){
            fullpath.mkdirs()
        }

        var list = imageReader(fullpath)
        val training = list.toTypedArray()
        val trainingString = arrayOfNulls<String>(training.size)


            for((x, i) in training.withIndex()){
                val imageFile = i.toString()
                val bitmap = BitmapFactory.decodeFile(imageFile)
                val imageString = getStringImage(bitmap)
                trainingString[x] = imageString
            }


        val pyobj = py.getModule("face_recognition") //give name of python file
        val obj = pyobj.callAttr("train", *trainingString ) //call main method

        val pyobj2 = py.getModule("get_pcca") //give name of python file
        val obj2 = pyobj2.callAttr("train", *trainingString) //call main method

        //val bitmapp = BitmapFactory.decodeFile(string)
        var imageStringg = getStringImage(bitmap)

        val threshold = sp.getInt("threshold", 8000)

        val objFinal = pyobj.callAttr(
            "project_testing",
            imageStringg,
            obj,
            obj2,
            threshold
        ) //call main method project_testing

      //  Toast.makeText(this, "$objFinal", Toast.LENGTH_LONG).show()

       if(!objFinal.toBoolean()){

           val intent = Intent(this, DbQueryIntentService::class.java)
           intent.putExtra("image", stringImage)
           intent.putExtra("query", "insertRecord")
           intent.putExtra("appName", appname)
          // intent.putExtra("status", objFinal.toString())
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
           ContextCompat.startForegroundService(this, intent)
           isSnapshotDone = true
           this.stopSelf()

           val editor = sp.edit()
           editor.apply(){
               putBoolean("result", objFinal.toBoolean())
           }.apply()
       }else{
           var file = File(stringImage)
           file.delete()

           isSnapshotDone = true
           this.stopSelf()
           val editor = sp.edit()
           editor.apply(){
               putBoolean("result", objFinal.toBoolean())
           }.apply()
       }

       // isSnapshotDone = true

        //sendBroadcastMessage(objFinal.toString())
    }

    private fun createDirectoryAndSaveFile(bitmap: Bitmap, string: String) {
       // var path = getOutputDirectory()
        var pathFd = "$outputDirectory/face detection"
        var fullpath = File(pathFd)

        val imageStringSplit = string.substring(string.lastIndexOf("/")+1); //split file path, take last(file)

        val file = File("$pathFd", imageStringSplit)

        if (!fullpath.exists()) {
            fullpath.mkdirs()
        }

        if (file.exists()) {
            file.delete()
        }

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
      //      Toast.makeText(this, "Successfuly Saved", Toast.LENGTH_SHORT).show()
            //faceRecognition(string)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
      //      Toast.makeText(this, "Fuck cant be Saved", Toast.LENGTH_SHORT).show()
        }

    }

    fun imageReader(root: File): ArrayList<File> {
        val a: ArrayList < File > = ArrayList()
        if (root.exists()) {
            val files = root.listFiles()
            if (files.isNotEmpty()) {
                for (i in 0..files.size - 1) {
                    if (files[i].name.endsWith(".jpg")) {
                        a.add(files[i])
                    }
                }
            }
        }
        return a!!
    }

    private fun getStringImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

        //store in byte array
        val imageBytes = baos.toByteArray()
        //finally encode to string
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun startCamera(){

        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            /* val preview = Preview.Builder()
                   .build()
                   .also {mPreview ->
                       mPreview.setSurfaceProvider(
                           binding.viewFinder.surfaceProvider
                       )

                   }*/

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try{

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, cameraSelector,imageCapture)

            }catch (e: java.lang.Exception){
                Log.d(Constants.TAG, "startCamera FAIL: ", e)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile->
            File(mFile,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
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