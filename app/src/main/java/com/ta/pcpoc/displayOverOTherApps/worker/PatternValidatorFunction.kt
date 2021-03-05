package com.ta.pcpoc.displayOverOTherApps.worker

import com.ta.pcpoc.displayOverOTherApps.data.PatternDot
import com.ta.pcpoc.displayOverOTherApps.util.PatternChecker
import io.reactivex.functions.BiFunction

class PatternValidatorFunction : BiFunction<List<PatternDot>, List<PatternDot>, Boolean> {
    override fun apply(t1: List<PatternDot>, t2: List<PatternDot>): Boolean {
        return PatternChecker.checkPatternsEqual(t1, t2)
    }
}