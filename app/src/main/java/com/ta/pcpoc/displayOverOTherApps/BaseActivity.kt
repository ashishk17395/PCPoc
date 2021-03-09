package com.ta.pcpoc.displayOverOTherApps

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ta.pcpoc.displayOverOTherApps.ui.DisplayOverOtherAppsViewModel

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    private val app = Application()
    var viewModelFactory = ViewModelProvider.NewInstanceFactory()


    lateinit var viewModel: VM

    abstract fun getViewModel(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(getViewModel())
    }
}