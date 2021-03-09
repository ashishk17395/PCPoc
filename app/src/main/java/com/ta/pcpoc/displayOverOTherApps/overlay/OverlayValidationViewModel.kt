package com.ta.pcpoc.displayOverOTherApps.overlay

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ta.pcpoc.PCPocApp
import com.ta.pcpoc.displayOverOTherApps.AppDatabase
import com.ta.pcpoc.displayOverOTherApps.data.AppLockerPreferences
import com.ta.pcpoc.displayOverOTherApps.data.PatternDao
import com.ta.pcpoc.displayOverOTherApps.data.PatternDot
import com.ta.pcpoc.displayOverOTherApps.ui.GradientBackgroundDataProvider
import com.ta.pcpoc.displayOverOTherApps.ui.GradientItemViewState
import com.ta.pcpoc.displayOverOTherApps.util.RxAwareAndroidViewModel
import com.ta.pcpoc.displayOverOTherApps.util.extensions.plusAssign
import com.ta.pcpoc.displayOverOTherApps.view.OverlayViewState
import com.ta.pcpoc.displayOverOTherApps.worker.PatternValidatorFunction
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

class OverlayValidationViewModel(
) : ViewModel() {

    private val patternValidationViewStateLiveData = MediatorLiveData<OverlayViewState>()
        .apply {
                appLockerPreferences = AppLockerPreferences(PCPocApp.sContext)

            this.value = OverlayViewState(
                isHiddenDrawingMode = appLockerPreferences.getHiddenDrawingMode()
            )
        }

    private val selectedBackgroundDrawableLiveData = MutableLiveData<GradientItemViewState>()

    private val patternDrawnSubject = PublishSubject.create<List<PatternDot>>()

    private val patternDao= AppDatabase.getAppDataBase(PCPocApp.sContext)?.patternDao()
    private var appLockerPreferences= AppLockerPreferences(PCPocApp.sContext)

    init {
        val existingPatternObservable = patternDao?.getPattern()?.map { it.patternMetadata.pattern }
        var disposables = CompositeDisposable()
        disposables += Flowable
            .combineLatest(
                existingPatternObservable,
                patternDrawnSubject.toFlowable(BackpressureStrategy.BUFFER),
                PatternValidatorFunction()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isValidated ->
                patternValidationViewStateLiveData.value =
                    OverlayViewState(
                        overlayValidateType = OverlayValidateType.TYPE_PATTERN,
                        isDrawnCorrect = isValidated,
                        isHiddenDrawingMode = appLockerPreferences.getHiddenDrawingMode()
                    )
            }

        /*patternValidationViewStateLiveData.addSource(fingerPrintLiveData) {
            patternValidationViewStateLiveData.value = OverlayViewState(
                overlayValidateType = OverlayValidateType.TYPE_FINGERPRINT,
                fingerPrintResultData = it,
                isHiddenDrawingMode = appLockerPreferences.getHiddenDrawingMode(),
                isIntrudersCatcherMode = appLockerPreferences.getIntrudersCatcherEnabled(),
                isFingerPrintMode = appLockerPreferences.getFingerPrintEnabled()
            )
        }*/

        val selectedBackgroundId = appLockerPreferences.getSelectedBackgroundId()
        GradientBackgroundDataProvider.gradientViewStateList.forEach {
            if (it.id == selectedBackgroundId) {
                selectedBackgroundDrawableLiveData.value = it
            }
        }
    }

    fun getViewStateObservable(): LiveData<OverlayViewState> = patternValidationViewStateLiveData

    fun getBackgroundDrawableLiveData(): LiveData<GradientItemViewState> =
        selectedBackgroundDrawableLiveData

    fun onPatternDrawn(pattern: List<PatternDot>) {
        patternDrawnSubject.onNext(pattern)
    }

}