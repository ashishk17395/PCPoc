package com.ta.pcpoc.displayOverOTherApps

import com.ta.pcpoc.PCPocApp
import com.ta.pcpoc.displayOverOTherApps.data.AppLockerPreferences
import com.ta.pcpoc.displayOverOTherApps.ui.RxAwareViewModel

class PrivacyPolicyViewModel () : RxAwareViewModel() {

    fun acceptPrivacyPolicy() {
        val appLockerPreferences= AppLockerPreferences(PCPocApp.sContext)
        appLockerPreferences.acceptPrivacyPolicy()
    }

}