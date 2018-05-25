package mx.shellcore.android.finalapp.ui.signup.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.R.id.*
import mx.shellcore.android.finalapp.ui.login.ui.LoginActivity


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUpGotoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnSignUpSignUp.setOnClickListener {
            val email = edtSignUpEmail.text.toString()
            val password = edtSignUpPassword.text.toString()
            if (isValidEmailAndPassword()) {
                signupByEmail(email, password)
            } else {
                showMessage(getString(R.string.signup_message_not_valid))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            showMessage(getString(R.string.signup_message_not_logged))
        } else {
            showMessage(getString(R.string.signup_message_logged))
        }
    }

    private fun isValidEmailAndPassword() : Boolean {
        return !edtSignUpEmail.text.isNullOrEmpty()
                && !edtSignUpPassword.text.isNullOrEmpty()
                && edtSignUpPassword.text.toString() == edtSignUpConfirmPassword.text.toString()
    }

    private fun signupByEmail(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        showMessage(getString(R.string.signup_message_created))
                        val user = mAuth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        showMessage(getString(R.string.signup_message_not_created))
                    }
                }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show()
    }
}
