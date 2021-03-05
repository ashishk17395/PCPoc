package com.ta.pcpoc.displayOverOTherApps.ui

import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.ta.pcpoc.R
import com.ta.pcpoc.databinding.ViewPatternOverlayBinding
import com.ta.pcpoc.displayOverOTherApps.data.AppLockerPreferences
import com.ta.pcpoc.displayOverOTherApps.overlay.OverlayValidateType
import com.ta.pcpoc.displayOverOTherApps.view.OverlayViewState

class PatternOverlayView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var onPatternCompleted: ((List<PatternLockView.Dot>) -> Unit)? = null

    private var appLockerPreferences = AppLockerPreferences(context.applicationContext)

    val binding: ViewPatternOverlayBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_pattern_overlay, this, true)

    init {
        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                pattern?.let { onPatternCompleted?.invoke(it) }
            }

            override fun onCleared() {
            }

            override fun onStarted() {
            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateSelectedBackground()
        binding.patternLockView.clearPattern()
        binding.viewState = OverlayViewState()
    }

    fun observePattern(onPatternCompleted: (List<PatternLockView.Dot>) -> Unit) {
        this.onPatternCompleted = onPatternCompleted
    }

    fun notifyDrawnWrong() {
        binding.patternLockView.clearPattern()
        binding.viewState =
            OverlayViewState(
                overlayValidateType = OverlayValidateType.TYPE_PATTERN,
                isDrawnCorrect = false
            )
      /*  YoYo.with(Techniques.Shake)
            .duration(700)
            .playOn(binding.textViewPrompt)*/
    }

    fun notifyDrawnCorrect() {
        binding.patternLockView.clearPattern()
        binding.viewState =
            OverlayViewState(
                overlayValidateType = OverlayValidateType.TYPE_PATTERN,
                isDrawnCorrect = true
            )
    }

    fun setHiddenDrawingMode(isHiddenDrawingMode: Boolean) {
        binding.patternLockView.isInStealthMode = isHiddenDrawingMode
    }

    fun setAppPackageName(appPackageName: String) {
        try {
            val icon = context.packageManager.getApplicationIcon(appPackageName)
            binding.avatarLock.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.avatarLock.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_lock))
            e.printStackTrace()
        }
    }

    private fun updateSelectedBackground() {
        val selectedBackgroundId = appLockerPreferences.getSelectedBackgroundId()
        GradientBackgroundDataProvider.gradientViewStateList.forEach {
            if (it.id == selectedBackgroundId) {
                binding.layoutOverlayMain.background = it.getGradiendDrawable(context)
            }
        }
    }
}