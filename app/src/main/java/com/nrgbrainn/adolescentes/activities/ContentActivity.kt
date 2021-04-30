package com.nrgbrainn.adolescentes.activities

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.adapters.ContentAdapter
import com.nrgbrainn.adolescentes.helpers.COLOR
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.helpers.UNIT_ID
import com.nrgbrainn.adolescentes.helpers.UNIT_TITLE
import com.nrgbrainn.adolescentes.models.Content
import com.nrgbrainn.adolescentes.models.User
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

        val dbHelper = DatabaseHelper(FirebaseApp.getInstance())
        val userId = dbHelper.getUserPrefId(this)!!

        //Getting User completed content
        dbHelper.getUser(userId).get().addOnSuccessListener { r ->
            val user = r.toObject(User::class.java)

            if (user?.completed_content == null) {
                user?.completed_content = arrayListOf()
            }

            val completedContentMap = TreeMap<Int, Int>()
            val completedContentMapGreater = TreeMap<Int, Int>()


            if (user?.completed_content != null && user?.completed_content!!.isNotEmpty()) {

                if (user?.completed_content!!.size in 0..4 && dbHelper.shouldShowTrophyBadge(
                        1,
                        this
                    )
                ) {
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog_trophy, null)
                    view.findViewById<TextView>(R.id.tv_trophy).text =
                        Html.fromHtml(getString(R.string.trophy_1))
                    view.findViewById<ImageView>(R.id.img_badge)
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ribbon1))

                    val trophyDialog = AlertDialog.Builder(this)
                        .setView(view)
                        .show()

                    view.findViewById<Button>(R.id.btn_trophy)
                        .setOnClickListener { trophyDialog.dismiss() }
                } else if (user?.completed_content!!.size in 5..9 && dbHelper.shouldShowTrophyBadge(
                        5,
                        this
                    )
                ) {
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog_trophy, null)
                    view.findViewById<TextView>(R.id.tv_trophy).text =
                        Html.fromHtml(getString(R.string.trophy_5))
                    view.findViewById<ImageView>(R.id.img_badge)
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ribbon5))

                    val trophyDialog = AlertDialog.Builder(this)
                        .setView(view)
                        .show()

                    view.findViewById<Button>(R.id.btn_trophy)
                        .setOnClickListener { trophyDialog.dismiss() }
                } else if (user?.completed_content!!.size in 10..24 && dbHelper.shouldShowTrophyBadge(
                        10,
                        this
                    )
                ) {
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog_trophy, null)
                    view.findViewById<TextView>(R.id.tv_trophy).text =
                        Html.fromHtml(getString(R.string.trophy_10))
                    view.findViewById<ImageView>(R.id.img_badge)
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ribbon10))

                    val trophyDialog = AlertDialog.Builder(this)
                        .setView(view)
                        .show()

                    view.findViewById<Button>(R.id.btn_trophy)
                        .setOnClickListener { trophyDialog.dismiss() }
                } else if (user?.completed_content!!.size in 25..49 && dbHelper.shouldShowTrophyBadge(
                        25,
                        this
                    )
                ) {
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog_trophy, null)
                    view.findViewById<TextView>(R.id.tv_trophy).text =
                        Html.fromHtml(getString(R.string.trophy_25))
                    view.findViewById<ImageView>(R.id.img_badge)
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ribbon25))

                    val trophyDialog = AlertDialog.Builder(this)
                        .setView(view)
                        .show()

                    view.findViewById<Button>(R.id.btn_trophy)
                        .setOnClickListener { trophyDialog.dismiss() }
                } else if (user?.completed_content!!.size in 50..100 && dbHelper.shouldShowTrophyBadge(
                        50,
                        this
                    )
                ) {
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog_trophy, null)
                    view.findViewById<TextView>(R.id.tv_trophy).text =
                        Html.fromHtml(getString(R.string.trophy_50))
                    view.findViewById<ImageView>(R.id.img_badge)
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ribbon50))

                    val trophyDialog = AlertDialog.Builder(this)
                        .setView(view)
                        .show()

                    view.findViewById<Button>(R.id.btn_trophy)
                        .setOnClickListener { trophyDialog.dismiss() }
                }

                for (content in user?.completed_content!!) {
                    if (content.startsWith("$unitId+")) {
                        if (content.length > 3) {
                            completedContentMapGreater["${content[2]}${content[3]}".toInt()] = Content.STATUS_DONE
                        } else {
                            completedContentMap[content[2].toString().toInt()] = Content.STATUS_DONE
                        }
                    }
                }

                for (content in completedContentMapGreater) {
                    completedContentMap[content.key] = Content.STATUS_DONE
                }
            }

            val ccMap = completedContentMap.toSortedMap()

            if (ccMap.isNotEmpty()) {
                ccMap[ccMap.lastKey() + 1] =
                    Content.STATUS_UNLOCKED
            } else {
                ccMap[1] = Content.STATUS_UNLOCKED
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

                    if (ccMap.containsKey(content.id?.toInt())) {
                        content.status = ccMap.getValue(content.id?.toInt())
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
            }.addOnFailureListener { e ->
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                Log.e(
                    "TAG",
                    "Login Activity - onActivityResult: " + e.message
                )
                dbHelper.finishDB()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "TAG",
                "Login Activity - onActivityResult: " + e.message
            )
            dbHelper.finishDB()
        }
    }

}