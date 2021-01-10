package com.nrgbrainn.adolescentes.adapters

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.activities.QuestionActivity
import com.nrgbrainn.adolescentes.helpers.QUESTION
import com.nrgbrainn.adolescentes.helpers.REPLY
import com.nrgbrainn.adolescentes.helpers.REPLY_DATE
import com.nrgbrainn.adolescentes.models.Question
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