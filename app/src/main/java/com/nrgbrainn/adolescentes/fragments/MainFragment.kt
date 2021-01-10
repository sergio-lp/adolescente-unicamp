package com.nrgbrainn.adolescentes.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.adapters.UnitAdapter
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.models.Unit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)

        val rvUnits = v.findViewById<RecyclerView>(R.id.rv_units)
        val progressBar = v.findViewById<ProgressBar>(R.id.progress_bar)
        val root = v.findViewById<ViewGroup>(R.id.root)

        val dbHelper = DatabaseHelper(FirebaseApp.getInstance())

        val manager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
        manager.alignItems = AlignItems.CENTER
        manager.justifyContent = JustifyContent.SPACE_EVENLY

        //Getting Units from Firebase
        dbHelper.collectionUnits.get().addOnSuccessListener { response ->
            val unitList = response.toObjects(Unit::class.java)

            rvUnits.apply {
                adapter = UnitAdapter(unitList)
                layoutManager = manager
            }

            progressBar.visibility = View.GONE
            root.visibility = View.VISIBLE

            dbHelper.finishDB()

        }.addOnFailureListener { e ->
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "TAG",
                "Login Activity - onActivityResult: " + e.message
            )
            dbHelper.finishDB()
        }

        return v
    }

}