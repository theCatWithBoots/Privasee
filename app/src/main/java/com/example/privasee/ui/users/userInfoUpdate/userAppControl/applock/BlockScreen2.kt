package com.example.privasee.ui.users.userInfoUpdate.userAppControl.applock

import android.content.Intent
import android.graphics.PixelFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.privasee.databinding.ActivityBlockScreenBinding
import com.example.privasee.AppAccessService

class BlockScreen2 : AppCompatActivity() {

    private lateinit var binding: ActivityBlockScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityBlockScreenBinding.inflate(layoutInflater)

        setContentView(binding.root, WindowManager.LayoutParams().apply {

            flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            PixelFormat.TRANSLUCENT
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        })
        val packageNames = (intent?.getStringArrayListExtra("packageNames"))

        val intent = Intent(this@BlockScreen2, AppAccessService::class.java)
        intent.putExtra("action", "addLock")
        intent.putStringArrayListExtra("packageNames", ArrayList(packageNames))
        this@BlockScreen2.startService(intent)

    }

    override fun onBackPressed() {
        // Leave empty to disable
    }
}