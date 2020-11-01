package com.sergiolp.portaldoadolescente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergiolp.portaldoadolescente.R
import com.sergiolp.portaldoadolescente.helpers.GlideApp
import com.sergiolp.portaldoadolescente.models.User

class UserAdapter(private val userList: MutableList<User>) :
    RecyclerView.Adapter<UserAdapter.UserVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        return UserVH(
            LayoutInflater.from(parent.context).inflate(R.layout.view_user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val user = userList[position]
        holder.setData(user)
    }

    class UserVH(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsername: TextView = view.findViewById(R.id.tv_username)
        private val tvScore: TextView = view.findViewById(R.id.tv_score)
        private val imgUser: ImageView = view.findViewById(R.id.img_user_pic)

        fun setData(u: User) {
            tvUsername.text = u.username
            tvScore.text = u.score.toString()

            if (u.photo_url != "null") {
                GlideApp.with(view)
                    .load(u.photo_url)
                    .into(imgUser)
            }
        }

    }
}