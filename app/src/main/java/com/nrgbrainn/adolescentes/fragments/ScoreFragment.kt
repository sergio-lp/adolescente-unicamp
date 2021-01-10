package com.nrgbrainn.adolescentes.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.helpers.DatabaseHelper
import com.nrgbrainn.adolescentes.helpers.USER_SCORE
import kotlinx.android.synthetic.main.fragment_score.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ScoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_score, container, false)

        val progressBar = v.findViewById<ProgressBar>(R.id.progress_bar)

        CoroutineScope(Dispatchers.Main).async {
            val score = context?.getSharedPreferences("users", Context.MODE_PRIVATE)
                ?.getInt(USER_SCORE, 0)

            tv_score.text = "$score coroas"
            if (score!!.toInt() > 0) {
                tv_desc.text = getString(R.string.parab_ns_continue_se_esfor_ando)
            } else {
                tv_desc.text = "Que tal responder um quiz para ganhar algumas?"
            }

            progressBar.visibility = View.GONE
            root.visibility = View.VISIBLE
        }.start()
        return v
    }
}