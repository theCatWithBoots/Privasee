package com.example.privasee.ui.initialRun

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.privasee.R
import com.example.privasee.database.model.App
import com.example.privasee.database.model.User
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentSetupOwnerBinding
import com.example.privasee.ui.users.addUser.AddUserCapturePhoto
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock.BlockScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SetupOwnerFragment : Fragment() {

    private var _binding: FragmentSetupOwnerBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mAppViewModel: AppViewModel

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupOwnerBinding.inflate(inflater, container, false)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        binding.btnSetupOwnerFinish.setOnClickListener {

            val name = binding.etSetName.text.toString()

            // Initialize the Owner information
            if(name.isNotEmpty()) {
                val userInfo = User(0, name, isOwner = true)
                mUserViewModel.addUser(userInfo)
                saveInstalledAppsToDB()
                findNavController().navigate(R.id.action_setupOwnerFragment_to_mainActivity)
                requireActivity().finishAffinity()
            } else
                Toast.makeText(requireContext(), "Please input your name", Toast.LENGTH_SHORT).show()


        }

        binding.btnOwnerRegisterFace.setOnClickListener {
            val intent = Intent(requireContext(), AddUserCapturePhoto::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null
    }

    private fun saveInstalledAppsToDB() {
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packageManager = requireContext().packageManager
        val resolveInfoList = packageManager?.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        // Check for duplicated apps existing in the database
        if (resolveInfoList != null) {

            lifecycleScope.launch(Dispatchers.IO) {
                val appsInDb = mAppViewModel.getAllData()
                for (resolveInfo in resolveInfoList) {

                    val packageName = resolveInfo.activityInfo.packageName
                    val appName = packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString()

                    var isDuplicate = false

                    if (appsInDb.isNotEmpty()) {
                        for(app in appsInDb) {
                            if(app.appName == appName) {
                                isDuplicate = true
                                break
                            }
                        }
                    }

                    if(!isDuplicate) {
                        val appInfo = App(packageName = packageName, appName = appName)
                        mAppViewModel.addApp(appInfo)
                    }
                }

            }
        }
    }
}