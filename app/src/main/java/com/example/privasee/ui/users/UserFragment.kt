package com.example.privasee.ui.users

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privasee.R
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentUserBinding
import com.example.privasee.ui.monitor.gallery.SeeSnapshots
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)

        // Recyclerview
        val adapter = UserAdapter()
        binding.userFragment.adapter = adapter
        binding.userFragment.layoutManager = LinearLayoutManager(requireContext())

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

        binding.addUserButton.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_addUserFragment)
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sp.edit()
            editor.apply(){
                putString("BACK", "addUser")
            }.apply()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}