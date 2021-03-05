package com.ta.pcpoc.displayOverOTherApps.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            context?.let { ServiceStarter.startService(context) }
        }
    }
}