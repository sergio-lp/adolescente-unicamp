package com.sergiolp.portaldoadolescente.activities

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
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.helpers.DatabaseHelper
import com.sergiolp.portaldoadolescente.helpers.USER_SCORE
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var cbManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth(FirebaseApp.initializeApp(this))

        val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(this)!!)

        if (dbHelper.getUserPrefId(this) == "") {
            btn_google_login.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.id_token_request))
                    .requestProfile()
                    .requestId()
                    .build()

                val sign = GoogleSignIn.getClient(this, gso)
                dbHelper.finishDB()
                startActivityForResult(sign.signInIntent, RC_SIGN_IN)
            }

            fb.setOnClickListener { btn_facebook_login.performClick() }

            cbManager = CallbackManager.Factory.create()
            btn_facebook_login.setReadPermissions("email")
            btn_facebook_login.registerCallback(cbManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    val authToken = result!!.accessToken
                    if (Profile.getCurrentProfile() == null) {
                        val profileTracker = object : ProfileTracker() {
                            override fun onCurrentProfileChanged(
                                oldProfile: Profile?,
                                currentProfile: Profile?
                            ) {
                                firebaseAuthWithFacebook(currentProfile!!, authToken)
                            }
                        }
                        profileTracker.startTracking()
                    } else {
                        firebaseAuthWithFacebook(Profile.getCurrentProfile(), authToken)
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
        } else {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val account =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)

                if (account != null && account.id != null && account.displayName != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: Exception) {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                Log.e(
                    "TAG",
                    "Login Activity - onActivityResult: " + e.message
                )
            }
        } else {
            cbManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            if (auth.uid != null && auth.uid != "") {
                val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(this)!!)

                dbHelper.searchUser(auth.uid!!).addOnSuccessListener { result ->
                    if (result != null && result.exists()) {
                        dbHelper.addUserPrefId(this, auth.uid!!)
                        dbHelper.setUserPrefScore(this, (result[USER_SCORE] as Long).toInt())
                        startActivity(Intent(this, MainActivity::class.java))
                        dbHelper.finishDB()
                        finish()
                    } else {
                        dbHelper.addUser(
                            auth.uid!!,
                            account.displayName!!,
                            account.photoUrl.toString()
                        ).addOnSuccessListener {
                            dbHelper.addUserPrefId(this, auth.uid!!)
                            startActivity(Intent(this, MainActivity::class.java))
                            dbHelper.finishDB()
                            finish()
                        }
                    }
                }.addOnFailureListener { e -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                    Log.e(
                        "TAG",
                        "Login Activity - onActivityResult: " + e.message
                    ) }
            }
        }
    }

    private fun firebaseAuthWithFacebook(p: Profile?, token: AccessToken) {
        val username = p!!.firstName + " " + p.lastName
        val pic = p.getProfilePictureUri(500, 500)

        val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(this)!!)
        val credential = FacebookAuthProvider.getCredential(token.token)

        auth.signInWithCredential(credential).addOnSuccessListener {
            Log.e("TAG", "CREDENTIAL SUCESS: ")
            if (auth.uid != null && auth.uid != "") {
                dbHelper.searchUser(auth.uid!!).addOnSuccessListener { result ->
                    if (result != null && result.exists()) {
                        Log.e("TAG", "firebaseAuthWithFacebook: result exists")
                        dbHelper.addUserPrefId(this, auth.uid!!)
                        dbHelper.setUserPrefScore(this, (result[USER_SCORE] as Long).toInt())
                        startActivity(Intent(this, MainActivity::class.java))
                        dbHelper.finishDB()
                        finish()
                    } else {
                        Log.e("TAG", "firebaseAuthWithFacebook: oioioioioi"  + auth.uid!!)
                        dbHelper.addUser(
                            auth.uid!!,
                            username,
                            pic.toString()
                        ).addOnSuccessListener {
                            dbHelper.addUserPrefId(this, auth.uid!!)
                            startActivity(Intent(this, MainActivity::class.java))
                            dbHelper.finishDB()
                            finish()
                        }
                    }
                }.addOnFailureListener { e -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                    Log.e(
                        "TAG",
                        "Login Activity - onActivityResult: " + e.message
                    ) }
            } else {
                Log.e("TAG", "firebaseAuthWithFacebook: AUTH NULL")
            }
        }.addOnFailureListener { e -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "TAG",
                "Login Activity - onActivityResult: " + e.message
            ) }
    }

    companion object {
        private const val RC_SIGN_IN = 1001
    }
}