package com.example.privasee.ui.controlAccess

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.example.privasee.databinding.FragmentControlAccessBinding
import com.example.privasee.ui.monitor.MyForegroundServices
import kotlinx.android.synthetic.main.fragment_control_access.*
import kotlinx.android.synthetic.main.fragment_control_access.givePermission
import kotlinx.android.synthetic.main.fragment_monitor.*


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
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if(sp.contains("theTime") != null){
            var t = sp.getLong("theTime", 0).toInt()
            editTime.setText("$t")
        }else{
            editTime.setText("0")
        }

        setTimer.setOnClickListener {
            val active = devicePolicyManager!!.isAdminActive(compName!!)

            if (active) {
                //devicePolicyManager!!.lockNow()
                var timerString = editTime.getText().toString()
                var timerInt = timerString.toLong()

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

            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to enable the Admin Device Features",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())


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
      // requireActivity().registerReceiver(broadcastReceiver, IntentFilter())
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter(
            MyForegroundServices.COUNTDOWN_BR))
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
        private var devicePolicyManager: DevicePolicyManager? = null
        private var activityManager: ActivityManager? = null
        private var compName: ComponentName? = null    }
}