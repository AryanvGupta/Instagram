package com.example.insta_clone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.insta_clone.AddStoryActivity
import com.example.insta_clone.Model.Story
import com.example.insta_clone.Model.User
import com.example.insta_clone.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter (private val mContext: Context,
                    private val mStory: List<Story>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        //StoryItem
        var storyImageSeen: CircleImageView? = null
        var storyImage: CircleImageView? = null
        var storyUsername: TextView? = null

        //AddStoryItem
        var storyPlusBtn: ImageView? = null
        var addStoryText: TextView? = null

        init {
            storyImageSeen = itemView.findViewById(R.id.story_image_seen)
            storyImage = itemView.findViewById(R.id.story_image)
            storyUsername = itemView.findViewById(R.id.story_username)

            storyPlusBtn = itemView.findViewById(R.id.story_add)
            addStoryText = itemView.findViewById(R.id.add_story_text)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return 0 //AddStoryItem
        return 1 //StoryItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == 0) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item_layout, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.story_item_layout, parent, false)
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = mStory[position]

        userInfo(holder, story.getUserId(), position)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, AddStoryActivity::class.java)
            intent.putExtra("userid", story.getUserId())
            mContext.startActivity(intent)
        }
    }

    private fun userInfo(viewHolder: ViewHolder, userId: String, position: Int) {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(viewHolder?.storyImage)

                    if (position != 0) {
                        Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(viewHolder?.storyImageSeen)
                        viewHolder.storyUsername!!.text = user.getUsername()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}