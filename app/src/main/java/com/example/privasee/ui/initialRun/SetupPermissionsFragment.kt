package com.example.privasee.ui.initialRun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.privasee.R
import com.example.privasee.databinding.FragmentSetupPermissionsBinding
import com.example.privasee.utils.CheckPermissionUtils


class SetupPermissionsFragment : Fragment() {

    private var _binding: FragmentSetupPermissionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupPermissionsBinding.inflate(inflater, container, false)

        binding.btnEnableAccessibilityService.setOnClickListener {
            val isPermissionGranted = CheckPermissionUtils.isPermissionGranted(requireContext())
            if(!isPermissionGranted)
                CheckPermissionUtils.openAccessibilityServiceSettings(requireContext())
            else
                Toast.makeText(requireContext(), "Accessibility Service is already enabled. Please proceed", Toast.LENGTH_SHORT).show()
        }

        // Check permission if enabled to proceed
        binding.btnPermissionsNext.setOnClickListener {
            val isPermissionGranted = CheckPermissionUtils.isPermissionGranted(requireContext())
            if(isPermissionGranted) {
                findNavController().navigate(R.id.action_setupPermissionsFragment_to_setupOwnerFragment)
            } else {
                Toast.makeText(requireContext(), "Please enable Accessibility Service settings first before proceeding", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

