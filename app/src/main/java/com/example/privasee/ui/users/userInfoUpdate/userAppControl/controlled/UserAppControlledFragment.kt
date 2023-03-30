package com.example.privasee.ui.users.userInfoUpdate.userAppControl.controlled

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
import com.example.privasee.databinding.FragmentUserAppControlledBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAppControlledFragment : Fragment() {

    private var _binding: FragmentUserAppControlledBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mRestrictionViewModel: RestrictionViewModel

    private val args: UserAppControlledFragmentArgs by navArgs()

    private var job1: Job? = null
    private var job2: Job? = null

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
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mRestrictionViewModel = ViewModelProvider(this)[RestrictionViewModel::class.java]

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
            val newUncontrolledList = adapter.getCheckedApps()
            job2 = lifecycleScope.launch(Dispatchers.IO) {
                for (restrictionId in newUncontrolledList)
                    mRestrictionViewModel.updateControlledApps(restrictionId, false)
            }
            if (newUncontrolledList.isNotEmpty())
                findNavController().navigate(R.id.action_userAppControlledFragment_to_userAppUncontrolledFragment, bundle)
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