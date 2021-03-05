package com.ta.pcpoc.displayOverOTherApps.observable

import android.content.Context
import com.ta.pcpoc.displayOverOTherApps.permissions.PermissionChecker
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class PermissionCheckerObservable(val context: Context) {

    fun get(): Flowable<Boolean> {
        return Flowable.interval(30, TimeUnit.MINUTES)
            .map { PermissionChecker.checkUsageAccessPermission(context).not() }
    }
}