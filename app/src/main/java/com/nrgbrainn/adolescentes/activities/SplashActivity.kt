package com.nrgbrainn.adolescentes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.helpers.USER_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                delay(2000)

                if (baseContext.getSharedPreferences("users", Context.MODE_PRIVATE)
                        .getString(USER_ID, "") == "") {
                    val i = Intent(baseContext, IntroActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(baseContext, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
            } catch (e: Exception) {
                AlertDialog.Builder(applicationContext)
                    .setTitle("Erro")
                    .setMessage("Ocorreu um erro: " + e.localizedMessage)
                    .setCancelable(true)
                    .show()
            }
        }
    }
}