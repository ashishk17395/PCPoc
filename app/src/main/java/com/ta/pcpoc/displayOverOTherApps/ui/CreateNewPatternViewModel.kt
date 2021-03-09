package com.ta.pcpoc.displayOverOTherApps.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrognito.patternlockview.PatternLockView
import com.ta.pcpoc.PCPocApp
import com.ta.pcpoc.displayOverOTherApps.AppDatabase
import com.ta.pcpoc.displayOverOTherApps.data.PatternDao
import com.ta.pcpoc.displayOverOTherApps.data.PatternDotMetadata
import com.ta.pcpoc.displayOverOTherApps.data.PatternEntity
import com.ta.pcpoc.displayOverOTherApps.util.PatternChecker
import com.ta.pcpoc.displayOverOTherApps.util.extensions.convertToPatternDot
import com.ta.pcpoc.displayOverOTherApps.util.extensions.doOnBackground

class CreateNewPatternViewModel () : RxAwareViewModel() {

    enum class PatternEvent {
        INITIALIZE, FIRST_COMPLETED, SECOND_COMPLETED, ERROR
    }

    private val patternEventLiveData = MutableLiveData<CreateNewPatternViewState>().apply {
        value = CreateNewPatternViewState(PatternEvent.INITIALIZE)
    }

    private val patternDao= AppDatabase.getAppDataBase(PCPocApp.sContext)?.patternDao()

    private var firstDrawedPattern: ArrayList<PatternLockView.Dot> = arrayListOf()
    private var redrawedPattern: ArrayList<PatternLockView.Dot> = arrayListOf()

    fun getPatternEventLiveData(): LiveData<CreateNewPatternViewState> = patternEventLiveData

    fun setFirstDrawedPattern(pattern: List<PatternLockView.Dot>?) {
        pattern?.let {
            this.firstDrawedPattern.clear()
            this.firstDrawedPattern.addAll(pattern)
            patternEventLiveData.value = CreateNewPatternViewState(PatternEvent.FIRST_COMPLETED)
        }
    }

    fun setRedrawnPattern(pattern: List<PatternLockView.Dot>?) {
        pattern?.let {
            this.redrawedPattern.clear()
            this.redrawedPattern.addAll(pattern)
            if (PatternChecker.checkPatternsEqual(
                    firstDrawedPattern.convertToPatternDot(),
                    redrawedPattern.convertToPatternDot()
                )
            ) {
                saveNewCreatedPattern(firstDrawedPattern)
                patternEventLiveData.value = CreateNewPatternViewState(PatternEvent.SECOND_COMPLETED)
            } else {
                firstDrawedPattern.clear()
                redrawedPattern.clear()
                patternEventLiveData.value = CreateNewPatternViewState(PatternEvent.ERROR)
            }
        }
    }

    fun isFirstPattern(): Boolean = firstDrawedPattern.isEmpty()

    private fun saveNewCreatedPattern(pattern: List<PatternLockView.Dot>){
        doOnBackground {
            val patternMetadata = PatternDotMetadata(pattern.convertToPatternDot())
            val patternEntity = PatternEntity(patternMetadata)
            patternDao?.createPattern(patternEntity)
        }
    }
}