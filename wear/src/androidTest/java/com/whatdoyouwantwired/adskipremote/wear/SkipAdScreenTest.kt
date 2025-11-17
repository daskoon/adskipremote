package com.whatdoyouwantwired.adskipremote.wear

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SkipAdScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun skipAdButton_initialState_isReady() {
        // Start the app
        composeTestRule.setContent {
            SkipAdScreen(buttonState = ButtonState.Ready, onSkipClick = {})
        }

        composeTestRule.onNodeWithText("SKIP AD").assertExists()
    }

    @Test
    fun skipAdButton_whenClicked_changesStateToSending() {
        // Start the app with a mutable state
        var buttonState = ButtonState.Ready
        composeTestRule.setContent {
            SkipAdScreen(
                buttonState = buttonState,
                onSkipClick = { buttonState = ButtonState.Sending }
            )
        }

        // Click the button
        composeTestRule.onNodeWithText("SKIP AD").performClick()

        // Check that the state has changed
        composeTestRule.onNodeWithText("Sending...").assertExists()
    }
}
