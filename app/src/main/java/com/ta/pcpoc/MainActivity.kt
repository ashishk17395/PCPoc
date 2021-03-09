package com.ta.pcpoc

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ta.pcpoc.accessibility.AccessService
import com.ta.pcpoc.deviceAdmin.AppAdminReceiver
import com.ta.pcpoc.kbrd.PCpocKeyboard


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

        findViewById<TextView>(R.id.deviceAdmin).setOnClickListener { onDeviceAdminClick() }
        findViewById<TextView>(R.id.accessibility).setOnClickListener { onAccessibility() }
        findViewById<TextView>(R.id.myKeyboard).setOnClickListener { onEnableKeyboard() }
        initializeView()
    }

    private fun initializeView() {

        if(devicePolicyManager.isAdminActive(demoDeviceAdmin))
            findViewById<TextView>(R.id.deviceAdmin).setTextColor(Color.GREEN)
        if(AccessService.isAccessServiceEnabled(this))
            findViewById<TextView>(R.id.accessibility).setTextColor(Color.GREEN)
        if(PCpocKeyboard.isCodeBoardEnabled(this))
            findViewById<TextView>(R.id.myKeyboard).setTextColor(Color.GREEN)
        //Make your text green if permission etc.. is given.
        //Open activity etc on second click

    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }

    fun onDeviceAdminClick() {

        if(devicePolicyManager.isAdminActive(demoDeviceAdmin))
        {
            Toast.makeText(this, "Already admin", Toast.LENGTH_LONG).show()
        }
        else {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This is extra explanation")
            startActivityForResult(intent, PER_REQ_CODE_DEVICE_ADMIN)
        }


    }

    fun onAppUsageClick(v: View) {
        val intent : Intent = Intent(MainActivity@ this, AppUsage::class.java)
        startActivity(intent)
    }

    fun onLocationClick(v: View) {

    }

    fun onDisplayOverOtherClick(v: View) {

    }

    fun onAccessibility() {
        if(AccessService.isAccessServiceEnabled(this))
        {
            Toast.makeText(this, "Service Disabled", Toast.LENGTH_LONG).show()
            val intent  = Intent(this, AccessService::class.java)
            intent.putExtra(AccessService.KEY_STOP_ACCESS_SERVICE, true)
            startService(intent)
        }
        else{
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivityForResult(intent, PER_REQ_CODE_ACCESSIBILITY)
        }

    }

    fun onPhoneClick(v: View) {

    }

    fun onEnableKeyboard(){
        if(PCpocKeyboard.isCodeBoardEnabled(this)){
            if(PCpocKeyboard.isUserUsingThisKeyboard(this))
                Toast.makeText(this, "Keyboard Already enabled", Toast.LENGTH_LONG).show()
            else
            {
                val imeManager =
                    applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imeManager.showInputMethodPicker()
            }
        }
        else
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
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
            PER_REQ_CODE_ACCESSIBILITY -> {
                Log.d("Access Response:", resultCode.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        AccessService.isMainActivityRunning= true
        super.onResume()
    }

    override fun onPause() {
        AccessService.isMainActivityRunning = false
            super.onPause()
    }


}