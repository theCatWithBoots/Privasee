package com.example.privasee.ui.users.userInfoUpdate.userAppControl.uncontrolled

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
import com.example.privasee.databinding.FragmentUserAppUncontrolledBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAppUncontrolledFragment : Fragment() {

    private var _binding: FragmentUserAppUncontrolledBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel

    private var job1: Job? = null
    private var job2: Job? = null

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
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]

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
            val newControlledList = adapter.getCheckedApps()
            job2 = lifecycleScope.launch(Dispatchers.IO) {
                for (restrictionId in newControlledList)
                    mRestrictionViewModel.updateControlledApps(restrictionId, true)
            }
            if (newControlledList.isNotEmpty())
                findNavController().navigate(R.id.action_userAppUncontrolledFragment_to_userAppControlledFragment, bundle)
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