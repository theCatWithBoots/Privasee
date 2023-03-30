package com.example.privasee.ui.users.userInfoUpdate.userAppMonitoring.monitored

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
import com.example.privasee.database.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentUserAppMonitoredBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserAppMonitoredFragment : Fragment() {

    private var _binding: FragmentUserAppMonitoredBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel

    private val args: UserAppMonitoredFragmentArgs by navArgs()

    private var job1: Job? = null
    private var job2: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAppMonitoredBinding.inflate(inflater, container, false)

        // Recyclerview adapter
        val adapter = UserAppMonitoredAdapter()
        binding.rvAppMonitored.adapter = adapter
        binding.rvAppMonitored.layoutManager = LinearLayoutManager(requireContext())

        // Database view-models
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]

        // Nav args
        val userId = args.userId
        val bundle = Bundle()
        bundle.putInt("userId", userId)

        // Observe Live data of unmonitored list
        job1 = lifecycleScope.launch {
            val monitoredList = mRestrictionViewModel.getAllMonitoredApps(userId)
            withContext(Dispatchers.Main) {

                monitoredList.observe(viewLifecycleOwner, Observer {
                    adapter.setData(it)
                })
            }
        }

        // Buttons
        binding.btnUnmonitoredList.setOnClickListener {
            findNavController().navigate(R.id.action_appMonitoredFragment_to_appUnmonitoredFragment, bundle)
        }

        // Update new list of monitored apps
        binding.btnApplyMonitored.setOnClickListener {
            val newUnmonitoredList = adapter.getCheckedApps()
            job2 = lifecycleScope.launch(Dispatchers.IO) {
                for (restrictionId in newUnmonitoredList)
                    mRestrictionViewModel.updateMonitoredApps(restrictionId, false)
            }
            if (newUnmonitoredList.isNotEmpty())
                findNavController().navigate(R.id.action_appMonitoredFragment_to_appUnmonitoredFragment, bundle)
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