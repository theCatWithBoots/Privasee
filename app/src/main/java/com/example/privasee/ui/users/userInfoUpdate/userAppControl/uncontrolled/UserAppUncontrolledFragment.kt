package com.example.privasee.ui.users.userInfoUpdate.userAppControl.uncontrolled

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privasee.R
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.databinding.FragmentUserAppUncontrolledBinding
import com.example.privasee.AppAccessService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserAppUncontrolledFragment : Fragment() {

    private var _binding: FragmentUserAppUncontrolledBinding? = null
    private val binding get() = _binding!!

    private lateinit var mRestrictionViewModel: RestrictionViewModel
    private lateinit var mAppViewModel: AppViewModel

    private var job1: Job? = null
    private var job2: Job? = null
    private var job3: Job? = null

    private val args: UserAppUncontrolledFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAppUncontrolledBinding.inflate(inflater, container, false)

        // Nav args
        val userId = args.userId
        val bundle = Bundle()
        bundle.putInt("userId", userId)

        // Recyclerview adapter
        val adapter = UserAppUncontrolledAdapter()
        binding.rvAppUncontrolled.adapter = adapter
        binding.rvAppUncontrolled.layoutManager = LinearLayoutManager(requireContext())

        // Database queries
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        job1 = lifecycleScope.launch(Dispatchers.IO) {
            val uncontrolledList = mRestrictionViewModel.getAllUncontrolledApps(userId)
            withContext(Dispatchers.Main) {
                uncontrolledList.observe(viewLifecycleOwner, Observer {
                    adapter.setData(it)
                })
            }
        }

        // Buttons
        binding.btnControlledList.setOnClickListener {
            findNavController().navigate(R.id.action_userAppUncontrolledFragment_to_userAppControlledFragment, bundle)
        }

        // Update new list of controlled apps
        binding.btnApplyUncontrolled.setOnClickListener {


            val newRestriction = adapter.getCheckedApps()
            job2 = lifecycleScope.launch(Dispatchers.IO) {
                for (restrictionId in newRestriction)
                    mRestrictionViewModel.updateControlledApps(restrictionId, true)
            }

            if (newRestriction.isNotEmpty()) {
                // Send data to Accessibility Service on monitoring
                job3 = lifecycleScope.launch(Dispatchers.IO) {

                    val newMonitoredListPackageName: MutableList<String> = mutableListOf()
                    val newMonitoredListAppName: MutableList<String> = mutableListOf()

                    for (restrictionId in newRestriction) {
                        val appId = mRestrictionViewModel.getPackageId(restrictionId)
                        newMonitoredListPackageName.add(mAppViewModel.getPackageName(appId))
                        newMonitoredListAppName.add(mAppViewModel.getAppName(appId))
                    }
                    val intent = Intent(requireContext(), AppAccessService::class.java)
                    intent.putExtra("action", "addControl" )
                    intent.putStringArrayListExtra("addControlAppPackageName", ArrayList(newMonitoredListPackageName))
                    intent.putStringArrayListExtra("addControlAppName", ArrayList(newMonitoredListAppName))
                    requireContext().startService(intent)
                }

                findNavController().navigate(
                    R.id.action_userAppUncontrolledFragment_to_userAppControlledFragment,
                    bundle
                )
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job1?.cancel()
        job2?.cancel()
        _binding = null
    }
}