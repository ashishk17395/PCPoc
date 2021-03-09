package com.ta.pcpoc.displayOverOTherApps.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ta.pcpoc.PCPocApp
import com.ta.pcpoc.displayOverOTherApps.AppDatabase
import com.ta.pcpoc.displayOverOTherApps.data.AppLockerPreferences
import com.ta.pcpoc.displayOverOTherApps.data.PatternDao
import com.ta.pcpoc.displayOverOTherApps.util.extensions.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DisplayOverOtherAppsViewModel(
) : ViewModel() {

    private val patternCreationNeedLiveData = MutableLiveData<Boolean>()

    private var appLaunchValidated = false
    private val patternDao= AppDatabase.getAppDataBase(PCPocApp.sContext)?.patternDao()
    val userPreferencesRepository= UserPreferencesRepository(AppLockerPreferences(PCPocApp.sContext))

    init {
        var disposables = CompositeDisposable()
        disposables += patternDao?.isPatternCreated()
            ?.toObservable()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { patternCreationNeedLiveData.value = it == 0 }!!

    }

    fun getPatternCreationNeedLiveData(): LiveData<Boolean> = patternCreationNeedLiveData

    fun onAppLaunchValidated() {
        appLaunchValidated = true
    }

    fun isPrivacyPolicyAccepted() = userPreferencesRepository.isPrivacyPolicyAccepted()

    fun isAppLaunchValidated(): Boolean = appLaunchValidated

    override fun onCleared() {
        super.onCleared()
        appLaunchValidated = false
        userPreferencesRepository.endSession()
    }

}