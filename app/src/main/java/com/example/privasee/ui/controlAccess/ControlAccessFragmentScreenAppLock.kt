package com.example.privasee.ui.controlAccess

import android.app.Activity
import android.app.ActivityManager
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import com.example.privasee.database.model.User
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentControlAccessApplockBinding
import com.example.privasee.AppAccessService
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.UserAppControllingActivity
import com.example.privasee.utils.CheckPermissionUtils
import kotlinx.android.synthetic.main.fragment_control_access_applock.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ControlAccessFragmentScreenAppLock : Fragment() {

    private var _binding: FragmentControlAccessApplockBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel
    private lateinit var mAppViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentControlAccessApplockBinding.inflate(inflater, container, false)
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

                        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                        val editor = sp.edit()
                        editor.apply(){
                            putInt("CurrentUser", selectedUser.id)
                        }.apply()

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

                                            var timerString = timeButton2.getText().toString()

                                            val units = timerString.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                                                .toTypedArray() //will break the string up into an array

                                            val hour = units[0].toInt() //first element

                                            val minutes = units[1].toInt() //second element

                                            val duration = 60 * hour + minutes //add up our values

                                            var timerInt = duration.toLong()


                                            activity!!.startService(
                                                Intent(
                                                    activity,
                                                    AppLockTimer::class.java
                                                ).putExtra("Timer",timerInt.toString())
                                                    .putStringArrayListExtra("controlledAppPackageNames", controlledAppPackageNames.toArrayList())
                                            )
                                        }

                                        binding.btnTestService2.setOnClickListener {

                                            activity!!.stopService(
                                                Intent(
                                                    activity,
                                                    AppLockTimer::class.java
                                                ))


                                            val intent = Intent(requireContext(), AppAccessService::class.java)
                                            intent.putExtra("action", "removeLock")
                                            intent.putStringArrayListExtra("packageNames", ArrayList(controlledAppPackageNames))
                                            requireContext().startService(intent)
                                        }

                                    }else{
                                        //
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

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_appLock_to_controlAccessFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback (callback)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        accessibility.setOnClickListener {
            CheckPermissionUtils.checkAccessibilityPermission(requireContext())
        }

        btnSelectApps.setOnClickListener {
            val intent = Intent(requireContext(), UserAppControllingActivity::class.java)
            startActivity(intent)
        }

        timeButton2.setOnClickListener {
            popTimePicker()
        }

        setOwner.setOnClickListener {
            findNavController().navigate(R.id.action_appLock_to_setOwner)
        }
    }

    fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }

    fun popTimePicker() {
        // var timeButton: Button? = null
        var hour = 0
        var minute = 0

        val onTimeSetListener =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                timeButton2!!.text =
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

            var seconds = (millisUntilFinished/1000)
            var minutes = (seconds/60)
            val hours = (minutes/60)

            seconds = seconds % 60
            minutes = minutes % 60


            remainingTime2.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds))


          //      remainingTime2.setText(java.lang.Long.toString(millisUntilFinished / 1000))
            //remainingTime.setText("flsjflksdjf")
        }
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter(
            AppLockTimer.COUNTDOWN_BR))


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