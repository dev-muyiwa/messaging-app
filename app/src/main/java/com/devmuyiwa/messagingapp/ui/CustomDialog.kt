package com.devmuyiwa.messagingapp.ui

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.CustomDialogLayoutBinding

class CustomDialog internal constructor(private val activity: Activity) {
    private var dialog: AlertDialog? = null
    fun show() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog_layout, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog!!.show()
    }

    fun dismiss() {
        dialog!!.dismiss()
    }

    fun message(message: CharSequence){
        var binding: CustomDialogLayoutBinding? = null

    }
}