package com.sergiolp.portaldoadolescente.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.adapters.ContentAdapter
import com.sergiolp.portaldoadolescente.helpers.COLOR
import com.sergiolp.portaldoadolescente.helpers.DatabaseHelper
import com.sergiolp.portaldoadolescente.helpers.UNIT_ID
import com.sergiolp.portaldoadolescente.helpers.UNIT_TITLE
import com.sergiolp.portaldoadolescente.models.Content
import com.sergiolp.portaldoadolescente.models.User
import kotlinx.android.synthetic.main.activity_content.*
import java.util.*

class ContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }

    override fun onResume() {
        super.onResume()

        progress_bar.visibility = View.VISIBLE
        rv_content.visibility = View.GONE

        //Getting unit ID and title from intent extra
        val unitId = intent.getStringExtra(UNIT_ID).toString()
        val title = intent.getStringExtra(UNIT_TITLE)
        val color = intent.getStringExtra(COLOR)

        //Toolbar enabling
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(this)!!)
        val userId = dbHelper.getUserPrefId(this)!!

        //Getting User completed content
        dbHelper.getUser(userId).get().addOnSuccessListener { r ->
            val user = r.toObject(User::class.java)

            if (user?.completed_content == null) {
                user?.completed_content = arrayListOf()
            }

            val completedContentMap = TreeMap<String, Int>()



            if (user?.completed_content != null && user?.completed_content!!.isNotEmpty())
            for (content in user?.completed_content!!) {
                if (content.startsWith("$unitId+")) {
                    completedContentMap[content] = Content.STATUS_DONE
                }
            }

            if (completedContentMap.isNotEmpty()) {
                completedContentMap["$unitId+${(completedContentMap.lastKey()[2] + 1)}"] =
                    Content.STATUS_UNLOCKED
            } else {
                completedContentMap["$unitId+1"] = Content.STATUS_UNLOCKED
            }

            //Getting Unit's content and setting whether the user has or hasn't completed
            dbHelper.getUnitContent(unitId).get().addOnSuccessListener { response ->
                val contentList = response.toObjects(Content::class.java)

                contentList.sortBy { Integer.parseInt(it.id!!) }

                for (content in contentList) {
                    content.unitId = unitId
                    content.userId = userId
                    content.color = color

                    if (content.url == null) {
                        content.url = ""
                    }

                    if (completedContentMap.containsKey("$unitId+${content.id}")) {
                        content.status = completedContentMap.getValue("$unitId+${content.id}")
                    } else {
                        content.status = Content.STATUS_LOCKED
                    }

                }

                val manager = LinearLayoutManager(this)

                rv_content.apply {
                    adapter = ContentAdapter(contentList)
                    layoutManager = manager
                }

                progress_bar.visibility = View.GONE
                rv_content.visibility = View.VISIBLE

                dbHelper.finishDB()
            }.addOnFailureListener { e -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                Log.e(
                    "TAG",
                    "Login Activity - onActivityResult: " + e.message
                ) }
        }.addOnFailureListener { e -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "TAG",
                "Login Activity - onActivityResult: " + e.message
            ) }
    }

}