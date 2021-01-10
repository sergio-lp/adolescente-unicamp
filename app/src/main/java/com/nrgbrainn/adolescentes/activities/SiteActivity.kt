package com.nrgbrainn.adolescentes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.helpers.*
import kotlinx.android.synthetic.main.activity_site.*
import kotlinx.android.synthetic.main.activity_site.toolbar

class SiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        val unitId = intent.getStringExtra(UNIT_ID).toString()
        val contentId = intent.getStringExtra(CONTENT_ID).toString()
        val contentTitle = intent.getStringExtra(CONTENT_TITLE).toString()
        val contentUrl = intent.getStringExtra(CONTENT_URL).toString()
        val userId = intent.getStringExtra(USER_ID).toString()

        setSupportActionBar(toolbar)
        supportActionBar?.title = contentTitle
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (contentUrl != "") {
            web_view.loadUrl(SITE_URL + contentUrl)
        } else {
            web_view.loadUrl(SITE_URL + contentTitle.toLowerCase().replace(" ", "-"))
        }

        btn_quiz.setOnClickListener {
            val i = Intent(this, QuizActivity::class.java)
            i.putExtra(UNIT_ID, unitId)
            i.putExtra(CONTENT_ID, contentId)
            i.putExtra(CONTENT_TITLE, contentTitle)
            i.putExtra(USER_ID, userId)
            startActivity(i)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }
}