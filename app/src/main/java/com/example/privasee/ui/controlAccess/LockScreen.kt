package com.example.privasee.ui.controlAccess

import androidx.appcompat.app.AlertDialog
import com.example.privasee.ui.monitor.MyForegroundServices


class LockScreen(private val activity: MyForegroundServices){

     fun showDialog (){
        val builder = AlertDialog.Builder(activity)

        builder.apply {
            setMessage("This device will now be locked.")
            setTitle("Time ran out")
            setPositiveButton("ok") { dialog, which ->
                ControlAccessFragment.devicePolicyManager!!.lockNow()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}