package com.pabs.operadores_funeraria.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.R

enum class MessageType(type: String) {
    ERROR("type_error"), SUCCESS("type_success"), INFO("type_info")
}
object MessageFactory {

    @SuppressLint("SetTextI18n")
    fun getDialogInfo(context: Context): AlertDialog.Builder{
        val dialogView = View.inflate(context, R.layout.dialog_info, null)
        dialogView.findViewById<TextView>(R.id.tvVersion).text = "Versión ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        return AlertDialog.Builder(context)
            .setView(dialogView)
    }



    fun getDialog(context: Context, type: MessageType, title: String, message: String, onClickPositiveButton: () -> Unit, positiveText: String? = "ACEPTAR", negativeText: String? = "CANCELAR"): AlertDialog.Builder {
        when(type){
            MessageType.ERROR -> {
                return AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText) { dialog, _ ->
                        onClickPositiveButton()
                        dialog.dismiss()
                    }.setNegativeButton(negativeText) { dialog, _ ->
                        dialog.dismiss()
                    }
            }
            MessageType.SUCCESS -> {
                return AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText) { dialog, _ ->
                        onClickPositiveButton()
                        dialog.dismiss()
                    }
            }
            MessageType.INFO -> {
                return AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText) { dialog, _ ->
                        onClickPositiveButton()
                        dialog.dismiss()
                    }
            }
        }
    }
}