package com.example.privasee.ui.controlAccess

import android.app.Activity
import android.app.ActivityManager
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.example.privasee.databinding.FragmentControlAccessBinding
import com.example.privasee.ui.monitor.AppAccessService
import com.example.privasee.ui.monitor.MyForegroundServices
import com.example.privasee.utils.CheckPermissionUtils
import kotlinx.android.synthetic.main.fragment_control_access.*
import java.util.*


class ControlAccessFragment : Fragment() {

    private var _binding: FragmentControlAccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        devicePolicyManager = requireActivity().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        activityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        compName = ComponentName(requireContext(), MyAdmin::class.java)


        _binding = FragmentControlAccessBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       // editTime.inputType = InputType.TYPE_CLASS_NUMBER

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        /*if(sp.contains("theTime") != null){
            var t = sp.getLong("theTime", 0).toInt()
            editTime.setText("$t")
        }else{
            editTime.setText("0")
        }*/

        setTimer.setOnClickListener {
            val active = devicePolicyManager!!.isAdminActive(compName!!)

            if (active) {
                //devicePolicyManager!!.lockNow()
                var timerString = timeButton.getText().toString()

                val units = timerString.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray() //will break the string up into an array

                val hour = units[0].toInt() //first element

                val minutes = units[1].toInt() //second element

                val duration = 60 * hour + minutes //add up our values

                var timerInt = duration.toLong()

                val editor = sp.edit()
                editor.apply(){
                    putLong("theTime", timerInt)
                }.apply()

                var t = sp.getLong("theTime", 0)
                Toast.makeText(
                    requireContext(),
                    "Timer has been set to $t minute(s)",
                    Toast.LENGTH_SHORT
                ).show()

                editor.apply(){
                    putBoolean("isLockerActive", true)
                }.apply()

                timeSet.setText("Timer has been set to $t minute(s)")

                enableFaceLock.setVisibility(View.GONE)

            } else {
                timeSet.setText("You need to enable Admin Permission first")

                Toast.makeText(
                    requireContext(),
                    "You need to enable the Admin Device Features",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cancelTimer.setOnClickListener {
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sp.edit()

            if ((sp.getBoolean("isLockerActive", false))) {

                val editor = sp.edit()
                editor.apply(){
                    putLong("theTime", 0)
                }.apply()

                Toast.makeText(
                    requireContext(),
                    "Timer has been stopped",
                    Toast.LENGTH_SHORT
                ).show()

                editor.apply(){
                    putBoolean("isLockerActive", false)
                }.apply()

                timeSet.setText("Timer is not set")

                enableFaceLock.setVisibility(View.VISIBLE)

            } else {
                Toast.makeText(
                    requireContext(),
                    "Timer is not active",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        enableFaceLock.setOnClickListener {
            val editor = sp.edit()

            editor.apply(){
                putBoolean("isLockerActive", false)
            }.apply()

            editor.apply(){
                putBoolean("isFaceLockActive", true)
            }.apply()

            faceLockStatus.setText("Face Lock is Active")

            setTimer.setVisibility(View.GONE)
            enableFaceLock.setVisibility(View.GONE)
        }

        disableFaceLock.setOnClickListener {
            val editor = sp.edit()

            editor.apply(){
                putBoolean("isFaceLockActive", false)
            }.apply()

            setTimer.setVisibility(View.VISIBLE)
            enableFaceLock.setVisibility(View.VISIBLE)
        }

        givePermission.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Additional text explaining why we need this permission"
            )
            startActivityForResult(intent, RESULT_ENABLE)
        }

        disablePermission.setOnClickListener{
            devicePolicyManager!!.removeActiveAdmin(compName!!)
            disablePermission.setVisibility(View.GONE)
            givePermission.setVisibility(View.VISIBLE)
        }

        timeButton.setOnClickListener {
            popTimePicker()
        }

        accessibility.setOnClickListener {
            CheckPermissionUtils.checkAccessibilityPermission(requireContext())
        }


    }

    fun popTimePicker() {
        // var timeButton: Button? = null
        var hour = 0
        var minute = 0

        val onTimeSetListener =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                timeButton!!.text =
                    kotlin.String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            }

        // int style = AlertDialog.THEME_HOLO_DARK;
        val timePickerDialog =
            TimePickerDialog(requireContext(),  /*style,*/onTimeSetListener, hour, minute, true)
        timePickerDialog.setTitle("Select Time")

        timePickerDialog.show()
    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Update GUI
            updateGUI(intent)
        }
    }

     override fun onPause() {
        super.onPause()
         LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        Log.i("OnPause", "Unregistered broadcast receiver")
    }

     override fun onStop() {
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)

        } catch (e: Exception) {
            // Receiver was probably already
        }
        super.onStop()
    }

    private fun updateGUI(intent: Intent) {
        if (intent.extras != null) {
            val millisUntilFinished = intent.getLongExtra("countdown", 30000)
            remainingTime.setText(java.lang.Long.toString(millisUntilFinished / 1000))
            //remainingTime.setText("flsjflksdjf")
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var t = sp.getLong("theTime", 0)

        if((sp.getBoolean("isLockerActive", false))){
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter(
                MyForegroundServices.COUNTDOWN_BR))
            timeSet.setText("Timer has been set to $t minute(s)")
            enableFaceLock.setVisibility(View.GONE)
        }else{
            timeSet.setText("Timer is not set")

        }

        if((sp.getBoolean("isFaceLockActive", false))){
            setTimer.setVisibility(View.GONE)
            enableFaceLock.setVisibility(View.GONE)

            var counter = sp.getInt("flcounter", 0)

            faceLockStatus.setText("Face Lock is Active. Counter: $counter")
        }else{
            faceLockStatus.setText("Face Lock is not Active")
        }

      // requireActivity().registerReceiver(broadcastReceiver, IntentFilter())
        val isActive = devicePolicyManager!!.isAdminActive(compName!!)
        disablePermission.setVisibility(if (isActive) View.VISIBLE else View.GONE)
        givePermission.setVisibility(if (isActive) View.GONE else View.VISIBLE)


    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_ENABLE -> if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    requireContext(),
                    "You have enabled the Admin Device features",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Problem to enable the Admin Device features",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RESULT_ENABLE = 11
        var devicePolicyManager: DevicePolicyManager? = null
        private var activityManager: ActivityManager? = null
        private var compName: ComponentName? = null    }
}