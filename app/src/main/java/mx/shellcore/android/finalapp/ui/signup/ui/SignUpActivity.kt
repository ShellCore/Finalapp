package mx.shellcore.android.finalapp.ui.signup.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.login.ui.LoginActivity
import mx.shellcore.android.finalapp.utils.*


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setupValidations()
        setupOnClicks()
    }

    private fun setupOnClicks() {
        btnSignUpGotoLogin.setOnClickListener {

            goToActivity<LoginActivity>() {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnSignUpSignUp.setOnClickListener {
            val email = edtSignUpEmail.text.toString()
            val password = edtSignUpPassword.text.toString()
            val confirmPassword = edtSignUpConfirmPassword.text.toString()
            if (isValidEmailAndPassword(email, password, confirmPassword)) {
                signupByEmail(email, password)
            } else {
                showMessage(R.string.default_validation_error_not_valid_data)
            }
        }
    }

    private fun setupValidations() {
        edtSignUpEmail.validate {
            edtSignUpEmail.error = if (isValidEmail(it)) null else getString(R.string.default_validation_error_not_valid_email)
        }

        edtSignUpPassword.validate {
            edtSignUpPassword.error = if (isValidPassword(it)) null else getString(R.string.default_validation_error_not_valid_password)
        }

        edtSignUpConfirmPassword.validate {
            edtSignUpConfirmPassword.error = if (isValidConfirmPassword(edtSignUpPassword.text.toString(), it)) null else getString(R.string.default_validation_error_not_valid_email)
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

    private fun isValidEmailAndPassword(email: String, password: String, confirmPassword: String): Boolean {
        return isValidEmail(email)
                && isValidPassword(password)
                && isValidConfirmPassword(password, confirmPassword)
    }

    private fun signupByEmail(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                            showMessage(R.string.signup_message_created)
                            goToActivity<LoginActivity>() {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        showMessage(R.string.default_message_error)
                    }
                }
    }


}
