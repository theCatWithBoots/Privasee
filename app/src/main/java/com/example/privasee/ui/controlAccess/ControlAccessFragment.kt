package com.example.privasee.ui.controlAccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.privasee.R
import com.example.privasee.databinding.FragmentControlAccessBinding
import kotlinx.android.synthetic.main.fragment_control_access.*


class ControlAccessFragment : Fragment() {

    private var _binding: FragmentControlAccessBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentControlAccessBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       // editTime.inputType = InputType.TYPE_CLASS_NUMBER

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        /*if(sp.contains("theTime") != null){
            var t = sp.getLong("theTime", 0).toInt()
            editTime.setText("$t")
        }else{
            editTime.setText("0")
        }*/

        applock.setOnClickListener {
            findNavController().navigate(R.id.action_controlAccessFragment_to_appLock)
        }

        scrnTimeLimit.setOnClickListener {
            findNavController().navigate(R.id.action_controlAccessFragment_to_screenTimeLimit)
        }



    }


}