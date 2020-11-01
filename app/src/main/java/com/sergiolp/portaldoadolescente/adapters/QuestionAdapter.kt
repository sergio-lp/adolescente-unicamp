package com.sergiolp.portaldoadolescente.adapters

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.activities.QuestionActivity
import com.sergiolp.portaldoadolescente.helpers.QUESTION
import com.sergiolp.portaldoadolescente.helpers.REPLY
import com.sergiolp.portaldoadolescente.helpers.REPLY_DATE
import com.sergiolp.portaldoadolescente.models.Question
import kotlinx.android.synthetic.main.view_unit.view.*
import java.util.*

class QuestionAdapter(private val questList: MutableList<Question?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_QUESTION = 1
        private const val VIEW_TYPE_LOADING = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_QUESTION) {
            QuestionVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_question, parent, false)
            )
        } else {
            LoadingVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_loading, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return questList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val q = questList[position]
        if (q != null) {
            (holder as QuestionVH).setData(q)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (questList[position] != null) {
            VIEW_TYPE_QUESTION
        } else {
            VIEW_TYPE_LOADING
        }
    }

    class QuestionVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvQuestion: TextView = v.findViewById(R.id.tv_question)
        private val tvReply: TextView = v.findViewById(R.id.tv_reply)
        private val tvDate: TextView = v.findViewById(R.id.tv_date)
        private val v: View = v.findViewById(R.id.question_root)

        fun setData(q: Question) {
            tvQuestion.text = q.question
            if (q.reply != null) {
                tvReply.text = Html.fromHtml("<b>Resposta curta: </b>" + q.short_reply)
            }
            val d = GregorianCalendar.getInstance()
            d.time = q.date!!.toDate()
            val month =
                if (d.get(Calendar.MONTH) < 10) "0" + (d.get(Calendar.MONTH) + 1).toString() else (d.get(
                    Calendar.MONTH
                ) + 1).toString()
            val day =
                if (d.get(Calendar.DAY_OF_MONTH) < 10) "0" + d.get(Calendar.DAY_OF_MONTH) else (d.get(
                    Calendar.DAY_OF_MONTH
                ).toString())
            tvDate.text = String.format(
                tvDate.context.getString(R.string.question_date),
                day,
                month,
                d.get(Calendar.YEAR)
            )

            v.setOnClickListener {
                val i = Intent(
                    tvQuestion.context,
                    QuestionActivity::class.java
                )
                i.putExtra(QUESTION, q.question.toString())
                val text: CharSequence = this.tvDate.text
                i.putExtra(REPLY_DATE, text as String)
                i.putExtra(REPLY, q.reply)
                tvQuestion.context.startActivity(i)
            }
        }
    }

    class LoadingVH(v: View) : RecyclerView.ViewHolder(v)
}