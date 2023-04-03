package com.example.privasee

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.privasee.databinding.ActivityMainBinding
import com.example.privasee.ui.initialRun.SetupActivity
import com.example.privasee.utils.CheckPermissionUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var BottomNavController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        

//        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


       /* val sharedPreferences = getSharedPreferences("isFirstTimeOpen", Context.MODE_PRIVATE)
        val isFirstTimeOpen = sharedPreferences.getBoolean("isFirstTimeOpen", true)

        if (isFirstTimeOpen) {
            // Start initial run
            Intent(this, SetupActivity::class.java).also {
                startActivity(it)
            }
            sharedPreferences.edit().putBoolean("isFirstTimeOpen", false).apply()
        } else {
            CheckPermissionUtils.checkAccessibilityPermission(this)
        }

*/
        val bottomNavigationView = binding.bottomNavMenu
        BottomNavController = findNavController(R.id.bottom_nav_menu_fcv)
        bottomNavigationView.setupWithNavController(BottomNavController)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.apply(){
            putString("BACK", "")
        }.apply()

        editor.apply(){
            putString("workingDirectory", getOutputDirectory().toString())
        }.apply()

        autoGivePermission()

    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile->
            File(mFile,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onSupportNavigateUp(): Boolean { // make the back button in AddFragment functional
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val backButtonString = sp?.getString("BACK", "").toString()

        if(backButtonString == "MONITOR"){
            val navController = findNavController(R.id.monitor_start)
            return navController.navigateUp() || super.onSupportNavigateUp()
        }else{
           val  navController = findNavController(R.id.list_user_fcv)
            return navController.navigateUp() || super.onSupportNavigateUp()
        }

    }

    private fun autoGivePermission(){
        if(allPermissionGranted()){

            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sp.edit()
            editor.apply(){
                putBoolean("IS_CAMERA_PERMISSION_ENABLED", true)
            }.apply()

            Toast.makeText(this,
                "Camera permission granted",
                Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        fun innerCheck (name: String){
            if(requestCode == Constants.REQUEST_CODE_PERMISSIONS){

                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT).show()
                    val sp = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor = sp.edit()
                    editor.apply(){
                        putBoolean("IS_CAMERA_PERMISSION_ENABLED", false)
                    }.apply()


                }else{
                    Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
                }
            }
        }

        when (requestCode){
            Constants.REQUEST_CODE_PERMISSIONS -> innerCheck("Camera")
        }

        /* if(requestCode == Constants.REQUEST_CODE_PERMISSIONS){

             if(allPermissionGranted()){
                 startCamera()
             }else{
                 Toast.makeText(this,
                     "Permission not Granted",
                     Toast.LENGTH_SHORT).show()

                // finish()
             }

         }*/
    }

    private fun allPermissionGranted()=
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }



}
