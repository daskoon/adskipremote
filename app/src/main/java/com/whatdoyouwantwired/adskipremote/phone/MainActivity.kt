package com.whatdoyouwantwired.adskipremote.phone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.whatdoyouwantwired.adskipremote.phone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enableButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        binding.instructionsText.text = """
            How to use:
            
            1. Enable the "Ad Skip Remote" accessibility service below
            2. Open YouTube on your phone
            3. When an ad appears, press the SKIP AD button on your Galaxy Watch 6
            4. The app will automatically click the skip button when it appears
            
            Note: This only works when the skip button is visible (after 5 seconds on skippable ads)
        """.trimIndent()
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }

    private fun updateServiceStatus() {
        if (isAccessibilityServiceEnabled()) {
            binding.statusText.text = "✓ Service is ENABLED and ready"
            binding.statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            binding.statusText.text = "✗ Service is DISABLED"
            binding.statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        
        for (service in enabledServices) {
            if (service.id.contains(packageName)) {
                return true
            }
        }
        return false
    }
}