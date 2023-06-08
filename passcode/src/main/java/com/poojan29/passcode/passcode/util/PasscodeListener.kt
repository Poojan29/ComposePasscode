package com.poojan29.passcode.passcode.util

interface PasscodeListener {
    fun onPassCodeReceive(passcode: String)
    fun onPasscodeReject()
}