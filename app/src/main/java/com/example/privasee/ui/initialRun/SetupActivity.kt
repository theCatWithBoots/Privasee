package com.example.privasee.ui.initialRun

import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.privasee.R
import com.example.privasee.database.model.App
import com.example.privasee.database.model.Restriction
import com.example.privasee.database.model.User
import com.example.privasee.database.viewmodel.AppViewModel
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.ActivitySetupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private lateinit var setupNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make this activity the nav host fragment for the navgraph for the initial run fragments
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvSetup) as NavHostFragment
        setupNavController = navHostFragment.navController
        setupActionBarWithNavController(setupNavController)

    }

    // Enable action bar's back button
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fcvSetup)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Disable back pressed
    override fun onBackPressed() {}
}
