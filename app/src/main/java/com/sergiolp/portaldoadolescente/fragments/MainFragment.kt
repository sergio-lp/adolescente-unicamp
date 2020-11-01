package com.sergiolp.portaldoadolescente.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.firebase.FirebaseApp
import com.rd.draw.data.Orientation
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.adapters.UnitAdapter
import com.sergiolp.portaldoadolescente.helpers.DatabaseHelper
import com.sergiolp.portaldoadolescente.models.Unit

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)

        val rvUnits = v.findViewById<RecyclerView>(R.id.rv_units)
        val progressBar = v.findViewById<ProgressBar>(R.id.progress_bar)
        val root = v.findViewById<ViewGroup>(R.id.root)

        val dbHelper = DatabaseHelper(FirebaseApp.initializeApp(context!!)!!)

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

        }.addOnFailureListener { e -> Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
            Log.e(
                "TAG",
                "Login Activity - onActivityResult: " + e.message
            )
        }

        return v
    }

}