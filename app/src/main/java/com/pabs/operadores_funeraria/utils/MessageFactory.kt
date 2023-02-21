package com.pabs.operadores_funeraria.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
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

        return AlertDialog.Builder(context).setView(dialogView)

    }

    fun getDialogLoading(ctx: Context, message: String): AlertDialog.Builder {
        val viewLoading = View.inflate(ctx, R.layout.dialog_refresh_service, null)
        viewLoading.findViewById<TextView>(R.id.tvMessageLoading).text = message

        return AlertDialog.Builder(ctx).setView(viewLoading)
    }

    fun getDialogUpdate(ctx: Context, version: String): AlertDialog.Builder {
        val viewLoading = View.inflate(ctx, R.layout.dialog_update_app, null)
        val btnGoToPlayStore = viewLoading.findViewById<TextView>(R.id.btnGoToPlayStore)
        viewLoading.findViewById<TextView>(R.id.tvNuevaVersion).text = "Nueva versión $version"
        btnGoToPlayStore.setOnClickListener {
                ctx.goToPlayStore()
        }

        return AlertDialog.Builder(ctx).setView(viewLoading)
    }

    fun getDialogFinalizarReco(ctx: Context, onClickPositiveButton: (code: String) -> Unit): AlertDialog.Builder {
        val viewFinalizarReco = View.inflate(ctx, R.layout.dialog_finalizar_recoleccion, null)
        val etCode = viewFinalizarReco.findViewById<TextInputEditText>(R.id.etCodigo)

        return  AlertDialog.Builder(ctx)
            .setView(viewFinalizarReco)
            .setTitle("Finalizar recolección")
            .setMessage("Para finalizar la recoleacción escribe el código proporcionado por el cliente")
            .setPositiveButton("ACEPTAR") { dialog, _ ->
                if (etCode.text.toString().isNotEmpty()){
                    onClickPositiveButton(etCode.text.toString())
                    dialog.dismiss()
                }
            }.setNegativeButton("CANCELAR") { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false)
    }



    fun getDialog(context: Context, type: MessageType, title: String, message: String, onClickPositiveButton: () -> Unit, positiveText: String? = "ACEPTAR", negativeText: String? = "CANCELAR"): AlertDialog.Builder {
        when(type){
            MessageType.ERROR -> {
                return AlertDialog.Builder(context, R.style.AlertDialogError)
                    .setIcon(R.drawable.baseline_error_24)
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
                return AlertDialog.Builder(context, R.style.AlertDialogSuccess)
                    .setIcon(R.drawable.baseline_check_circle_24)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText) { dialog, _ ->
                        onClickPositiveButton()
                        dialog.dismiss()
                    }
            }
            MessageType.INFO -> {
                return AlertDialog.Builder(context)
                    .setIcon(R.drawable.baseline_info_24)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText) { dialog, _ ->
                        onClickPositiveButton()
                        dialog.dismiss()
                    }.setNegativeButton(negativeText) { dialog, _ ->
                        dialog.dismiss()
                    }
            }
        }
    }


}