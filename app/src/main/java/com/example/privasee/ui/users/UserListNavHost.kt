package com.example.privasee.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.privasee.R
import com.example.privasee.databinding.FragmentUserListNavhostBinding

class UserListNavHost : Fragment() {

    private var _binding: FragmentUserListNavhostBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListNavhostBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fcvUser) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(activity as AppCompatActivity, navController)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}