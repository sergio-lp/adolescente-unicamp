package com.nrgbrainn.adolescentes.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.helpers.USER_SCORE
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mApp: FirebaseApp
    private lateinit var mCbManager: CallbackManager
    private lateinit var mDB: DatabaseHelper
    private var TAG: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_login)

        mApp = FirebaseApp.initializeApp(this)!!
        mAuth = FirebaseAuth.getInstance(mApp)
        mDB = DatabaseHelper(mApp)


        //Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_token_request))
            .requestId()
            .requestEmail()
            .requestProfile()
            .build()

        val sign = GoogleSignIn.getClient(this, gso)

        btn_google_login.setOnClickListener {
            startActivityForResult(sign.signInIntent, RC_SIGN_IN)
        }

        //Facebook SignIn
        fb.setOnClickListener { btn_facebook_login.performClick() }
        mCbManager = CallbackManager.Factory.create()
        btn_facebook_login.setReadPermissions("email")
        btn_facebook_login.registerCallback(mCbManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val authToken = result!!.accessToken
                if (Profile.getCurrentProfile() == null) {
                    val profileTracker = object : ProfileTracker() {
                        override fun onCurrentProfileChanged(
                            oldProfile: Profile?,
                            currentProfile: Profile?
                        ) {
                            firebaseAuthenticateFacebook(currentProfile!!, authToken)
                        }
                    }
                    profileTracker.startTracking()
                } else {
                    firebaseAuthenticateFacebook(Profile.getCurrentProfile(), authToken)
                }
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(baseContext, R.string.error, Toast.LENGTH_SHORT).show()
                Log.e(
                    "TAG",
                    "Login Activity - onActivityResult: " + error?.message
                )
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)

                if (account != null && account.idToken != null && account.displayName != null) {
                    firebaseAuthenticateGoogle(account)
                }

            } catch (e: ApiException) {
                errorHandler(e, object {}.javaClass.enclosingMethod?.name)
            }
        } else {
            mCbManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthenticateGoogle(account: GoogleSignInAccount?) {
        try {
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            mAuth.signInWithCredential(credential).addOnSuccessListener {
                try {
                    userLogin(mAuth.uid!!)
                } catch (e: Exception) {
                    errorHandler(e, "userLogin")
                }
            }.addOnFailureListener { e ->
                errorHandler(e, "signWithCredential")
            }

        } catch (e: Exception) {
            errorHandler(e, object {}.javaClass.enclosingMethod?.name)
        }
    }

    private fun firebaseAuthenticateFacebook(p: Profile?, token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)

        mAuth.signInWithCredential(credential).addOnSuccessListener {
            try {
                userLogin(mAuth.uid!!)
            } catch (e: Exception) {
                errorHandler(e, "userLogin")
            }
        }.addOnFailureListener { e ->
            errorHandler(e, "signWithCredential")
        }
    }

    private fun userLogin(uid: String) {
        mDB.searchUser(uid)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && task.result!!.exists()) {
                        mDB.addUserPrefId(baseContext, mAuth.uid!!)
                        mDB.setUserPrefScore(
                            baseContext,
                            (task.result!![USER_SCORE] as Long).toInt()
                        )
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        mDB.finishDB()
                        finish()
                    } else {
                        mDB.addUser(
                            mAuth.uid!!,
                            mAuth.currentUser?.displayName!!,
                            mAuth.currentUser?.photoUrl.toString()
                        ).addOnSuccessListener {
                            mDB.addUserPrefId(baseContext, mAuth.uid!!)
                            startActivity(Intent(baseContext, MainActivity::class.java))
                            mDB.finishDB()
                            finish()
                        }.addOnFailureListener { e ->
                            errorHandler(e, "addUser")
                        }
                    }
                } else {
                    errorHandler(task.exception, "searchUser")
                }
            }
    }

    private fun errorHandler(e: Exception?, m: String?) {
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
        Log.e(
            "ERROR",
            m + ": " + e?.message
        )
    }

    companion object {
        private const val RC_SIGN_IN = 1001
    }
}

