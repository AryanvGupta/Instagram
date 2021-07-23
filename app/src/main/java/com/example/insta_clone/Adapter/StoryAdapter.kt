package com.example.insta_clone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.insta_clone.Model.Story
import com.example.insta_clone.R
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


    }

}