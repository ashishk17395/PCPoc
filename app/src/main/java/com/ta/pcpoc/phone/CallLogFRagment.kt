package com.ta.pcpoc.phone

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.ta.pcpoc.R
import kotlinx.android.synthetic.main.fragment_call_log.*
import kotlinx.coroutines.*
import java.lang.Long
import java.util.*

class CallLogFRagment: Fragment() {

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(activity!!, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CALL_LOG),
                111
            )
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            showCallLogs()
        }
        return inflater.inflate(R.layout.fragment_call_log,container,false)
    }

    private fun showCallLogs(){
        uiScope.launch(Dispatchers.IO) {
            val sb = StringBuffer()
            val managedCursor: Cursor =
                activity!!.managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null)
            val number: Int = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
            val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
            val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            sb.append("Call Details :")
            while (managedCursor.moveToNext()) {
                val phNumber: String = managedCursor.getString(number) // mobile number
                val callType: String = managedCursor.getString(type) // call type
                val callDate: String = managedCursor.getString(date) // call date
                val callDayTime = Date(Long.valueOf(callDate))
                val callDuration: String = managedCursor.getString(duration)
                var dir: String? = null
                val dircode = callType.toInt()
                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                }
                sb.append("\nPhone Number:--- $phNumber \nCall Type:--- $dir \nCall Date:--- $callDayTime \nCall duration in sec :--- $callDuration")
                sb.append("\n----------------------------------")
            }
            managedCursor.close()
            withContext(Dispatchers.Main) {
                callTv.text = sb
                Log.e("Agil value --- ", sb.toString())
            }
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
                showCallLogs()
            } else {
                Toast.makeText(activity!!, "Until you grant the permission, we canot display the names", Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}