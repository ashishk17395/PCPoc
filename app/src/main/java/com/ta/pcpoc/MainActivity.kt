package com.ta.pcpoc

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity


class MainActivity : AppCompatActivity() {

//    devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//    demoDeviceAdmin = new ComponentName(this, DemoDeviceAdminReceiver.class);

    private lateinit var  devicePolicyManager :DevicePolicyManager
    private lateinit var demoDeviceAdmin:ComponentName

    companion object {
        const val PER_REQ_CODE_DEVICE_ADMIN = 20
        const val PER_REQ_CODE_APP_USAGE_ACCESS = 21
        const val PER_REQ_CODE_LOCATION = 22
        const val PER_REQ_CODE_DISPLAY_OVER_OTHER_APPS = 23
        const val PER_REQ_CODE_ACCESSIBILITY = 24
        const val PER_REQ_CODE_PHONE = 25
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(android.content.Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        demoDeviceAdmin =  ComponentName(this, AppAdminReceiver::class.java)

        initializeView()
    }

    private fun initializeView() {

        if(devicePolicyManager.isAdminActive(demoDeviceAdmin))
            findViewById<TextView>(R.id.deviceAdmin).setTextColor(Color.GREEN)

    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }

    fun onDeviceAdminClick(v: View) {

        if(devicePolicyManager.isAdminActive(demoDeviceAdmin))
        {
            devicePolicyManager.setCameraDisabled(demoDeviceAdmin, true)
        }
        else {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This is extra explanation")
            startActivityForResult(intent, PER_REQ_CODE_DEVICE_ADMIN)
        }


    }

    fun onAppUsageClick(v: View) {

    }

    fun onLocationClick(v: View) {

    }

    fun onDisplayOverOtherClick(v: View) {

    }

    fun onAccessibility(v: View) {

    }

    fun onPhoneClick(v: View) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PER_REQ_CODE_DEVICE_ADMIN -> {
                if (resultCode == Activity.RESULT_OK) {
                   findViewById<TextView>(R.id.deviceAdmin).setTextColor(Color.GREEN)
                } else {
                    findViewById<TextView>(R.id.deviceAdmin).setTextColor(Color.RED)
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}