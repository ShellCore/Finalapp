package mx.shellcore.android.finalapp.ui.login.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.forgotpass.ui.ForgotPasswordActivity
import mx.shellcore.android.finalapp.ui.signup.ui.SignUpActivity
import mx.shellcore.android.finalapp.utils.*

class LoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupValidations()
        setupOnClicks()
    }

    private fun setupOnClicks() {
        btnLoginSignIn.setOnClickListener {
            val email = edtLoginEmail.text.toString()
            val password = edtLoginPassword.text.toString()
            if (isValidEmailPassword(email, password)) {
                loginByEmail(email, password)
            } else {
                showMessage(getString(R.string.default_validation_error_not_valid_data))
            }
        }

        txtLoginForgotPassword.setOnClickListener {
            goToActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnLoginSignUp.setOnClickListener {
            goToActivity<SignUpActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun setupValidations() {
        edtLoginEmail.validate {
            edtLoginEmail.error = if (isValidEmail(it)) null else getString(R.string.default_validation_error_not_valid_email)
        }

        edtLoginPassword.validate {
            edtLoginPassword.error = if (isValidPassword(it)) null else getString(R.string.default_validation_error_not_valid_password)
        }
    }

    private fun loginByEmail(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                if (mAuth.currentUser!!.isEmailVerified) {
                    showMessage(getString(R.string.login_message_email_verified))
                } else {
                    showMessage(getString(R.string.login_message_email_not_verified))
                }
            } else {
                showMessage(getString(R.string.default_message_error))
            }
        }
    }

    private fun isValidEmailPassword(email: String, password: String): Boolean {
        return isValidEmail(email)
                && isValidPassword(password)
    }
}
