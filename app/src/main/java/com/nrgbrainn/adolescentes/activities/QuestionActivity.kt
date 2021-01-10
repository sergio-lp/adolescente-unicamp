package com.nrgbrainn.adolescentes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.helpers.QUESTION
import com.nrgbrainn.adolescentes.helpers.REPLY
import com.nrgbrainn.adolescentes.helpers.REPLY_DATE
import kotlinx.android.synthetic.main.activity_content.toolbar
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val question = intent.getStringExtra(QUESTION)
        val replyDate = intent.getStringExtra(REPLY_DATE)
        val reply = intent.getStringExtra(REPLY)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Pergunta"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tv_question.text = question
        tv_date.text = replyDate
        tv_reply.text = Html.fromHtml(reply)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }
}