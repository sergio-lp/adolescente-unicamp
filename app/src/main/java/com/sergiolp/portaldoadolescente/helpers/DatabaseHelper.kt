package com.sergiolp.portaldoadolescente.helpers

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.models.Question

class DatabaseHelper(app: FirebaseApp) : ViewModel() {
    private val db = FirebaseFirestore.getInstance(app)

    companion object Paths {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_UNITS = "units"
        private const val COLLECTION_CONTENT = "content"
        private const val COLLECTION_QUIZ = "quiz"
        private const val COLLECTION_QUESTIONS = "questions"
        private const val REPLIED = "replied"
        private const val PACE: Long = 10
    }

    private val collectionUsers = db.collection(COLLECTION_USERS)
    private val collectionQuestions = db.collection(COLLECTION_QUESTIONS)
    val collectionUnits = db.collection(COLLECTION_UNITS)

    fun getUnitContent(unitId: String): CollectionReference {
        return collectionUnits.document(unitId).collection(COLLECTION_CONTENT)
    }

    fun getContentQuiz(unitId: String, contentId: String): CollectionReference {
        return collectionUnits.document(unitId)
            .collection(COLLECTION_CONTENT).document(contentId)
            .collection(COLLECTION_QUIZ)
    }

    fun getUser(id: String): DocumentReference {
        return collectionUsers.document(id)
    }

    fun getUsers(): Query {
        return collectionUsers.orderBy(USER_SCORE, Query.Direction.DESCENDING)
    }

    fun searchUser(userId: String): Task<DocumentSnapshot> {
        return collectionUsers.document(userId).get()
    }

    fun addUser(userId: String, username: String, photoUrl: String): Task<Void> {
        return collectionUsers.document(userId).set(
            mutableMapOf(
                USERNAME to username,
                PHOTO_URL to photoUrl,
                USER_SCORE to 0,
                COMPLETED_CONTENT to arrayListOf<String>()
            )
        )
    }

    fun getQuestions(currentPage: Int): Task<QuerySnapshot> {
        /*return collectionQuestions
            .orderBy("date", Query.Direction.ASCENDING)
            .startAt(currentPage)
            .whereEqualTo(REPLIED, true)
            .limit(PACE)
            .get()*/
        return collectionQuestions.whereEqualTo("replied", true).get()
    }

    fun addQuestion(q: Question): Task<Void> {
        return collectionQuestions.document().set(q)
    }

    fun finishDB() {
        db.terminate()
    }

    fun getUserPrefScore(ctx: Context): Int? {
        return ctx.getSharedPreferences(COLLECTION_USERS, Context.MODE_PRIVATE)
            .getInt(USER_SCORE, 0)
    }

    fun getUserPrefId(ctx: Context): String? {
        return ctx.getSharedPreferences(COLLECTION_USERS, Context.MODE_PRIVATE)
            .getString(USER_ID, "")
    }

    fun addUserPrefId(ctx: Context, userId: String) {
        val sharedPreferences = ctx.getSharedPreferences(COLLECTION_USERS, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putString(USER_ID, userId)
            commit()
        }
    }

    fun setUserPrefScore(ctx: Context, score: Int) {
        with(ctx.getSharedPreferences(COLLECTION_USERS, Context.MODE_PRIVATE).edit()) {
            putInt(USER_SCORE, score)
            val c = commit()

            if (!c) {
                AlertDialog.Builder(ctx)
                    .setMessage(ctx.getString(R.string.error))
                    .show()
            }
        }
    }

    fun shouldShowTrophyBadge(trophy: Int, ctx: Context): Boolean {
        var t: String = ""

        when (trophy) {
            1 -> {
                t = BADGE_1
            }
            5 -> {
                t = BADGE_5
            }
            10 -> {
                t = BADGE_10
            }
            25 -> {
                t = BADGE_25
            }
            50 -> {
                t = BADGE_50
            }
        }

        val sharedPreferences = ctx.getSharedPreferences(COLLECTION_USERS, Context.MODE_PRIVATE)

        val b = sharedPreferences.getBoolean(t, true)

        with(sharedPreferences.edit()) {
            putBoolean(t, false)
            commit()
        }

        return b
    }
}