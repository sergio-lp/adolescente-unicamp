package com.nrgbrainn.adolescentes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.fragments.QuizFragment
import com.nrgbrainn.adolescentes.fragments.ResultFragment
import com.nrgbrainn.adolescentes.helpers.*
import com.nrgbrainn.adolescentes.models.Quiz
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {
    var currentScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        //Getting Unit's ID, Content's ID and Content's title from intent extras
        val unitId = intent.getStringExtra(UNIT_ID).toString()
        val contentId = intent.getStringExtra(CONTENT_ID).toString()
        val contentTitle = intent.getStringExtra(CONTENT_TITLE).toString()
        val userId = intent.getStringExtra(USER_ID).toString()

        //Toolbar enabling
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Quiz: $contentTitle"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Get Content's quizzes
        val dbHelper = DatabaseHelper(FirebaseApp.getInstance())
        dbHelper.getContentQuiz(unitId, contentId).get().addOnSuccessListener { result ->
            val fragmentList = mutableListOf<QuizFragment>()
            val quizList = result.toObjects(Quiz::class.java)

            for (quiz in quizList) {
                val arguments = Bundle()
                arguments.putString(QUESTION, quiz.question)
                arguments.putStringArrayList(ALTERNATIVES, quiz.alternatives)
                quiz.correct?.let { arguments.putInt(CORRECT, it) }

                val quizFragment = QuizFragment()
                quizFragment.arguments = arguments
                fragmentList.add(quizFragment)
            }

            val resultFrag = ResultFragment(unitId, contentId, contentTitle, userId)
            val arguments = Bundle()
            arguments.putString(UNIT_ID, unitId)
            arguments.putString(CONTENT_ID, contentId)
            arguments.putString(CONTENT_TITLE, contentTitle)
            fragmentList.add(resultFrag)

            val screenSlideAdapter = ScreenSlideAdapter(supportFragmentManager, fragmentList)

            quiz_pager.apply {
                adapter = screenSlideAdapter
            }

            quiz_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    (screenSlideAdapter.getItem(position) as QuizFragment).onFragmentResume()
                }

            })

            progress_bar.visibility = View.GONE
            quiz_pager.visibility = View.VISIBLE
            quiz_pager_indicator.visibility = View.VISIBLE

            dbHelper.finishDB()
        }.addOnFailureListener { e ->
            Toast.makeText(baseContext, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "TAG",
                "QuizActivity: " + e.message
            )
        }
    

    }

    private class ScreenSlideAdapter(
        fm: FragmentManager,
        val fragmentList: MutableList<QuizFragment>
    ) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }
}