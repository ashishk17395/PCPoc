package com.ta.pcpoc.displayOverOTherApps.ui

import com.ta.pcpoc.displayOverOTherApps.data.AppLockerPreferences

class UserPreferencesRepository (val appLockerPreferences: AppLockerPreferences) {

    private var isRateUsAskedInThisSession = false


    fun isPrivacyPolicyAccepted(): Boolean {
        return appLockerPreferences.isPrivacyPolicyAccepted()
    }

    fun endSession(){
        isRateUsAskedInThisSession = false
    }
}