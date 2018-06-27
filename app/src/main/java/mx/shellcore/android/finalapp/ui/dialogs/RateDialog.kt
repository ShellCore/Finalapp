package mx.shellcore.android.finalapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.utils.showMessage

class RateDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.dialog_title))
                .setView(R.layout.dialog_rate)
                .setPositiveButton(getString(R.string.dialog_button_ok)) { dialog, which ->
                    activity!!.showMessage("Pressed Ok")
                }
                .setNegativeButton(getString(R.string.dialog_button_cancel)) { dialog, which ->
                    activity!!.showMessage("Pressed Cancel")
                }
                .create()
    }
}