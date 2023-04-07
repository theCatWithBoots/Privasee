package com.example.privasee.ui.controlAccess

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.privasee.R
import com.example.privasee.databinding.FragmentControlAccessStartBinding

class ControlAccessStart : Fragment() {

    private var _binding: FragmentControlAccessStartBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentControlAccessStartBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragmentControlAccess = childFragmentManager.findFragmentById(R.id.control_access_start) as NavHostFragment
        navController = navHostFragmentControlAccess.navController

        // set up the ActionBar
      //  setupActionBarWithNavController(activity as AppCompatActivity, navController)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}