package mx.shellcore.android.finalapp.ui.signup.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.login.ui.LoginActivity
import mx.shellcore.android.finalapp.utils.*


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUpGotoLogin.setOnClickListener {

            goToActivity<LoginActivity>() {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnSignUpSignUp.setOnClickListener {
            val email = edtSignUpEmail.text.toString()
            val password = edtSignUpPassword.text.toString()
            if (isValidEmailAndPassword(email, password)) {
                signupByEmail(email, password)
            } else {
                showMessage(R.string.signup_message_not_valid)
            }
        }

        edtSignUpEmail.validate {
            edtLoginEmail.error = if (isValidEmail(it)) null else "Email is not valid"
        }

        edtSignUpPassword.validate {
            edtSignUpPassword.error = if (isValidPassword(it)) null else "Password should contain 1 lowercase, 1 uppercase, 1 number, 1 special character and at least 4 characters lenght."
        }

        edtSignUpConfirmPassword.validate {
            edtSignUpConfirmPassword.error = if (isValidConfirmPassword(edtSignUpPassword.text.toString(), it)) null else "Confirm Passord does not match with Password"
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            showMessage(R.string.signup_message_not_logged)
        } else {
            showMessage(R.string.signup_message_logged)
        }
    }

    private fun isValidEmailAndPassword(email: String, password: String): Boolean {
        return !email.isNullOrEmpty()
                && !password.isNullOrEmpty()
                && password == edtSignUpConfirmPassword.text.toString()
    }

    private fun signupByEmail(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        showMessage(R.string.signup_message_created)
                        goToActivity<LoginActivity>() {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    } else {
                        // If sign in fails, display a message to the user.
                        showMessage(R.string.signup_message_not_created)
                    }
                }
    }


}
