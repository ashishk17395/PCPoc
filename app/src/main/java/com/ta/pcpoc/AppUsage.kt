package com.ta.pcpoc

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process.myUid
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class AppUsage : AppCompatActivity() {

    lateinit var tvUsageStats: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)

        val intent: Intent = getIntent()
        tvUsageStats = findViewById(R.id.tvUsageStats)
        if (checkUsageStatsPermission()) {
            showUsageStats()
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

    }

    private fun showUsageStats() {
        var usageStatsManager: UsageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)

        var queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis,
            System.currentTimeMillis()
        )
        var stats_data: String = ""
        for (i in 0..queryUsageStats.size - 1) {
            stats_data =
                stats_data + "Package Name : " + queryUsageStats.get(i).packageName + "\n" + "Last Time Used : " +
                        convertTime(queryUsageStats.get(i).lastTimeUsed) + '\n' + "Describe Contents : " +
                        queryUsageStats.get(i).describeContents() + "\n" +
                        "First Time Stamp : " +
                        convertTime(queryUsageStats.get(i).firstTimeStamp) + '\n' + "Last Time Stamp : " +
                        convertTime(queryUsageStats.get(i).lastTimeStamp) + '\n' + "Total time in foreground : " +
                        convertTime2(queryUsageStats.get(i).totalTimeInForeground) + '\n'
        }
        tvUsageStats.setText(stats_data)
        Log.d("data", "showUsageStats: " + stats_data)
    }

    private fun convertTime2(totalTimeInForeground: Long): Any? {
        var date: Date = Date(totalTimeInForeground)
        var format: SimpleDateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        return format.format(date)
    }

    private fun convertTime(lastTimeUsed: Long): String {
        var date: Date = Date(lastTimeUsed)
        var format: SimpleDateFormat = SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.ENGLISH)
        return format.format(date)
    }

    private fun checkUsageStatsPermission(): Boolean {
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOpsManager.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED

    }


}