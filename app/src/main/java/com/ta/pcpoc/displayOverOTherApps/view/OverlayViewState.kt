package com.ta.pcpoc.displayOverOTherApps.view

import android.content.Context
import android.view.View
import com.ta.pcpoc.R
import com.ta.pcpoc.displayOverOTherApps.overlay.OverlayValidateType

class OverlayViewState(
    val overlayValidateType: OverlayValidateType? = null,
    val isDrawnCorrect: Boolean? = null,
    val isHiddenDrawingMode: Boolean = false,
    val isFingerPrintMode: Boolean = false,
    val isIntrudersCatcherMode: Boolean = false
) {

    fun getPromptMessage(context: Context): String {
        return when (overlayValidateType) {
            OverlayValidateType.TYPE_PATTERN -> {
                when (isDrawnCorrect) {
                    true -> context.getString(R.string.overlay_prompt_pattern_title_correct)
                    false -> context.getString(R.string.overlay_prompt_pattern_title_wrong)
                    null -> context.getString(R.string.overlay_prompt_pattern_title)
                }
            }
            else -> context.getString(R.string.overlay_prompt_pattern_title)
        }
    }

    fun getFingerPrintIconVisibility(): Int = if (isFingerPrintMode) View.VISIBLE else View.INVISIBLE

}