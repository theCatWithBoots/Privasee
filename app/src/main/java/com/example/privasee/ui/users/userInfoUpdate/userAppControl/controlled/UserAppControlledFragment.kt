package com.example.privasee.ui.users.userInfoUpdate.userAppControl.controlled

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
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.databinding.FragmentUserAppControlledBinding
import com.example.privasee.AppAccessService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAppControlledFragment : Fragment() {

    private var _binding: FragmentUserAppControlledBinding? = null
    private val binding get() = _binding!!

    private val args: UserAppControlledFragmentArgs by navArgs()


    private lateinit var mRestrictionViewModel: RestrictionViewModel
    private lateinit var mAppViewModel: AppViewModel

    private var job1: Job? = null
    private var job2: Job? = null
    private var job3: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAppControlledBinding.inflate(inflater, container, false)

        // Nav args
        val userId = args.userId
        val bundle = Bundle()
        bundle.putInt("userId", userId)

        // Recyclerview adapter
        val adapter = UserAppControlledAdapter()
        binding.rvAppControlled.adapter = adapter
        binding.rvAppControlled.layoutManager = LinearLayoutManager(requireContext())

        // Database queries
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]
        mAppViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        job1 = lifecycleScope.launch(Dispatchers.IO) {
            val controlledList = mRestrictionViewModel.getAllControlledApps(userId)
            withContext(Dispatchers.Main) {
                controlledList.observe(viewLifecycleOwner, Observer {
                    adapter.setData(it)
                })
            }
        }

        // Buttons
        binding.btnUncontrolledList.setOnClickListener {
            findNavController().navigate(R.id.action_userAppControlledFragment_to_userAppUncontrolledFragment, bundle)
        }

        // Update new list of uncontrolled apps
        binding.btnApplyControlled.setOnClickListener {
            val newRestriction = adapter.getCheckedApps()
            job2 = lifecycleScope.launch(Dispatchers.IO) {
                for (restrictionId in newRestriction)
                    mRestrictionViewModel.updateControlledApps(restrictionId, false)
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
                    intent.putExtra("action", "removeControl" )
                    intent.putStringArrayListExtra("removeControlAppPackageName", ArrayList(newMonitoredListPackageName))
                    intent.putStringArrayListExtra("removeControlAppName", ArrayList(newMonitoredListAppName))
                    requireContext().startService(intent)
                }

                findNavController().navigate(
                    R.id.action_userAppControlledFragment_to_userAppUncontrolledFragment,
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