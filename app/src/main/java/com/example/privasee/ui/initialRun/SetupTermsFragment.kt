package com.example.privasee.ui.initialRun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.privasee.R
import com.example.privasee.databinding.FragmentSetupTermsBinding

class SetupTermsFragment : Fragment() {

    private var _binding: FragmentSetupTermsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupTermsBinding.inflate(inflater, container, false)

        binding.btnTermsNext.setOnClickListener {
            findNavController().navigate(R.id.action_setupTermsFragment_to_setupPermissionsFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}