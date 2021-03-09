package com.ta.pcpoc.displayOverOTherApps

import android.R.id
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ta.pcpoc.displayOverOTherApps.overlay.OverlayValidationViewModel
import com.ta.pcpoc.displayOverOTherApps.ui.DisplayOverOtherAppsViewModel


class Factory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass === DisplayOverOtherAppsViewModel::class.java) {
            return modelClass.getConstructor(DisplayOverOtherAppsViewModel::class.java).newInstance("abc") as T
        }
        return modelClass as T
        //throw IllegalArgumentException("Unable to construct viewmodel")
    }


}