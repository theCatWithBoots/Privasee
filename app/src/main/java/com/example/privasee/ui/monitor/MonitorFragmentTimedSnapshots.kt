package com.example.privasee.ui.monitor

import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import com.example.privasee.databinding.FragmentMonitorTimedSnapshotsBinding
import com.example.privasee.ui.controlAccess.AppLockTimer
import kotlinx.android.synthetic.main.fragment_control_access_applock.*
import kotlinx.android.synthetic.main.fragment_control_access_screentimelimit.*
import kotlinx.android.synthetic.main.fragment_control_access_screentimelimit_run.btnStart
import kotlinx.android.synthetic.main.fragment_control_access_screentimelimit_run.btnStop
import kotlinx.android.synthetic.main.fragment_control_access_screentimelimit_run.viewServiceStatus
import kotlinx.android.synthetic.main.fragment_control_access_screentimelimit_run.viewTimer
import kotlinx.android.synthetic.main.fragment_monitor_timed_snapshots.*
import java.util.*
import kotlin.collections.ArrayList

class MonitorFragmentTimedSnapshots: Fragment() {



    private var _binding: FragmentMonitorTimedSnapshotsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonitorTimedSnapshotsBinding.inflate(inflater, container, false)



        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_monitorFragmenTimedSnapshots_to_monitorFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback (callback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timeButton3.setOnClickListener {
            popTimePicker()
        }

        btnStart.setOnClickListener {
            if(timeButton3.getText().toString() == "Select Time"){
                Toast.makeText(
                    requireContext(),
                    "Please set snapshot time",
                    Toast.LENGTH_LONG).
                show()
            }else{
                val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val editor = sp.edit()

                editor.apply(){
                    putBoolean("IS_ACTIVITY_RUNNING", true)
                }.apply()

              //  if((sp.getString("snapshotTimerRecord", "00:01") != "00:01")){
               //     var timerString = (sp.getString("snapshotTimerRecord", "00:01"))
               // }

                var timerString = timeButton3.getText().toString()

            //    editor.apply(){
             //       putString("snapshotTimerRecord", timerString)
             //   }.apply()

                val units = timerString.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray() //will break the string up into an array

                val hour = units[0].toInt() //first element

                val minutes = units[1].toInt() //second element

                val duration = 60 * hour + minutes //add up our values

                var timerInt = duration.toLong()


                //seeTimer.text = "Service will take snapshot every $timerInt minute(s)"
                // seeTimer.text = "putang ina"

                isMyServiceRunning()

                requireActivity().startForegroundService(
                    Intent(context, MyForegroundServices::class.java)
                        .putExtra("snapshotTimer",timerInt.toString()))
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

    }

    private fun isMyServiceRunning(){
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if((sp.getBoolean("IS_ACTIVITY_RUNNING", false))){
            seeServiceStatus.text = "Serivice is Running"
        }else{
            seeServiceStatus.text = "Service is Stopped"
        }
    }

    fun popTimePicker() {
        // var timeButton: Button? = null
        var hour = 0
        var minute = 0

        val onTimeSetListener =
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                timeButton3!!.text =
                    String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            }

        // int style = AlertDialog.THEME_HOLO_DARK;
        val timePickerDialog =
            TimePickerDialog(requireContext(),  /*style,*/onTimeSetListener, hour, minute, true)
        timePickerDialog.setTitle("Select Time")

        timePickerDialog.show()
    }


    fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }

}