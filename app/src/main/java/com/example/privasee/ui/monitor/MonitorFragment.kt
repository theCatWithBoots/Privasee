package com.example.privasee.ui.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import com.example.privasee.databinding.FragmentMonitorBinding
import com.example.privasee.ui.monitor.gallery.SeeSnapshots
import kotlinx.android.synthetic.main.fragment_add_user.*
import kotlinx.android.synthetic.main.fragment_control_access.*
import kotlinx.android.synthetic.main.fragment_monitor.*


class MonitorFragment : Fragment() {

    private var _binding: FragmentMonitorBinding? = null
    private val binding get() = _binding!!

    val thresholdForSnapshots = arrayOf(8000, 9000, 10000,11000, 12000, 13000, 14000, 15000 )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonitorBinding.inflate(inflater, container, false)

        val spinner = binding.root.findViewById<Spinner>(R.id.spinnerThreshold)
        val arrayAdapter = ArrayAdapter<Int>(requireContext(), R.layout.spinner_item_threshold, thresholdForSnapshots)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var threshold =  spinner.getSelectedItem().toString()

                val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val editor = sp.edit()

                editor.apply(){
                    putInt("threshold", threshold.toInt())
                }.apply()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        appSnapshots.setOnClickListener {
            findNavController().navigate(R.id.action_monitorFragment_to_monitorFragmentAppSnapshots)
        }

        timedSnapshots.setOnClickListener {
            findNavController().navigate(R.id.action_monitorFragment_to_monitorFragmenTimedSnapshots)
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var t = sp.getInt("threshold", 8000)
        setThreshold.text = "Current Threshold is $t"

        var r = (sp.getBoolean("result", false))
        btnresult.text = "$r"

    }




}