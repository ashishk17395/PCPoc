package com.ta.pcpoc

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class AppAdminReceiver :DeviceAdminReceiver() {


    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }
}