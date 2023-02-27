package com.example.privasee.ui.controlAccess

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.privasee.databinding.FragmentControlAccessBinding
import kotlinx.android.synthetic.main.fragment_add_user.*
import kotlinx.android.synthetic.main.fragment_control_access.*


class ControlAccessFragment : Fragment() {

    private var _binding: FragmentControlAccessBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        devicePolicyManager = requireActivity().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        activityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        compName = ComponentName(requireContext(), MyAdmin::class.java)

        _binding = FragmentControlAccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if(sp.contains("theTime")){
            editTime.setText(sp.getLong("theTime", 0).toInt())
        }else{
            editTime.setText("10")
        }

        setTimer.setOnClickListener {
            val active = devicePolicyManager!!.isAdminActive(compName!!)

            if (active) {
                //devicePolicyManager!!.lockNow()
                var timerString = editTime.getText().toString()
                var timerInt = timerString.toLong()

                val editor = sp.edit()
                editor.apply(){
                    putLong("theTime", timerInt)
                }.apply()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to enable the Admin Device Features",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        givePermission.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Additional text explaining why we need this permission"
            )
            startActivityForResult(intent, RESULT_ENABLE)
        }

        disablePermission.setOnClickListener{
            devicePolicyManager!!.removeActiveAdmin(compName!!)
            disablePermission.setVisibility(View.GONE)
            givePermission.setVisibility(View.VISIBLE)
        }

    }
    override fun onResume() {
        super.onResume()
        val isActive = devicePolicyManager!!.isAdminActive(compName!!)
        disablePermission.setVisibility(if (isActive) View.VISIBLE else View.GONE)
        givePermission.setVisibility(if (isActive) View.GONE else View.VISIBLE)
    }
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_ENABLE -> if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    requireContext(),
                    "You have enabled the Admin Device features",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Problem to enable the Admin Device features",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RESULT_ENABLE = 11
        private var devicePolicyManager: DevicePolicyManager? = null
        private var activityManager: ActivityManager? = null
        private var compName: ComponentName? = null    }
}