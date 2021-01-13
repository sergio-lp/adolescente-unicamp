package com.nrgbrainn.adolescentes.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.fragments.*
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.helpers.USER_SCORE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar enabling
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val mainFragment = MainFragment()
        val rankingFragment = ScoreFragment()
        val questionsFragment = QuestionsFragment()
        val infoFragment = InfoFragment()

        getUserScore()

        try {
            //Fragment definition
            CoroutineScope(Dispatchers.Main).async {
                var transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, mainFragment).commit()

                bottom_navigation.setOnNavigationItemSelectedListener(object :
                    BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        transaction = supportFragmentManager.beginTransaction()
                        when (item.itemId) {
                            R.id.menu_quiz -> {
                                transaction.replace(R.id.frame_layout, mainFragment).commit()
                            }
                            R.id.menu_ranking -> {
                                transaction.replace(R.id.frame_layout, rankingFragment).commit()
                            }
                            R.id.menu_questions -> {
                                transaction.replace(R.id.frame_layout, questionsFragment).commit()
                            }
                            R.id.menu_about -> {
                                transaction.replace(R.id.frame_layout, infoFragment).commit()
                            }
                            else -> return false
                        }

                        return true
                    }

                })

                withContext(Dispatchers.Main) {
                    progress_bar.visibility = View.GONE
                    frame_layout.visibility = View.VISIBLE

                }
            }.start()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "ERROR",
                ": " + e.message
            )
        }
    }

    private fun getUserScore() {
        try {
            val score = this.getSharedPreferences("users", Context.MODE_PRIVATE)
                .getInt(USER_SCORE, 0)

            toolbar_score.text = score.toString()
        } catch (e: Exception) {
            AlertDialog.Builder(this)
                .setTitle("Erro")
                .setMessage("Ocorreu um erro: " + e.localizedMessage)
                .setCancelable(true)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserScore()
    }

}