package com.example.privasee.ui.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import com.example.privasee.ui.monitor.gallery.SeeSnapshots
import kotlinx.android.synthetic.main.fragment_add_user.*
import kotlinx.android.synthetic.main.fragment_control_access.*
import kotlinx.android.synthetic.main.fragment_monitor.*
import kotlinx.android.synthetic.main.fragment_monitor.givePermission
import kotlinx.android.synthetic.main.fragment_monitor_start.*
import java.io.File


class MonitorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent) {
                    val result =
                        intent.getStringExtra(intent.toString())
                    seeResult.setText("$result")
                }
            }, IntentFilter()
        )

        return inflater.inflate(R.layout.fragment_monitor, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        fab_settings.setOnClickListener {
            findNavController().navigate(R.id.action_monitorFragment_to_settingsFragment)
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sp.edit()
            editor.apply(){
                putString("BACK", "MONITOR")
            }.apply()

        }

        settings()
        isMyServiceRunning()
        isCameraPermissionEnabled()
        buttonTaps()
    }

    private fun buttonTaps(){
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var fullPath = sp?.getString("workingDirectory", "")

        var trainingPath = File(fullPath + "/Face Recognition")
        val files = trainingPath.listFiles()

        if (!trainingPath.exists() || files.isEmpty()) {
            btnStart.setVisibility(View.GONE)
        }else{
            btnStart.setVisibility(View.VISIBLE)
        }

        btnStart.setOnClickListener {
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val snapshotTimer = sp?.getString("setSnapshotTimer", "").toString()

            if((sp.getBoolean("IS_CAMERA_PERMISSION_ENABLED", false))){
                val editor = sp.edit()
                editor.apply(){
                    putBoolean("IS_ACTIVITY_RUNNING", true)
                }.apply()

                isMyServiceRunning()

                requireActivity().startForegroundService(
                    Intent(context, MyForegroundServices::class.java)
                        .putExtra("snapshotTimer",snapshotTimer))
            }else{
                Toast.makeText(requireContext(), "You did not give Camera Permission", Toast.LENGTH_LONG).show()
            }

        }

        btnStop.setOnClickListener{
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sp.edit()
            editor.apply(){
                putBoolean("IS_ACTIVITY_RUNNING", false)
            }.apply()

            isMyServiceRunning()

            requireActivity().stopService(
                Intent(context, MyForegroundServices::class.java))

            editor.apply(){
                putBoolean("isLockerActive", false)
            }.apply()
        }

        givePermission.setOnClickListener {
            checkForPermissions(android.Manifest.permission.CAMERA, "Camera", Constants.REQUEST_CODE_PERMISSIONS)
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            if((sp.getBoolean("IS_CAMERA_PERMISSION_ENABLED", false))){
                givePermission.setText("Permission Granted")
            }
        }



        var snapshotPath = File(fullPath + "/Snapshots")

        if (!snapshotPath.exists()) {
            seeSnapshotsButton.setVisibility(View.GONE)
        }else{
            seeSnapshotsButton.setVisibility(View.VISIBLE)
        }

        seeSnapshotsButton.setOnClickListener {
            val intent = Intent(requireContext(), SeeSnapshots::class.java)
            startActivity(intent)
        }
    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int){ //if not granted, it asks for permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when {

                ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(requireContext(), "$name permission granted", Toast.LENGTH_SHORT).show()
                    val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sp.edit()
                    editor.apply(){
                        putBoolean("IS_CAMERA_PERMISSION_ENABLED", true)
                    }.apply()
                }

                shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode) //explains why permission is needed after they rejected it the first time

                else -> {
                    goToSettings()
                }
            }
        }
    }

    private fun showDialog (permission: String, name: String, requestCode: Int){
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setMessage("Permission to access your $name is required to use this app. If you deny this again, you will have to manually add permission via settings.")
            setTitle("Permission required")
            setPositiveButton("ok") { dialog, which ->
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        requireContext().startActivity(intent)
    }

    private fun isMyServiceRunning(){
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if((sp.getBoolean("IS_ACTIVITY_RUNNING", false))){
            viewServiceStatus.text = "Serivice is Running"
        }else{
            viewServiceStatus.text = "Service is Stopped"
        }
    }

    private fun isCameraPermissionEnabled(){
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if((sp.getBoolean("IS_CAMERA_PERMISSION_ENABLED", false))){
            seePermission.text = "Camera Permission is Enabled"
        }else{
            seePermission.text = "Camera Permission is Blocked"
        }
    }

    private fun settings() {

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var snapshotTimer = sp?.getString("setSnapshotTimer", "").toString()

        if(snapshotTimer.isEmpty()){
            val editor = sp.edit()
            editor.apply(){
                putString("setSnapshotTimer", "1")
            }.apply()
        }

        viewTimer.text = "Service will take snapshot every $snapshotTimer minute(s)"

    }


}