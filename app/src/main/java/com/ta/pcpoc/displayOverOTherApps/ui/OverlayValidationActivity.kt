package com.ta.pcpoc.displayOverOTherApps.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.ta.pcpoc.R
import com.ta.pcpoc.displayOverOTherApps.BaseActivity
import com.ta.pcpoc.displayOverOTherApps.overlay.OverlayValidationViewModel
import com.ta.pcpoc.displayOverOTherApps.util.extensions.convertToPatternDot
import com.ta.pcpoc.databinding.*

class OverlayValidationActivity : BaseActivity<OverlayValidationViewModel>() {


    private lateinit var binding: ActivityOverlayValidationBinding

    override fun getViewModel(): Class<OverlayValidationViewModel> =
        OverlayValidationViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_overlay_validation)

        intent.getStringExtra(KEY_PACKAGE_NAME)?.let { updateLaunchingAppIcon(it) }

        viewModel.getViewStateObservable().observe(this, Observer {
            binding.patternLockView.clearPattern()
            binding.viewState = it
            binding.executePendingBindings()

            if (it.isDrawnCorrect == true) {
                setResult(Activity.RESULT_OK)
                finish()
            }

            if (it.isDrawnCorrect == false) {
                /*YoYo.with(Techniques.Shake)
                    .duration(700)
                    .playOn(binding.textViewPrompt)*/
            }
        })

        viewModel.getBackgroundDrawableLiveData().observe(this, Observer {
            binding.layoutOverlayMain.background = it.getGradiendDrawable(this)
        })

        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                pattern?.let { viewModel.onPatternDrawn(it.convertToPatternDot()) }
            }

            override fun onCleared() {
            }

            override fun onStarted() {
            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.HOME")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateLaunchingAppIcon(appPackageName: String) {
        try {
            val icon = packageManager.getApplicationIcon(appPackageName)
            binding.avatarLock.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.avatarLock.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_lock
                )
            )
            e.printStackTrace()
        }
    }

    companion object {

        private const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"

        fun newIntent(context: Context, packageName: String): Intent {
            val intent = Intent(context, OverlayValidationActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            return intent
        }
    }

}