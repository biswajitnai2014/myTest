package com.bis.mytest.permission

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.widget.Button

class CommonMethod {
    companion object{
        fun createAlertDialog(
            context: Context,
            title: String,
            msg: String,
            positiveButtonText: String,
            negativeButtonText: String,
            listener: (Boolean) -> Unit
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton(
                positiveButtonText
            ) { dialog, which ->
                dialog.cancel()
                listener(true)
            }
            builder.setNegativeButton(
                negativeButtonText
            ) { dialog, which ->
                dialog.cancel()
                listener(false)
            }

            var alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

            val buttonbackground: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)

            val buttonbackground1: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)
        }
    }
}