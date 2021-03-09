package com.ta.pcpoc.displayOverOTherApps

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Observable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ta.pcpoc.CreateNewPatternActivity
import com.ta.pcpoc.R
import com.ta.pcpoc.databinding.ActivityDisplayOverOtherAppsBinding
import com.ta.pcpoc.displayOverOTherApps.permissions.PermissionChecker
import com.ta.pcpoc.displayOverOTherApps.permissions.UsageAccessPermissionDialog
import com.ta.pcpoc.displayOverOTherApps.ui.DisplayOverOtherAppsViewModel
import com.ta.pcpoc.displayOverOTherApps.ui.OverlayValidationActivity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import kotlin.collections.ArrayList


class DisplayOverOtherAppsActivity : BaseActivity<DisplayOverOtherAppsViewModel>(), RecyclerItemClickInterface {
    private val TAG = "DisplayOverOtherApps"
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : AppLockListAdapter
    private var lockAppDao: LockAppDao? = null
    val appArrayList = ArrayList<LockAppEntity>()
    private lateinit var binding: ActivityDisplayOverOtherAppsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_over_other_apps)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val db = AppDatabase.getAppDataBase(this)
        lockAppDao = db?.lockAppDao()

        adapter = AppLockListAdapter(this@DisplayOverOtherAppsActivity,this@DisplayOverOtherAppsActivity)
        adapter.addAll(appArrayList)
        recyclerView.layoutManager = LinearLayoutManager(this@DisplayOverOtherAppsActivity)
        recyclerView.adapter = adapter

        GlobalScope.launch{ // launches coroutine in main thread
            val abc =  lockAppDao?.getAll()
            if(abc?.size==0)
            {
                appArrayList.addAll(getAllApps())
                for (i in getAllApps().indices) {
                    lockAppDao?.insertAll(getAllApps()[i])
                }
            } else {
                appArrayList.addAll(lockAppDao?.getAll() as ArrayList<LockAppEntity>)
            }
            withContext(Dispatchers.Main) {
                adapter.addAll(appArrayList)
            }

        }

        viewModel.getPatternCreationNeedLiveData().observe(this, Observer { isPatternCreateNeed ->
            when {
                isPatternCreateNeed -> {
                    startActivityForResult(
                        CreateNewPatternActivity.newIntent(this),
                        RC_CREATE_PATTERN
                    )
                }
                viewModel.isAppLaunchValidated().not() -> {
                    startActivityForResult(
                        OverlayValidationActivity.newIntent(this, this.packageName),
                        RC_VALIDATE_PATTERN
                    )
                }
            }
        })
    }

    fun getAllApps(): List<LockAppEntity> {
        val packageManager : PackageManager = packageManager
        val packages: List<ApplicationInfo> = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList= ArrayList<LockAppEntity>()

        for (i in packages.indices) {
            val lockAppEntity = LockAppEntity()
            lockAppEntity.appName = packageManager.getApplicationLabel(packages[i]).toString()
            lockAppEntity.appLock = false
            appList.add(lockAppEntity)
        }
        return appList
    }

    override fun onItemClick(position: Int) {
        if (!PermissionChecker.checkUsageAccessPermission(this)) {
            UsageAccessPermissionDialog.newInstance().show(supportFragmentManager, "")
        } else {
            GlobalScope.launch {
                val selectedApp = lockAppDao?.findByAppName(appArrayList[position].appName)
                selectedApp?.appLock = !(selectedApp?.appLock!!)
                lockAppDao?.updateAppInfo(selectedApp)
                val abc1 =  lockAppDao?.getAll()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_CREATE_PATTERN -> {
                viewModel.onAppLaunchValidated()
                showPrivacyPolicyIfNeeded()
                if (resultCode != Activity.RESULT_OK) {
                    finish()
                }
            }
            RC_VALIDATE_PATTERN -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.onAppLaunchValidated()
                    showPrivacyPolicyIfNeeded()
                } else {
                    finish()
                }
            }
        }
    }

    private fun showPrivacyPolicyIfNeeded() {
        if (viewModel.isPrivacyPolicyAccepted().not()) {
            PrivacyPolicyDialog.newInstance().show(supportFragmentManager, "")
        }
    }

    companion object {
        private const val RC_CREATE_PATTERN = 2002
        private const val RC_VALIDATE_PATTERN = 2003
    }

    override fun getViewModel(): Class<DisplayOverOtherAppsViewModel> = DisplayOverOtherAppsViewModel::class.java
}