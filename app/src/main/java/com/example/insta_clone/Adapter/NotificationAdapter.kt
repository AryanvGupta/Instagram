package com.example.insta_clone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.insta_clone.Model.Notification
import com.example.insta_clone.R

class NotificationAdapter(private val mContext: Context,
                          private val mNotification: List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView
        var profileImage: ImageView
        var text: TextView
        var userName: TextView

        init {
            postImage = itemView.findViewById(R.id.notification_post_image)
            userName = itemView.findViewById(R.id.username_notification)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            text = itemView.findViewById(R.id.comment_notification)
        }
    }
}