package com.ta.pcpoc

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.ta.pcpoc.databinding.ActivityCreateNewPatternBinding
import com.ta.pcpoc.displayOverOTherApps.BaseActivity
import com.ta.pcpoc.displayOverOTherApps.ui.CreateNewPatternViewModel

class CreateNewPatternActivity : BaseActivity<CreateNewPatternViewModel>() {

    private lateinit var binding: ActivityCreateNewPatternBinding

    override fun getViewModel(): Class<CreateNewPatternViewModel> = CreateNewPatternViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_new_pattern)

        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                if (viewModel.isFirstPattern()) {
                    viewModel.setFirstDrawedPattern(pattern)
                } else {
                    viewModel.setRedrawnPattern(pattern)
                }
                binding.patternLockView.clearPattern()
            }

            override fun onCleared() {
            }

            override fun onStarted() {
            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }
        })

        viewModel.getPatternEventLiveData().observe(this, Observer { viewState ->
            binding.viewState = viewState
            binding.executePendingBindings()

            if (viewState.isCreatedNewPattern()) {
                onPatternCreateCompleted()
            }
        })
    }

    private fun onPatternCreateCompleted() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, CreateNewPatternActivity::class.java)
        }
    }
}