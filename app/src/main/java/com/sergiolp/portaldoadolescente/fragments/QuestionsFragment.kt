package com.sergiolp.portaldoadolescente.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.adapters.QuestionAdapter
import com.sergiolp.portaldoadolescente.helpers.DatabaseHelper
import com.sergiolp.portaldoadolescente.models.Question

class QuestionsFragment : Fragment() {
    private var current: Int = 0
    private var questList: MutableList<Question?> = mutableListOf()
    private var loading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_questions, container, false)

        val rvQuestions = v.findViewById<RecyclerView>(R.id.rv_questions)
        val progressBar = v.findViewById<ProgressBar>(R.id.progress_bar)
        val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(context!!)!!)

        val fab = v.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_new_question, null)
            val ed = view.findViewById<EditText>(R.id.ed_question)

            AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(R.string.send_question) { _, _ ->
                    val dbHelp = DatabaseHelper(FirebaseApp.initializeApp(context!!)!!)

                    val authorId = dbHelp.getUserPrefId(context!!)
                    dbHelp.addQuestion(
                        Question(
                            null,
                            ed.text.toString(),
                            authorId,
                            false,
                            null,
                            null,
                            Timestamp.now()
                        )
                    ).addOnSuccessListener {
                        AlertDialog.Builder(context)
                            .setMessage(R.string.new_question_success)
                            .setNeutralButton(R.string.ok) { _, _ -> }
                            .show()
                        dbHelp.finishDB()
                    }.addOnFailureListener {
                        dbHelp.finishDB()
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel) { p0, _ ->
                    p0.dismiss()
                }.show()
        }

        loadData(dbHelper, rvQuestions, progressBar)

        return v
    }

    private fun loadData(
        dbHelper: DatabaseHelper,
        rvQuestions: RecyclerView,
        progressBar: ProgressBar
    ) {
        loading = true
        dbHelper.getQuestions(current).addOnSuccessListener { response ->
            if (!response.isEmpty) {
                questList = response.toObjects(Question::class.java)

                rvQuestions.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = QuestionAdapter(questList)
                    hasFixedSize()

                    loading = false
                }

                progressBar.visibility = View.GONE
                rvQuestions.visibility = View.VISIBLE

                current += response.size()
            } else {
                progressBar.visibility = View.GONE
                try {
                    Toast.makeText(context, R.string.no_questions, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }
}