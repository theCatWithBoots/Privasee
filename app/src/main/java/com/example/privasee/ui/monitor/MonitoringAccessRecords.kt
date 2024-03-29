package com.example.privasee.ui.monitor


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privasee.R
import com.example.privasee.database.model.Record
import com.example.privasee.database.viewmodel.RecordViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentMonitorAccessRecordsBinding
import com.example.privasee.ui.users.UserListAdapter

import kotlinx.android.synthetic.main.recycler_item_monitor_view_image.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MonitoringAccessRecords : Fragment() {

    private var _binding: FragmentMonitorAccessRecordsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mRecordViewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitorAccessRecordsBinding.inflate(inflater, container, false)

        mRecordViewModel = ViewModelProvider(this)[RecordViewModel::class.java]

        // Set adapter
        val adapter = MonitorAccessRecordsAdapter()
        binding.rvMonitorRecords.adapter = adapter
        binding.rvMonitorRecords.layoutManager = LinearLayoutManager(requireContext())

        binding.cvMonitorRecords.setOnDateChangeListener { view, year, month, day ->
            val tempMonth = month+1
            lifecycleScope.launch(Dispatchers.Main) {
                mRecordViewModel.getRecord(day, tempMonth, year).observe(viewLifecycleOwner){
                    if(it.isNotEmpty()) {
                        Log.d("tagimandos", "monitor access fragment $it $day $month $year")
                        adapter.setData(it)
                    } else {
                        Log.d("tagimandos", "Empty list")
                        val tempRecord = listOf(Record(0,0,0,0,0, "", "Empty Record"))
                        adapter.setData(tempRecord)
                    }
                }
            }
        }


        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_AccessRecords_to_monitorFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback (callback)

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
