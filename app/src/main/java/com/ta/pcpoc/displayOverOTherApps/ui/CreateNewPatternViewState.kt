package com.ta.pcpoc.displayOverOTherApps.ui

import android.content.Context
import com.ta.pcpoc.R

data class CreateNewPatternViewState(val patternEvent: CreateNewPatternViewModel.PatternEvent) {

    fun getPromptText(context: Context): String {
        val s = when (patternEvent) {
            CreateNewPatternViewModel.PatternEvent.INITIALIZE -> context.getString(R.string.draw_pattern_title)
            CreateNewPatternViewModel.PatternEvent.FIRST_COMPLETED -> context.getString(R.string.redraw_pattern_title)
            CreateNewPatternViewModel.PatternEvent.SECOND_COMPLETED -> context.getString(R.string.create_pattern_successful)
            CreateNewPatternViewModel.PatternEvent.ERROR -> context.getString(R.string.recreate_pattern_error)
        }
        return s
    }

    fun isCreatedNewPattern(): Boolean = patternEvent == CreateNewPatternViewModel.PatternEvent.SECOND_COMPLETED
}