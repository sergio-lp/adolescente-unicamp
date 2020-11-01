package com.sergiolp.portaldoadolescente.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.adapters.UserAdapter
import com.sergiolp.portaldoadolescente.helpers.DatabaseHelper
import com.sergiolp.portaldoadolescente.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class RankingFragment : Fragment() {
    var listUsers = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_ranking, container, false)

        val rvUsers = v.findViewById<RecyclerView>(R.id.rv_users)
        val progressBar = v.findViewById<ProgressBar>(R.id.progress_bar)

        val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(context!!)!!)
        dbHelper.getUsers().get().addOnSuccessListener { response ->
            listUsers = response.toObjects(User::class.java)

            rvUsers.apply {
                adapter = UserAdapter(listUsers)
                layoutManager = LinearLayoutManager(context)
            }
            progressBar.visibility = View.GONE
            v.findViewById<ViewGroup>(R.id.root).visibility = View.VISIBLE

            GlobalScope.async {
                val thisUser = listUsers.find { it.id == dbHelper.getUserPrefId(context!!) }
                Log.e("TAG", "onCreateView: " + dbHelper.getUserPrefId(context!!) + "\\ " + dbHelper.getUserPrefScore(context!!) )
                if (thisUser?.score == 0) {
                    Snackbar.make(
                        v,
                        getString(R.string.warning_no_points),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                context!!,
                                R.color.colorPrimaryDark
                            )
                        )
                        .setDuration(5000)
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setAction(getString(R.string.ok)) { }
                        .show()
                }
            }.start()
        }.addOnFailureListener { e -> Log.e("TAG", "onCreateView: ERRO" + e.message ) }

        return v
    }
}