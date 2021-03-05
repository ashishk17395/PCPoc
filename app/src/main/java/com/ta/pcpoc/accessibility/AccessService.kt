package com.ta.pcpoc.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.ta.pcpoc.MainActivity
import com.ta.pcpoc.PCPocApp

class AccessService : AccessibilityService() {

    var isStringPresentOnScreen = false

    companion object {
        var isMainActivityRunning = false
        fun isAccessServiceEnabled(context: Context): Boolean {
            val prefString =
                    Settings.Secure.getString(
                            context.contentResolver,
                            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    ) ?: ""
            return prefString.contains("${context.packageName}/${AccessService::class.java.name}")
        }
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {

            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                //On tap of "This is accessibility service label"
                //D/AccSer:Click::: androidx.core.view.accessibility.AccessibilityNodeInfoCompat@80006d38; boundsInParent: Rect(0, 0 - 1080, 1920); boundsInScreen: Rect(0, 0 - 1080, 1920); packageName: com.android.settings; className: android.widget.FrameLayout; text: null; contentDescription: null; viewId: null; checkable: false; checked: false; focusable: false; focused: false; selected: false; clickable: false; longClickable: false; enabled: true; password: false; scrollable: false; [ACTION_SELECT, ACTION_CLEAR_SELECTION, ACTION_ACCESSIBILITY_FOCUS, ACTION_SHOW_ON_SCREEN]

                Log.d("AccSer:else::", AccessibilityNodeInfoCompat.wrap(rootInActiveWindow).toString())


            }
            else -> {
                if (event.source != null)
                    Log.d("AccSer:else::", AccessibilityNodeInfoCompat.wrap(event.source).toString())

                if (rootInActiveWindow != null && rootInActiveWindow.packageName == "com.android.settings") {
                    if (rootInActiveWindow.childCount > 0) {
                        if (rootInActiveWindow.getChild(0)?.className == "android.widget.TextView" && rootInActiveWindow.getChild(0)?.text?.contains("Device admin app") == true) {
                            parseHierarchy(rootInActiveWindow, "Label for the device admin")
                            if (isStringPresentOnScreen) {
                                Log.d("AccSer:Click::", "user is on device aadmin screen ")
                                Toast.makeText(
                                        applicationContext,
                                        "user is on device aadmin screen",
                                        Toast.LENGTH_LONG
                                ).show()
                                openApp()
                                isStringPresentOnScreen = false
                            }


                        }

                    }
                }

                //package should be settings and accessibility's name should be in toolbar.
                if (rootInActiveWindow != null && rootInActiveWindow.packageName == "com.android.settings") {
                    if (rootInActiveWindow.childCount > 0) {
                        for (i in 0 until rootInActiveWindow.childCount) {
                            if (rootInActiveWindow?.getChild(i)?.className == "android.widget.TextView"
                                    && rootInActiveWindow?.getChild(i)?.text?.contains("This is accessibility service label") == true
                            ) {
                                Log.d("AccSer:Click::", "user is on access screen ")
                                Toast.makeText(
                                        applicationContext,
                                        "user is on access screen",
                                        Toast.LENGTH_LONG
                                ).show()
                                openApp()
                            }
                        }
                    }
                }

            }
        }
    }

    fun parseHierarchy(v: AccessibilityNodeInfo, str: String) {
        if (v.childCount > 0) {
            for (i in 0 until v.childCount) {
                parseHierarchy(v.getChild(i), str)
            }
        } else {
            if (v.className == "android.widget.TextView" && v.text == str)
                isStringPresentOnScreen = true
        }

    }

    fun openApp() {

        if(isMainActivityRunning==false){
            var intent = Intent(applicationContext, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }
    }

}