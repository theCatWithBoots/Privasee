package com.example.privasee.ui.monitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceFragmentCompat
import com.example.privasee.R
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentMonitorBinding
import com.example.privasee.databinding.FragmentUserBinding

class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}