# Android Phone App - Complete Implementation

This is the companion app for your Android phone that receives the skip command from your watch and clicks the YouTube skip button.

## 1. AdSkipAccessibilityService.kt

This is the core service that detects and clicks the "Skip Ad" button in YouTube.

```kotlin
package com.whattoyouwantwired.adskipremote.phone

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.android.gms.wearable.*

class AdSkipAccessibilityService : AccessibilityService(), MessageClient.OnMessageReceivedListener {

    private lateinit var messageClient: MessageClient
    private var shouldSkipAd = false
    
    companion object {
        private const val TAG = "AdSkipService"
        private const val SKIP_AD_PATH = "/skip_ad"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
        
        // Initialize MessageClient to receive messages from watch
        messageClient = Wearable.getMessageClient(this)
        messageClient.addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        messageClient.removeListener(this)
    }

    // Called when watch sends skip command
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path}")
        
        if (messageEvent.path == SKIP_AD_PATH) {
            shouldSkipAd = true
            Log.d(TAG, "Skip command received from watch")
            
            // Try to skip immediately if YouTube is active
            tryToSkipAd()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // Only process events from YouTube
        if (event.packageName != YOUTUBE_PACKAGE) return
        
        // If skip command was sent, try to find and click skip button
        if (shouldSkipAd) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    tryToSkipAd()
                }
            }
        }
    }

    private fun tryToSkipAd() {
        val rootNode = rootInActiveWindow ?: return
        
        // Try different text variations YouTube uses for skip button
        val skipTexts = listOf(
            "Skip ad",
            "Skip ads", 
            "Skip Ad",
            "Skip Ads",
            "SKIP AD",
            "SKIP ADS"
        )
        
        for (skipText in skipTexts) {
            val skipNodes = rootNode.findAccessibilityNodeInfosByText(skipText)
            
            for (node in skipNodes) {
                if (node.isClickable) {
                    // Found clickable skip button
                    Log.d(TAG, "Found skip button: $skipText")
                    val clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    
                    if (clicked) {
                        Log.d(TAG, "Successfully clicked skip button!")
                        shouldSkipAd = false
                        rootNode.recycle()
                        return
                    }
                } else {
                    // Try parent node if text node itself isn't clickable
                    var parent = node.parent
                    var attempts = 0
                    
                    while (parent != null && attempts < 5) {
                        if (parent.isClickable) {
                            Log.d(TAG, "Found clickable parent for skip button")
                            val clicked = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            
                            if (clicked) {
                                Log.d(TAG, "Successfully clicked skip button via parent!")
                                shouldSkipAd = false
                                rootNode.recycle()
                                return
                            }
                        }
                        parent = parent.parent
                        attempts++
                    }
                }
            }
        }
        
        rootNode.recycle()
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }
}
```

## 2. accessibility_service_config.xml

Place this in `res/xml/accessibility_service_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeWindowStateChanged|typeWindowContentChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagReportViewIds|flagRetrieveInteractiveWindows"
    android:canRetrieveWindowContent="true"
    android:description="@string/accessibility_service_description"
    android:notificationTimeout="100"
    android:packageNames="com.google.android.youtube"
    android:settingsActivity=".MainActivity" />
```

## 3. MainActivity.kt (Phone App)

This activity helps users enable the accessibility service.

```kotlin
package com.whattoyouwantwired.adskipremote.phone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.statusText)
        val enableButton = findViewById<Button>(R.id.enableButton)
        val instructionsText = findViewById<TextView>(R.id.instructionsText)

        // Check if accessibility service is enabled
        updateServiceStatus(statusText)

        enableButton.setOnClickListener {
            // Open accessibility settings
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        instructionsText.text = """
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
        val statusText = findViewById<TextView>(R.id.statusText)
        updateServiceStatus(statusText)
    }

    private fun updateServiceStatus(statusText: TextView) {
        val isEnabled = isAccessibilityServiceEnabled()
        
        if (isEnabled) {
            statusText.text = "✓ Service is ENABLED and ready"
            statusText.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            statusText.text = "✗ Service is DISABLED"
            statusText.setTextColor(getColor(android.R.color.holo_red_dark))
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
```

## 4. activity_main.xml

Place this in `res/layout/activity_main.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center">

    <TextView
        android:id="@+id/titleText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="YouTube Ad Skip Remote"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="32dp" />

    <TextView
        android:id="@+id/statusText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Status: Unknown"
        android:textSize="18sp"
        android:layout_marginBottom="24dp" />

    <Button
        android:id="@+id/enableButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Enable Accessibility Service"
        android:textSize="16sp"
        android:layout_marginBottom="32dp" />

    <TextView
        android:id="@+id/instructionsText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="14sp"
        android:lineSpacingExtra="4dp" />

</LinearLayout>
```

## 5. AndroidManifest.xml (Phone App)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.whattoyouwantwired.adskipremote.phone">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="com.google.android.wearable.permission.MESSAGE_API" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Ad Skip Remote"
        android:theme="@style/Theme.AppCompat.Light">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="Ad Skip Remote">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".AdSkipAccessibilityService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="false">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service>

    </application>
</manifest>
```

## 6. strings.xml

Place this in `res/values/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Ad Skip Remote</string>
    <string name="accessibility_service_description">
        Allows your Galaxy Watch to remotely click the Skip Ad button in YouTube. 
        This service only clicks when you send the command from your watch and does not block ads.
    </string>
</resources>
```

## 7. build.gradle (Module: phone)

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.whattoyouwantwired.adskipremote.phone'
    compileSdk 34

    defaultConfig {
        applicationId "com.whattoyouwantwired.adskipremote.phone"
        minSdk 26  // Android 8.0
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'com.google.android.gms:play-services-wearable:18.1.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```

## Setup Instructions

1. **Create Android Studio Project**: Multi-module project with "wear" and "phone" modules
2. **Ensure Same Package Name**: Both modules must have the same applicationId
3. **Build & Install**: Install both apps (phone and watch)
4. **Enable Accessibility**: Open phone app → tap Enable button → find "Ad Skip Remote" → toggle ON
5. **Test**: Open YouTube on phone, play video with ad, press watch button when skip appears

This solution is clean, efficient, and respects YouTube's ad model by only automating the manual skip action.
