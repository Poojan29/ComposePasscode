package com.poojan29.passcode.passcode.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.poojan29.passcode.passcode.ui.component.DraggablePasscode
import com.poojan29.passcode.passcode.ui.theme.ComposePasscodeTheme
import com.poojan29.passcode.passcode.util.PasscodeListener
import com.poojan29.passcode.passcode.util.PasscodeManager
import com.poojan29.passcode.passcode.viewmodel.PasscodeViewModel

class MainActivity : ComponentActivity(), PasscodeListener {

    private lateinit var passcodeViewModel: PasscodeViewModel
    private lateinit var passcodeManager: PasscodeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        passcodeManager = PasscodeManager(this)
        passcodeViewModel = PasscodeViewModel(passcodeManager)

        setContent {
            ComposePasscodeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DraggablePasscode(dragViewModel = passcodeViewModel, this, passcodeManager)
                }
            }
        }
    }

    override fun onPasscodeReject() {
        // Show passcode reject message
    }

    override fun onPasscodeForgot() {
        // clear store passcode by calling `clearPasscode()` method
        // Implement your own flow
        // i.e send new passcode by email or message and verify it from user with email or message.
    }

    override fun onPassCodeReceive(passcode: String) {
        if (passcodeManager.getSavedDragPasscode() == passcode) {
            // Navigate to new screen
        } else {
            passcodeManager.isPasscodeWrong.value = true
        }
    }
}