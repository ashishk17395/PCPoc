package com.ta.pcpoc.displayOverOTherApps.permissions

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ta.pcpoc.R
import kotlinx.android.synthetic.main.dialog_usage_permission.view.*


class UsageAccessPermissionDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(
            R.layout.dialog_usage_permission, container,
            false
        )
        view.buttonPermit.setOnClickListener {
            onPermitClicked()
        }
        view.buttonCancel.setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun onPermitClicked() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        dismiss()
    }

    companion object {

        fun newInstance(): AppCompatDialogFragment = UsageAccessPermissionDialog()
    }

}