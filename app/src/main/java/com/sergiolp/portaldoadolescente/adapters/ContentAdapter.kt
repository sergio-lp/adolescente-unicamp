package com.sergiolp.portaldoadolescente.adapters

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.activities.QuizActivity
import com.sergiolp.portaldoadolescente.activities.SiteActivity
import com.sergiolp.portaldoadolescente.helpers.*
import com.sergiolp.portaldoadolescente.models.Content

class ContentAdapter(private val contentList: List<Content>) :
    RecyclerView.Adapter<ContentAdapter.ContentVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentVH {
        return ContentVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_card_topic, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun onBindViewHolder(holder: ContentVH, position: Int) {
        val content: Content = contentList[position]
        holder.tvTitle.text = content.title
        holder.tvSubtitle.text = content.subtitle

        val matrix = ColorMatrix()
        val statusImg: Drawable?

        when (content.status) {
            Content.STATUS_DONE -> {
                statusImg = holder.img.context.getDrawable(R.drawable.ic_baseline_check_24)
                holder.root.setCardBackgroundColor(Color.parseColor(content.color))
                holder.root.setOnClickListener {
                    Toast.makeText(
                        holder.root.context,
                        String.format(
                            holder.root.context.getString(R.string.content_done),
                            content.title
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                holder.img.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(holder.root.context, R.color.gold),
                        PorterDuff.Mode.MULTIPLY
                    )
                holder.root.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.root.context,
                        R.color.gold
                    )
                )
                holder.imgStatus.circleColor = ContextCompat.getColor(holder.img.context, R.color.correct_color)
                holder.imgStatus.borderColor = ContextCompat.getColor(holder.img.context, R.color.white)

            }
            Content.STATUS_UNLOCKED -> {
                holder.root.setCardBackgroundColor(Color.parseColor(content.color))
                holder.root.setOnClickListener {
                    val i = Intent(holder.tvSubtitle.context, SiteActivity::class.java)
                    i.putExtra(UNIT_ID, content.unitId)
                    i.putExtra(CONTENT_ID, content.id.toString())
                    i.putExtra(CONTENT_TITLE, content.title)
                    i.putExtra(CONTENT_URL, content.url)
                    i.putExtra(USER_ID, content.userId.toString())
                    holder.root.context.startActivity(i)
                }
                statusImg = null
                matrix.setSaturation(1f)
                val filter = ColorMatrixColorFilter(matrix)
                holder.img.colorFilter = filter
                holder.imgStatus.visibility = View.GONE
            }
            else -> {
                statusImg = holder.img.context.getDrawable(R.drawable.ic_baseline_lock_24)
                holder.root.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.img.context,
                        R.color.disabledColor
                    )
                )
                holder.root.setOnClickListener {
                    Toast.makeText(
                        holder.root.context,
                        String.format(
                            holder.root.context.getString(R.string.content_locked),
                            contentList[position - 1].title
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                matrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(matrix)
                holder.img.colorFilter = filter
                holder.imgStatus.circleColor = ContextCompat.getColor(holder.img.context, R.color.white)
                holder.imgStatus.borderColor = ContextCompat.getColor(holder.img.context, R.color.gray)
            }
        }

        val storageRef = FirebaseStorage.getInstance().getReference(content.image.toString())
        Glide.with(holder.img.context)
            .load(storageRef)
            .into(holder.img)

        GlideApp.with(holder.img.context)
            .load(statusImg)
            .into(holder.imgStatus)

    }

    class ContentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.vcard_title)
        val tvSubtitle: TextView = itemView.findViewById(R.id.vcard_subtitle)
        val img: ImageView = itemView.findViewById(R.id.vcard_img)
        val imgStatus: CircularImageView = itemView.findViewById(R.id.img_done)
        val root: CardView = itemView.findViewById(R.id.vcard_root)

    }

}