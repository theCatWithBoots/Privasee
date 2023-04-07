package com.example.privasee.ui.monitor

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.privasee.R
import com.example.privasee.database.model.User
import com.example.privasee.database.viewmodel.RestrictionViewModel
import com.example.privasee.database.viewmodel.UserViewModel
import com.example.privasee.databinding.FragmentUserInfoUpdateBinding
import com.example.privasee.databinding.RecyclerItemMonitorViewImageBinding
import com.example.privasee.ui.monitor.ViewImage
import com.example.privasee.ui.userList.userInfoUpdate.UserInfoUpdateAdapter
//import com.example.privasee.ui.userList.userInfoUpdate.userAppControl.UserAppControllingActivity
//import com.example.privasee.ui.userList.userInfoUpdate.userAppMonitoring.UserAppMonitoringActivity
import com.example.privasee.ui.users.userInfoUpdate.userAppControl.UserAppControllingActivity
import com.example.privasee.ui.users.userInfoUpdate.userAppMonitoring.UserAppMonitoringActivity
import kotlinx.android.synthetic.main.recycler_item_monitor_view_image.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewImage : Fragment() {

    private var _binding: RecyclerItemMonitorViewImageBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ViewImageArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecyclerItemMonitorViewImageBinding.inflate(inflater, container, false)


      val imageString = args.currentRecord.image

     /*   //convert it to byte array
        val data = Base64.decode(imageString, Base64.DEFAULT)
        //now convert it to bitmap
        val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)

       */
        val bitmap = BitmapFactory.decodeFile(imageString)

        binding.ViewSnapshotImage.setImageBitmap(bitmap)

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_ViewImage_to_AccessRecords)
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

