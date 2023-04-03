package com.example.privasee.ui.initialRun

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.privasee.R
import com.example.privasee.database.model.App
import com.example.privasee.database.model.Restriction
import com.example.privasee.database.model.User
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentSetupOwnerBinding
import com.example.privasee.ui.users.userInfoUpdate.userAppMonitoring.UserAppMonitoringActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SetupOwnerFragment : Fragment() {

    private var _binding: FragmentSetupOwnerBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mAppViewModel: AppViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel

    private var job: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupOwnerBinding.inflate(inflater, container, false)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]

        // Initialize the Owner information
        val userInfo = User(0, "owner", isOwner = true)
        mUserViewModel.addUser(userInfo)

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packageManager = requireContext().packageManager
        val resolveInfoList = packageManager?.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        if (resolveInfoList != null) {
            for (resolveInfo in resolveInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                val appName = packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString()
                val appInfo = App(0, packageName, appName)
                mAppViewModel.addApp(appInfo)
            }
        }

        binding.btnSelectApps.setOnClickListener {
            Intent(requireContext(), UserAppMonitoringActivity::class.java).also { intent ->
                intent.putExtra("userId", 1)
                startActivity(intent)
            }
        }

        binding.btnSetupOwnerFinish.setOnClickListener {
            findNavController().navigate(R.id.action_setupOwnerFragment_to_mainActivity)
            requireActivity().finishAffinity()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null

    }

}