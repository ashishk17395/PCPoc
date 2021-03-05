package com.ta.pcpoc.displayOverOTherApps.util.extensions

import com.andrognito.patternlockview.PatternLockView
import com.ta.pcpoc.displayOverOTherApps.data.PatternDot

fun List<PatternLockView.Dot>.convertToPatternDot(): List<PatternDot> {
    val patternDotList: ArrayList<PatternDot> = arrayListOf()
    forEach {
        patternDotList.add(PatternDot(column = it.column, row = it.row))
    }
    return patternDotList
}