package com.pabs.operadores_funeraria.utils

import android.app.AlertDialog
import android.content.Context

enum class MessageType(type: String) {
    ERROR("type_error"), SUCCESS("type_success"), INFO("type_info")
}
object MessageFactory {



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
                    .setPositiveButton(negativeText) { dialog, _ ->
                        onClickPositiveButton()
                        dialog.dismiss()
                    }
            }
        }
    }
}