package com.example.privasee.ui.users.addUser

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.privasee.R
import com.example.privasee.databinding.ActivityAddUserCapturePhotoBinding
import com.example.privasee.ui.monitor.Constants
import kotlinx.android.synthetic.main.activity_add_user_capture_photo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Handler


class AddUserCapturePhoto: AppCompatActivity() {

    private lateinit var binding: ActivityAddUserCapturePhotoBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var loadingDialog : LoadingDialog
    var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddUserCapturePhotoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.addUserCaptureButton.isEnabled = false
        if (allPermissionGranted()) {
            startCamera()
            binding.addUserCaptureButton.isEnabled = true
        } else
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)

        binding.addUserCaptureButton.setOnClickListener {

            loadingDialog = LoadingDialog(this)
            loadingDialog.startLoadingDialog()

            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sp.edit()
            editor.apply(){
                putBoolean("isThereAFace", false)
            }.apply()

            editor.apply(){
                putInt("loadingStopCounter", counter)
            }.apply()

            takePhoto()

        }
    }


    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        var pathSnapshot = "$outputDirectory/raw photo"
        var fullpath = File(pathSnapshot)

        if (!fullpath.exists()) {
            fullpath.mkdirs()
        }

        var fileName = SimpleDateFormat(
            Constants.FILE_NAME_FORMAT,
            Locale.getDefault())
            .format(System
                .currentTimeMillis()) + ".jpg"

        val photoFile = File(
            "$fullpath",fileName)

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object :ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo Saved"

                    //Toast.makeText( baseContext, "$msg $savedUri", Toast.LENGTH_LONG).show()

                    val sp = PreferenceManager.getDefaultSharedPreferences(this@AddUserCapturePhoto)

                    if((sp.getBoolean("isThereAFace", false))){//if face is detected
                        faceDetection(photoFile.toString())
                    } else{
                        ifFaceExist(photoFile.toString())
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(
                        TAG,
                        "onError: ${exception.message}",
                        exception)
                }
            }
        )
    }

    private fun ifFaceExist(string: String){

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
            noFaceDetectedDialog()
        }else{
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sp.edit()
            editor.apply(){
                putBoolean("isThereAFace", true)
            }.apply()

            for(i in 1..20){
                takePhoto()
            }


        }
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

            Toast.makeText(this, "No face detected", Toast.LENGTH_LONG).show()
        }else{
            //convert it to byte array
            val data = Base64.decode(str, Base64.DEFAULT)
            //now convert it to bitmap
            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)

            createDirectoryAndSaveFile(bmp, string)
        }

    }

    private fun noFaceDetectedDialog (){
        loadingDialog.dismissDialog()

        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Please center your face inside the box.")
            setTitle("No face detected")
            setPositiveButton("ok") { dialog, which ->
               /* val intent = intent
                finish()
                startActivity(intent)*/
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun createDirectoryAndSaveFile(bitmap: Bitmap, string: String) {
        // var path = getOutputDirectory()
        var pathFd = "$outputDirectory/face recognition"
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

            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sp.edit()

            editor.apply(){
                putInt("loadingStopCounter", ++counter)
            }.apply()

            if((sp.getInt("loadingStopCounter", 0)) == 20){
                loadingDialog.dismissDialog()
            }

            var v = sp.getInt("loadingStopCounter", 0).toString()
            imageNumber.setText("$v")
          //  Toast.makeText(this, "$v", Toast.LENGTH_SHORT).show()
            //faceRecognition(string)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //Toast.makeText(this, "Can't be Saved", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getStringImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

        //store in byte array
        val imageBytes = baos.toByteArray()
        //finally encode to string
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    // Start Front Camera Preview
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                .also { mPreview ->
                    mPreview.setSurfaceProvider(
                        binding.captureView.surfaceProvider
                    )
                }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e:Exception) {
                Log.d(TAG, "startCamera Fail:", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((REQUEST_CODE_PERMISSION == 111) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            startCamera()
            binding.addUserCaptureButton.isEnabled = true
        }
    }


    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    companion object {
        const val TAG = "cameraX"
        //const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSION = 111
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}
