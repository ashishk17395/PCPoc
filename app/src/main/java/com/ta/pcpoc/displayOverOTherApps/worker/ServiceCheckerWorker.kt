package com.ta.pcpoc.displayOverOTherApps.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ta.pcpoc.displayOverOTherApps.service.ServiceStarter

class ServiceCheckerWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        ServiceStarter.startService(applicationContext)
        return Result.success()
    }
}