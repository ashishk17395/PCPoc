package com.ta.pcpoc.displayOverOTherApps.data

import android.content.Context

class AppLockerPreferences(context: Context) {

    private val sharedPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun setHiddenDrawingMode(hiddenDrawingPatternMode: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(KEY_IS_PATTERN_HIDDEN, hiddenDrawingPatternMode)
            apply()
        }
    }

    fun getHiddenDrawingMode(): Boolean = sharedPref.getBoolean(KEY_IS_PATTERN_HIDDEN, false)

    fun setFingerPrintEnable(fingerPrintEnabled: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(KEY_IS_FINGERPRINT_ENABLE, fingerPrintEnabled)
            apply()
        }
    }

    fun setSelectedBackgroundId(backgroundId: Int) {
        with(sharedPref.edit()) {
            putInt(KEY_BACKGROUND_ID, backgroundId)
            apply()
        }
    }

    fun getSelectedBackgroundId(): Int {
        return sharedPref.getInt(KEY_BACKGROUND_ID, 0)
    }

    fun acceptPrivacyPolicy(){
        with(sharedPref.edit()) {
            putBoolean(KEY_ACCEPT_PRIVACY_POLICY, true)
            apply()
        }
    }

    fun isPrivacyPolicyAccepted(): Boolean{
        return sharedPref.getBoolean(KEY_ACCEPT_PRIVACY_POLICY, false)
    }

    companion object {

        private const val PREFERENCES_NAME = "AppLockerPreferences"

        private const val KEY_IS_PATTERN_HIDDEN = "KEY_IS_PATTERN_HIDDEN"

        private const val KEY_IS_FINGERPRINT_ENABLE = "KEY_IS_FINGERPRINT_ENABLE"

        private const val KEY_ACCEPT_PRIVACY_POLICY = "KEY_ACCEPT_PRIVACY_POLICY"

        private const val KEY_BACKGROUND_ID = "KEY_BACKGROUND_ID"
    }
}