package mx.shellcore.android.finalapp.ui.login.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.forgotpass.ui.ForgotPasswordActivity
import mx.shellcore.android.finalapp.ui.signup.ui.SignUpActivity
import mx.shellcore.android.finalapp.utils.goToActivity
import mx.shellcore.android.finalapp.utils.showMessage

class LoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLoginSignIn.setOnClickListener {
            val email = edtLoginEmail.text.toString()
            val password = edtLoginPassword.text.toString()
            if (isValidEmailPassword(email, password)) {
                loginByEmail(email, password)
            } else {
                showMessage(getString(R.string.login_message_not_valid))
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

    private fun loginByEmail(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                showMessage("User is now logged in.")
            } else {
                showMessage("An unexcepected error ocurred, please try again.")
            }
        }
    }

    private fun isValidEmailPassword(email: String, password: String): Boolean {
        return email.isNullOrEmpty().not()
                && !password.isNullOrEmpty()
    }
}
