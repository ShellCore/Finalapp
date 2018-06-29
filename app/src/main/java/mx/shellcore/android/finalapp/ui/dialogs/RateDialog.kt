package mx.shellcore.android.finalapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_rate.view.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.models.NewRateEvent
import mx.shellcore.android.finalapp.models.Rate
import mx.shellcore.android.finalapp.utils.RxBus
import mx.shellcore.android.finalapp.utils.showMessage
import java.util.*
import java.util.zip.Inflater

class RateDialog : DialogFragment() {

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser : FirebaseUser

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        setupCurrentUser()

        val view = activity!!.layoutInflater
                .inflate(R.layout.dialog_rate, null)

        return AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.dialog_title))
                .setView(view)
                .setPositiveButton(getString(R.string.dialog_button_ok)) { _, _ ->
                    activity!!.showMessage("Pressed Ok")
                    val textRate = view.edtRateFeedback.text.toString()
                    if (textRate.isNotEmpty()) {
                        val imageUrl = currentUser.photoUrl?.toString()
                                ?: run { "" }
                        val rate = Rate(currentUser.uid, textRate, view.ratFeedback.rating, Date(), imageUrl)
                        RxBus.publish(NewRateEvent(rate))
                    }
                }
                .setNegativeButton(getString(R.string.dialog_button_cancel)) { _, _ ->
                    activity!!.showMessage("Pressed Cancel")
                }
                .create()
    }

    private fun setupCurrentUser() {
        currentUser = mAuth.currentUser!!
    }
}