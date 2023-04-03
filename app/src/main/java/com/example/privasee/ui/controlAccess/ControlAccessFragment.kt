package com.example.privasee.ui.controlAccess

import android.app.Activity
import android.app.ActivityManager
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import androidx.navigation.fragment.findNavController
import com.example.privasee.database.model.App
import com.example.privasee.database.model.Restriction
import com.example.privasee.database.model.User
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentControlAccessBinding
import com.example.privasee.ui.monitor.AppAccessService
import com.example.privasee.ui.monitor.MyForegroundServices
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.UserAppControllingActivity
import com.example.privasee.ui.users.userInfoUpdate.userAppMonitoring.UserAppMonitoringActivity
import com.example.privasee.utils.CheckPermissionUtils
import kotlinx.android.synthetic.main.fragment_control_access.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ControlAccessFragment : Fragment() {

    private var _binding: FragmentControlAccessBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


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

        applock.setOnClickListener {
            findNavController().navigate(R.id.action_controlAccessFragment_to_appLock)
        }

        scrnTimeLimit.setOnClickListener {
            findNavController().navigate(R.id.action_controlAccessFragment_to_screenTimeLimit)
        }



    }


}