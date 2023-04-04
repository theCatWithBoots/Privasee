package com.example.privasee.ui.monitor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentMonitorAppSnapshotsBinding
import com.example.privasee.AppAccessService
import com.example.privasee.utils.CheckPermissionUtils
import kotlinx.android.synthetic.main.fragment_monitor_app_snapshots.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MonitorFragmentAppSnapshots: Fragment() {



    private var _binding: FragmentMonitorAppSnapshotsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel
    private lateinit var mAppViewModel: AppViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonitorAppSnapshotsBinding.inflate(inflater, container, false)


        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]



        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_monitorFragmentAppSnapshots_to_monitorFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback (callback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        accessibility2.setOnClickListener {
            CheckPermissionUtils.checkAccessibilityPermission(requireContext())
        }

        val monitoredAppPackageNames: MutableList<String> = mutableListOf()

        btnActivate.setOnClickListener {

            lifecycleScope.launch(Dispatchers.Main) {


                val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                var cuser = (sp.getInt("CurrentUser", 69))


                mRestrictionViewModel.getAllMonitoredApps(cuser)
                    .observe(viewLifecycleOwner) { controlledList ->

                        // Take package names
                        lifecycleScope.launch(Dispatchers.IO) {

                            for (restrictedApp in controlledList) {
                                val appId = restrictedApp.packageId
                                val packageName = mAppViewModel.getPackageName(appId)
                                monitoredAppPackageNames.add(packageName)
                            }
                            if (monitoredAppPackageNames.size > 0) {

                                val intent = Intent(requireContext(), AppAccessService::class.java)
                                intent.putExtra("action", "addMonitor")
                                intent.putStringArrayListExtra(
                                    "packageNames",
                                    ArrayList(monitoredAppPackageNames.toArrayList())
                                )
                                requireContext().startService(intent)


                            }

                        }

                    }
            }

            Toast.makeText(
                requireContext(),
                "App Snapshots is enabled",
                Toast.LENGTH_LONG
            ).show()

        }


        btnDeactivate.setOnClickListener {
            if (monitoredAppPackageNames.size > 0) {
                val intent =
                    Intent(requireContext(), AppAccessService::class.java)
                intent.putExtra("action", "removeMonitor")
                intent.putStringArrayListExtra(
                    "packageNames",
                    ArrayList(monitoredAppPackageNames.toArrayList())
                )
                requireContext().startService(intent)

                Toast.makeText(
                    requireContext(),
                    "App Snapshots is Disabled",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }

}