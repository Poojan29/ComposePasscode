package com.poojan29.passcode.passcode.util

import android.content.Context

class PasscodeManager(context: Context) {
    private val sharedPreference = context.getSharedPreferences(
        "Passcode",
        Context.MODE_PRIVATE
    )

    var isPasscodeWrong: Boolean = false

    var hasPasscode: Boolean
        get() = sharedPreference.getBoolean("hasPasscode", false)
        set(value) = sharedPreference.edit().putBoolean("hasPasscode", value).apply()

    var hasDragPasscode: Boolean
        get() = sharedPreference.getBoolean("hasDragPasscode", false)
        set(value) = sharedPreference.edit().putBoolean("hasDragPasscode", value).apply()

    fun savePasscode(passcode: String) {
        sharedPreference.edit().putString("passcode", passcode).apply()
        hasPasscode = true
    }

    fun saveDragPasscode(passcode: String) {
        sharedPreference.edit().putString("drag_passcode", passcode).apply()
        hasDragPasscode = true
    }

    fun getSavedPasscode(): String {
        return sharedPreference.getString("passcode", "").toString()
    }

    fun getSavedDragPasscode(): String {
        return sharedPreference.getString("drag_passcode", "").toString()
    }

    fun clearPasscode() {
        sharedPreference.edit().clear().apply()
        hasPasscode = false
        hasDragPasscode = false
    }
}