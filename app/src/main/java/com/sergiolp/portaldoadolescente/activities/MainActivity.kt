package com.sergiolp.portaldoadolescente.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.fragments.InfoFragment
import com.sergiolp.portaldoadolescente.fragments.MainFragment
import com.sergiolp.portaldoadolescente.fragments.QuestionsFragment
import com.sergiolp.portaldoadolescente.fragments.RankingFragment
import com.sergiolp.portaldoadolescente.helpers.DatabaseHelper
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
        val rankingFragment = RankingFragment()
        val questionsFragment = QuestionsFragment()
        val infoFragment = InfoFragment()

        CoroutineScope(Dispatchers.Main).async {
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, mainFragment).commit()
            getUserScore()

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
    }

    private fun getUserScore() {
        try {
            val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(this)!!)
            val score = dbHelper.getUserPrefScore(baseContext)
            dbHelper.finishDB()

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