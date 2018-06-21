package mx.shellcore.android.finalapp.ui.login.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.ui.forgotpass.ui.ForgotPasswordActivity
import mx.shellcore.android.finalapp.ui.signup.ui.SignUpActivity
import mx.shellcore.android.finalapp.utils.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mGoogleApiClient: GoogleApiClient by lazy { getGoogleApiClient() }

    private val REQUEST_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupValidations()
        setupOnClicks()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        showMessage(getString(R.string.default_message_connection_fail))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                loginByGoogleAccountIntoFirebase(account!!)
            }
        }
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

        btnLoginSigninGoogle.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, REQUEST_CODE)
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

    private fun getGoogleApiClient() : GoogleApiClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        return GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso    )
                .build()
    }

    private fun loginByGoogleAccountIntoFirebase(googleAccount : GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            showMessage(getString(R.string.login_message_signed_in_google))
        }
    }
}
