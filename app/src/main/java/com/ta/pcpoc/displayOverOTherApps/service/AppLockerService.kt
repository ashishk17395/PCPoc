package com.ta.pcpoc.displayOverOTherApps.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import com.andrognito.patternlockview.PatternLockView
import com.ta.pcpoc.displayOverOTherApps.AppDatabase
import com.ta.pcpoc.displayOverOTherApps.data.AppLockerPreferences
import com.ta.pcpoc.displayOverOTherApps.data.PatternDao
import com.ta.pcpoc.displayOverOTherApps.data.PatternDot
import com.ta.pcpoc.displayOverOTherApps.data.SystemPackages
import com.ta.pcpoc.displayOverOTherApps.notification.ServiceNotificationManager
import com.ta.pcpoc.displayOverOTherApps.observable.AppForegroundObservable
import com.ta.pcpoc.displayOverOTherApps.observable.PermissionCheckerObservable
import com.ta.pcpoc.displayOverOTherApps.overlay.OverlayViewLayoutParams
import com.ta.pcpoc.displayOverOTherApps.permissions.PermissionChecker
import com.ta.pcpoc.displayOverOTherApps.ui.OverlayValidationActivity
import com.ta.pcpoc.displayOverOTherApps.ui.PatternOverlayView
import com.ta.pcpoc.displayOverOTherApps.util.extensions.convertToPatternDot
import com.ta.pcpoc.displayOverOTherApps.util.extensions.plusAssign
import com.ta.pcpoc.displayOverOTherApps.worker.PatternValidatorFunction
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlin.collections.HashSet

class AppLockerService : Service() {

    private lateinit var serviceNotificationManager : ServiceNotificationManager

    lateinit var appForegroundObservable: AppForegroundObservable

    lateinit var permissionCheckerObservable: PermissionCheckerObservable

    //lateinit var lockedAppsDao : LockAppDao

    //lateinit var patternDao: PatternDao

    lateinit var appLockerPreferences: AppLockerPreferences

    private val validatedPatternObservable = PublishSubject.create<List<PatternDot>>()

    private val allDisposables: CompositeDisposable = CompositeDisposable()

   private var foregroundAppDisposable: Disposable? = null

    private val lockedAppPackageSet: HashSet<String> = HashSet()

    private lateinit var windowManager: WindowManager

    private lateinit var overlayParams: WindowManager.LayoutParams

    private lateinit var overlayView: PatternOverlayView

    private var isOverlayShowing = false

    private var lastForegroundAppPackage: String? = null

    private var screenOnOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> observeForegroundApplication()
                Intent.ACTION_SCREEN_OFF -> stopForegroundApplicationObserver()
            }
        }
    }

    private var installUninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
        }
    }

    init {
        SystemPackages.getSystemPackages().forEach { lockedAppPackageSet.add(it) }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        initializeAppLockerNotification()

        initializeOverlayView()

        registerScreenReceiver()

        registerInstallUninstallReceiver()

        observeLockedApps()

        observeOverlayView()

        observeForegroundApplication()

        observePermissionChecker()
    }

    override fun onDestroy() {
        ServiceStarter.startService(applicationContext)
        unregisterScreenReceiver()
        unregisterInstallUninstallReceiver()
        if (allDisposables.isDisposed.not()) {
            allDisposables.dispose()
        }
        super.onDestroy()
    }

    private fun registerInstallUninstallReceiver() {
        var installUninstallFilter = IntentFilter()
            .apply {
                addAction(Intent.ACTION_PACKAGE_INSTALL)
                addDataScheme("package")
            }

        registerReceiver(installUninstallReceiver, installUninstallFilter)
    }

    private fun unregisterInstallUninstallReceiver() {
        unregisterReceiver(installUninstallReceiver)
    }

    private fun registerScreenReceiver() {
        val screenFilter = IntentFilter()
        screenFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOnOffReceiver, screenFilter)
    }

    private fun unregisterScreenReceiver() {
        unregisterReceiver(screenOnOffReceiver)
    }

    private fun observeLockedApps() {
            allDisposables += AppDatabase.getAppDataBase(applicationContext)?.lockAppDao()?.getLockedApps()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { lockedAppList ->
                        lockedAppPackageSet.clear()
                        lockedAppList.forEach { lockedAppPackageSet.add(it.appName) }
                        SystemPackages.getSystemPackages().forEach { lockedAppPackageSet.add(it) }
                    },
                    { error -> Log.d("exception",error.localizedMessage)})!!

    }

    private fun observeOverlayView() {
        allDisposables += Flowable
            .combineLatest(
                AppDatabase.getAppDataBase(applicationContext)?.patternDao()?.getPattern()?.map { it.patternMetadata.pattern },
                validatedPatternObservable.toFlowable(BackpressureStrategy.BUFFER),
                PatternValidatorFunction()
            )
            .subscribe(this@AppLockerService::onPatternValidated)
    }

    private fun initializeOverlayView() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayParams = OverlayViewLayoutParams.get()
        overlayView = PatternOverlayView(
            applicationContext
        ).apply {
            observePattern(this@AppLockerService::onDrawPattern)
        }
    }

    private fun observeForegroundApplication() {
        appForegroundObservable = AppForegroundObservable(applicationContext)
        if (foregroundAppDisposable != null && foregroundAppDisposable?.isDisposed?.not() == true) {
            return
        }

        foregroundAppDisposable = appForegroundObservable
            .get()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { foregroundAppPackage -> onAppForeground(foregroundAppPackage) },
                { error -> Log.d("exception",error.localizedMessage) })
        allDisposables.add(foregroundAppDisposable!!)
    }

    private fun stopForegroundApplicationObserver() {
        if (foregroundAppDisposable != null && foregroundAppDisposable?.isDisposed?.not() == true) {
            foregroundAppDisposable?.dispose()
        }
    }

    private fun observePermissionChecker() {
        permissionCheckerObservable = PermissionCheckerObservable(applicationContext)
        allDisposables += permissionCheckerObservable
            .get()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isPermissionNeed ->
                if (isPermissionNeed) {
                    showPermissionNeedNotification()
                } else {
                    serviceNotificationManager.hidePermissionNotification()
                }
            }
    }

    private fun onAppForeground(foregroundAppPackage: String) {
        hideOverlay()
        if (lockedAppPackageSet.contains(foregroundAppPackage)) {
            if (PermissionChecker.checkOverlayPermission(
                    applicationContext
                ).not()
            ) {
                val intent =
                    OverlayValidationActivity.newIntent(applicationContext, foregroundAppPackage)
                if (lastForegroundAppPackage == applicationContext.packageName) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                } else {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                startActivity(intent)
            } else {
                showOverlay(foregroundAppPackage)
            }
        }
        lastForegroundAppPackage = foregroundAppPackage
    }

    private fun onDrawPattern(pattern: List<PatternLockView.Dot>) {
        validatedPatternObservable.onNext(pattern.convertToPatternDot())
    }

    private fun onPatternValidated(isDrawedPatternCorrect: Boolean) {
        if (isDrawedPatternCorrect) {
            overlayView.notifyDrawnCorrect()
            hideOverlay()
        } else {
            overlayView.notifyDrawnWrong()
        }
    }

    private fun initializeAppLockerNotification() {
        serviceNotificationManager = ServiceNotificationManager(applicationContext)
        val notification = serviceNotificationManager.createNotification()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
    }

    private fun showPermissionNeedNotification() {
        val notification = serviceNotificationManager.createPermissionNeedNotification()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED, notification)
    }

    private fun showOverlay(lockedAppPackageName: String) {
        if (isOverlayShowing.not()) {
            isOverlayShowing = true
            overlayView.setHiddenDrawingMode(appLockerPreferences.getHiddenDrawingMode())
            overlayView.setAppPackageName(lockedAppPackageName)
            windowManager.addView(overlayView, overlayParams)
        }
    }

    private fun hideOverlay() {
        if (isOverlayShowing) {
            isOverlayShowing = false
            windowManager.removeViewImmediate(overlayView)
        }
    }

    companion object {
        private const val NOTIFICATION_ID_APPLOCKER_SERVICE = 1
        private const val NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED = 2
    }
}