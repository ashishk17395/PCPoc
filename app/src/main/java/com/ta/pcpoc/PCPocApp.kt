package com.ta.pcpoc

import android.app.Application
import android.content.Context
import com.ta.pcpoc.displayOverOTherApps.service.ServiceStarter
import com.ta.pcpoc.displayOverOTherApps.worker.WorkerStarter

class PCPocApp: Application() {


    override fun onCreate() {
        super.onCreate()
        ServiceStarter.startService(this)
        WorkerStarter.startServiceCheckerWorker()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //MultiDex.install(this)
    }
}