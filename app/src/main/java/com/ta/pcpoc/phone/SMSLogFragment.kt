package com.ta.pcpoc.phone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ta.pcpoc.MainActivity
import com.ta.pcpoc.R
import kotlinx.android.synthetic.main.fragment_sms_log.*

class SMSLogFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_SMS),
                111
            )
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            showSMSLogs()
        }
        return inflater.inflate(R.layout.fragment_sms_log,container,false)
    }

    private fun showSMSLogs(){
        val cursor: Cursor? = activity?.contentResolver?.query(Uri.parse("content://sms/inbox"), null, null, null, null)

        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                for (idx in 0 until cursor.getColumnCount()) {
                    msgData += " " + cursor.getColumnName(idx).toString() + ":" + cursor.getString(
                        idx
                    )
                }
                smsLog.text = msgData
                // use msgData
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(activity!!,"Your inbox is empty",Toast.LENGTH_LONG).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showSMSLogs()
            } else {
                Toast.makeText(activity!!, "Until you grant the permission, we canot display the names", Toast.LENGTH_LONG).show();
            }
        }
    }
}