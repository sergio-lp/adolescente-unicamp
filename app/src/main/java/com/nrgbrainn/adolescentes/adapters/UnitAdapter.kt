package com.nrgbrainn.adolescentes.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.nrgbrainn.adolescentes.R
import com.nrgbrainn.adolescentes.activities.ContentActivity
import com.nrgbrainn.adolescentes.helpers.COLOR
import com.nrgbrainn.adolescentes.helpers.GlideApp
import com.nrgbrainn.adolescentes.helpers.UNIT_ID
import com.nrgbrainn.adolescentes.helpers.UNIT_TITLE
import com.nrgbrainn.adolescentes.models.Unit

class UnitAdapter(private val unitList: List<Unit>) :
    RecyclerView.Adapter<UnitAdapter.UnitVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitVH {
        return UnitVH(
            LayoutInflater.from(parent.context).inflate(R.layout.view_unit, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return unitList.size
    }

    override fun onBindViewHolder(holder: UnitVH, position: Int) {
        val unit = unitList[position]

        holder.tvTitle.text = unit.title

        val shapeBg = holder.imgIcon.background as RippleDrawable
        (shapeBg.findDrawableByLayerId(android.R.id.background) as GradientDrawable).setColor(
            Color.parseColor(
                unit.color
            )
        )

        val storageRef = FirebaseStorage.getInstance().getReference(unit.icon.toString())
        GlideApp.with(holder.tvTitle.context)
            .load(storageRef)
            .into(holder.imgIcon)

        holder.root.setOnClickListener {
            val i = Intent(holder.root.context, ContentActivity::class.java)
            i.putExtra(UNIT_ID, unit.id)
            i.putExtra(UNIT_TITLE, unit.title)
            i.putExtra(COLOR, unit.color)
            Log.e("TAG", "onBindViewHolder: " + unit.color)
            holder.root.context.startActivity(i)
        }
    }

    class UnitVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.vbtn_title)
        val imgIcon: ImageView = itemView.findViewById(R.id.vbtn_img)
        val root: View = itemView.findViewById(R.id.root)
    }
}