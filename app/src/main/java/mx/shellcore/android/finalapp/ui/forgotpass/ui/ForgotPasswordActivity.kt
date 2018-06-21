package mx.shellcore.android.finalapp.ui.forgotpass.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.login.ui.LoginActivity
import mx.shellcore.android.finalapp.utils.goToActivity
import mx.shellcore.android.finalapp.utils.isValidEmail
import mx.shellcore.android.finalapp.utils.showMessage
import mx.shellcore.android.finalapp.utils.validate

class ForgotPasswordActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtForgotPasswordEmail.validate {
            edtForgotPasswordEmail.error = if (isValidEmail(it)) null else getString(R.string.default_validation_error_not_valid_email)
        }

        btnForgotPasswordGotoLogin.setOnClickListener {
            goToLoginActivity()
        }

        btnForgotPasswordResetPassword.setOnClickListener {
            val email = edtForgotPasswordEmail.text.toString()
            if (isValidEmail(email)) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
                    showMessage(getString(R.string.reset_password_message_reset_password_email_sent))
                    goToLoginActivity()
                }
            } else {
                showMessage(getString(R.string.reset_password_message_not_correct_email))
            }
        }
    }

    private fun goToLoginActivity() {
        goToActivity<LoginActivity> {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
