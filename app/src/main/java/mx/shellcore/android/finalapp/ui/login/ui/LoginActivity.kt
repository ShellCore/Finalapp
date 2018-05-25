package mx.shellcore.android.finalapp.ui.login.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import mx.shellcore.android.finalapp.R

class LoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLoginSignIn.setOnClickListener {
            val email = edtLoginEmail.text.toString()
            val password = edtLoginPassword.text.toString()
            if (isValidEmailPassword()) {
                signinByEmail(email, password)
            } else {
                showMessage(getString(R.string.login_message_not_valid))
            }
        }
    }

    private fun signinByEmail(email: String, password: String) {
        // TODO Falta implementación
    }

    private fun isValidEmailPassword() : Boolean {
        // TODO Falta implementación
        return false
    }

    private fun showMessage(message: String) {
        // TODO Falta implementación
    }
}
