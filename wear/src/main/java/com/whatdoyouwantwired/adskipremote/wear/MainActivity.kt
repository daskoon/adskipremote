package com.whatdoyouwantwired.adskipremote.wear

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var messageClient: MessageClient
    private var buttonState by mutableStateOf<ButtonState>(ButtonState.Ready)

    companion object {
        private const val SKIP_AD_PATH = "/skip_ad"
        private const val SKIP_AD_SUCCESS_PATH = "/skip_ad_success"
        private const val SKIP_AD_FAILURE_PATH = "/skip_ad_failure"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageClient = Wearable.getMessageClient(this)
        setContent {
            MaterialTheme {
                SkipAdScreen(
                    buttonState = buttonState,
                    onSkipClick = {
                        scope.launch {
                            sendSkipCommand()
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        messageClient.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        messageClient.removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        scope.launch {
            when (messageEvent.path) {
                SKIP_AD_SUCCESS_PATH -> {
                    buttonState = ButtonState.Success
                    vibrate()
                    delay(2000)
                    buttonState = ButtonState.Ready
                }
                SKIP_AD_FAILURE_PATH -> {
                    buttonState = ButtonState.Failure
                    delay(2000)
                    buttonState = ButtonState.Ready
                }
            }
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    private suspend fun sendSkipCommand() {
        buttonState = ButtonState.Sending
        try {
            val nodes = Wearable.getNodeClient(this).connectedNodes.await()
            if (nodes.isEmpty()) {
                buttonState = ButtonState.Failure
                return
            }
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, SKIP_AD_PATH, null).await()
            }
        } catch (e: Exception) {
            buttonState = ButtonState.Failure
        }
    }
}

enum class ButtonState(val text: String, val color: Color) {
    Ready("SKIP AD", Color(0xFFFF0000)),
    Sending("Sending...", Color.DarkGray),
    Success("Success!", Color(0xFF00C853)),
    Failure("Failed", Color.DarkGray)
}

@Composable
fun SkipAdScreen(buttonState: ButtonState, onSkipClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "YouTube Ad Skip",
                style = MaterialTheme.typography.title1
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSkipClick,
                modifier = Modifier.size(120.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = buttonState.color
                ),
                enabled = buttonState == ButtonState.Ready
            ) {
                Text(
                    text = buttonState.text,
                    style = MaterialTheme.typography.button,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}