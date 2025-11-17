package com.whatdoyouwantwired.adskipremote.phone

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdSkipAccessibilityService : AccessibilityService(), MessageClient.OnMessageReceivedListener {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var messageClient: MessageClient

    companion object {
        private const val TAG = "AdSkipService"
        private const val SKIP_AD_PATH = "/skip_ad"
        private const val SKIP_AD_SUCCESS_PATH = "/skip_ad_success"
        private const val SKIP_AD_FAILURE_PATH = "/skip_ad_failure"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        private const val SKIP_BUTTON_ID = "com.google.android.youtube:id/skip_ad_button"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
        messageClient = Wearable.getMessageClient(this)
        messageClient.addListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == SKIP_AD_PATH) {
            Log.d(TAG, "Skip command received from watch")
            if (!tryToSkipAd()) {
                sendMessageToWatch(SKIP_AD_FAILURE_PATH)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // This service is only for skipping ads, so we don't need to do anything here.
        // The skip logic is triggered by the message from the watch.
    }

    private fun tryToSkipAd(): Boolean {
        val rootNode = rootInActiveWindow ?: return false

        val skipButtons = rootNode.findAccessibilityNodeInfosByViewId(SKIP_BUTTON_ID)
        if (skipButtons.isNotEmpty()) {
            for (node in skipButtons) {
                if (click(node)) {
                    Log.d(TAG, "Successfully clicked skip button found by ID!")
                    sendMessageToWatch(SKIP_AD_SUCCESS_PATH)
                    return true
                }
            }
        }

        // Fallback to text search
        val skipTexts = listOf("Skip Ad", "Skip ad", "Skip Ads", "Skip ads")
        for (skipText in skipTexts) {
            val skipButtonsByText = rootNode.findAccessibilityNodeInfosByText(skipText)
            for (node in skipButtonsByText) {
                if (click(node)) {
                    Log.d(TAG, "Successfully clicked skip button found by text: $skipText")
                    sendMessageToWatch(SKIP_AD_SUCCESS_PATH)
                    return true
                }
            }
        }

        return false
    }

    private fun click(node: AccessibilityNodeInfo): Boolean {
        if (node.isClickable) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        var parent = node.parent
        while (parent != null) {
            if (parent.isClickable) {
                return parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            parent = parent.parent
        }
        return false
    }

    private fun sendMessageToWatch(path: String) {
        serviceScope.launch {
            try {
                val nodes = Wearable.getNodeClient(this@AdSkipAccessibilityService).connectedNodes.await()
                for (node in nodes) {
                    messageClient.sendMessage(node.id, path, null).await()
                }
                Log.d(TAG, "Sent '$path' message to watch")
            } catch (e: Exception) {
                Log.e(TAG, "Error sending message to watch", e)
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        messageClient.removeListener(this)
    }
}