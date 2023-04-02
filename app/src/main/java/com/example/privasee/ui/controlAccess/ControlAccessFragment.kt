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


    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel
    private lateinit var mAppViewModel: AppViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentControlAccessBinding.inflate(inflater, container, false)
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        lifecycleScope.launch(Dispatchers.Main) {

            mUserViewModel.getAllDataLive.observe(viewLifecycleOwner) {

                val spinner = binding.root.findViewById<Spinner>(R.id.spinnerUsers)

                // Show all user using names.
                val spinnerAdapter = object : ArrayAdapter<User>(requireContext(), R.layout.spinner_item_user, it) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val user = getItem(position)
                        if (user != null)
                            (view.findViewById<TextView>(android.R.id.text1)).text = user.name
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = if (convertView == null) {
                            val inflater = LayoutInflater.from(context)
                            inflater.inflate(R.layout.spinner_item_user, parent, false)
                        } else
                            convertView
                        val user = getItem(position)
                        if (user != null)
                            (view.findViewById<TextView>(android.R.id.text1)).text = user.name

                        return view
                    }
                }

                spinner.adapter = spinnerAdapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                        val selectedUser = parent.getItemAtPosition(position) as User

                        lifecycleScope.launch(Dispatchers.Main) {

                            mRestrictionViewModel.getAllControlledApps(selectedUser.id).observe(viewLifecycleOwner) { controlledList ->

                                // Take package names
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val controlledAppPackageNames: MutableList<String> = mutableListOf()
                                    for(restrictedApp in controlledList) {
                                        val appId = restrictedApp.packageId
                                        val packageName = mAppViewModel.getPackageName(appId)
                                        controlledAppPackageNames.add(packageName)
                                    }

                                    // Set on click listener for adding or removing app lock
                                    if (controlledAppPackageNames.size > 0) {

                                        binding.btnTestService1.setOnClickListener {
                                            val intent = Intent(requireContext(), AppAccessService::class.java)
                                            intent.putExtra("action", "addLock")
                                            intent.putStringArrayListExtra("packageNames", ArrayList(controlledAppPackageNames))
                                            requireContext().startService(intent)
                                        }

                                        binding.btnTestService2.setOnClickListener {
                                            val intent = Intent(requireContext(), AppAccessService::class.java)
                                            intent.putExtra("action", "removeLock")
                                            intent.putStringArrayListExtra("packageNames", ArrayList(controlledAppPackageNames))
                                            requireContext().startService(intent)
                                        }

                                    }

                                }
                            }

                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Do wNothing
                    }
                }

            }
        }

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


        accessibility.setOnClickListener {
            CheckPermissionUtils.checkAccessibilityPermission(requireContext())
        }

        btnSelectApps.setOnClickListener {
            val intent = Intent(requireContext(), UserAppControllingActivity::class.java)
            startActivity(intent)
        }

        binding.setOwner.setOnClickListener {
            findNavController().navigate(R.id.action_controlAccessFragment_to_setupOwner)
        }

    }


}