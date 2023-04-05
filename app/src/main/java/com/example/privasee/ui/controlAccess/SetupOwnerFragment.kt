package com.example.privasee.ui.controlAccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentSetupOwnerBinding
import kotlinx.coroutines.Job


class SetupOwnerFragment : Fragment() {

    private var _binding: FragmentSetupOwnerBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mAppViewModel: AppViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel

    private var job: Job? = null
    private var ownerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupOwnerBinding.inflate(inflater, container, false)
/*
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

        job = lifecycleScope.launch(Dispatchers.IO) {

            val appList = mAppViewModel.getAllDataLive()
            ownerId = mUserViewModel.getOwnerId(true)

            withContext(Dispatchers.Main) {
                appList.observe(viewLifecycleOwner) {
                    for(app in it) {
                        val appName = app.appName
                        val appId = app.id
                        val restriction = Restriction(0, appName, monitored = false, controlled = false, ownerId, appId)
                        mRestrictionViewModel.addRestriction(restriction)
                    }
                }
            }
        }

        binding.btnSelectApps.setOnClickListener {
            Intent(requireContext(), UserAppControllingActivity::class.java).also { intent ->
                intent.putExtra("userId", ownerId)
                startActivity(intent)
            }
        }

        binding.btnSetupOwnerFinish.setOnClickListener {
          // findNavController().navigate(R.id.action_controlAccessFragment_to_setupOwner)
           // requireActivity().finishAffinity()
            getFragmentManager()?.popBackStackImmediate();
        }

        binding.btnOwnerRegisterFace.setOnClickListener {
            val intent = Intent(requireContext(), AddUserCapturePhoto::class.java)
            startActivity(intent)
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_setOwner_to_appLock2)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback (callback)
*/
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null

    }

}