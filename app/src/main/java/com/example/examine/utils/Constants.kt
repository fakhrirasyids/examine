package com.example.examine.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object Constants {
    fun alertDialogMessage(context: Context, message: String, title: String? = null) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)

        with(builder)
        {
            if (title != null) {
                setTitle(title)
            }
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }
}