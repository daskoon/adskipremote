# Wear OS Watch App - MainActivity.kt

This is the main activity for your Galaxy Watch 6 that displays a simple button to remotely trigger the YouTube ad skip on your phone.

```kotlin
package com.whattoyouwantwired.adskipremote.wear

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.wearable.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    
    private lateinit var messageClient: MessageClient
    private val SKIP_AD_PATH = "/skip_ad"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Wearable MessageClient
        messageClient = Wearable.getMessageClient(this)
        
        setContent {
            AdSkipRemoteTheme {
                SkipAdScreen()
            }
        }
    }
    
    @Composable
    fun SkipAdScreen() {
        var buttonState by remember { mutableStateOf("Ready") }
        val scope = rememberCoroutineScope()
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "YouTube Ad Skip",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            sendSkipCommand()
                            buttonState = "Sent!"
                            kotlinx.coroutines.delay(2000)
                            buttonState = "Ready"
                        }
                    },
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000) // YouTube red
                    )
                ) {
                    Text(
                        text = "SKIP AD",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = buttonState,
                    color = if (buttonState == "Sent!") Color.Green else Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
    
    private suspend fun sendSkipCommand() {
        try {
            // Get all connected nodes (your phone)
            val nodeClient = Wearable.getNodeClient(this)
            val nodes = nodeClient.connectedNodes.await()
            
            if (nodes.isEmpty()) {
                Toast.makeText(this, "Phone not connected", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Send message to all connected nodes
            for (node in nodes) {
                messageClient.sendMessage(
                    node.id,
                    SKIP_AD_PATH,
                    "skip".toByteArray()
                ).await()
            }
            
            Toast.makeText(this, "Skip command sent!", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AdSkipRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}
```

## AndroidManifest.xml for Watch App

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.whattoyouwantwired.adskipremote.wear">

    <uses-feature android:name="android.hardware.type.watch" />
    
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="com.google.android.wearable.permission.MESSAGE_API" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Ad Skip Remote"
        android:theme="@android:style/Theme.DeviceDefault">
        
        <uses-library
            android:name="com.google.android.wearable"
            android:required="true" />

        <meta-data
            android:name="com.google.android.wearable.standalone"
            android:value="false" />

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="Skip YouTube Ad">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

## build.gradle (Module: wear)

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.whattoyouwantwired.adskipremote.wear'
    compileSdk 34

    defaultConfig {
        applicationId "com.whattoyouwantwired.adskipremote.wear"
        minSdk 30  // Wear OS 3
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion '1.5.3'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'com.google.android.gms:play-services-wearable:18.1.0'
    implementation 'androidx.wear:wear:1.3.0'
    
    // Compose for Wear OS
    implementation 'androidx.wear.compose:compose-material3:1.0.0-alpha15'
    implementation 'androidx.wear.compose:compose-foundation:1.3.0'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation 'androidx.compose.ui:ui-tooling:1.6.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```
