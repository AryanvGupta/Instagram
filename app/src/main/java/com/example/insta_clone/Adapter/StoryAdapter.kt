package com.example.insta_clone.Adapter

import android.app.AlertDialog
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
import com.example.insta_clone.StoryActivity
import com.google.firebase.auth.FirebaseAuth
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
        }
        else {
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

        if (holder.adapterPosition !== 0) {
            seenStory(holder, story.getUserId())
        }
        if (holder.adapterPosition === 0) {
            myStories(holder.addStoryText!!, holder.storyPlusBtn!!, false)
        }


        holder.itemView.setOnClickListener {
            if (holder.adapterPosition === 0) {
                myStories(holder.addStoryText!!, holder.storyPlusBtn!!, true)
            }
            else {
                val intent = Intent(mContext, StoryActivity::class.java)
                intent.putExtra("userid", story.getUserId())
                mContext.startActivity(intent)
            }
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

    private fun myStories(textView: TextView, imageView: ImageView, click: Boolean) {
        val storyRef = FirebaseDatabase.getInstance().reference
            .child("Stories")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        storyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                var counter = 0
                val timeCurrent = System.currentTimeMillis()

                for (snapshot in pO.children) {
                    val story = snapshot.getValue(Story::class.java)

                    if (timeCurrent > story!!.getTimeStart() && timeCurrent < story!!.getTimeEnd()) {
                        counter++
                    }
                }

                if (click) {
                    if (counter > 0) {
                        val alertDialog = AlertDialog.Builder(mContext).create()

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story") {
                            dialogInterface, which ->

                            val intent = Intent(mContext, StoryActivity::class.java)
                            intent.putExtra("userid", FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)
                            alertDialog.dismiss()
                        }

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story") {
                                dialogInterface, which ->

                            val intent = Intent(mContext, AddStoryActivity::class.java)
                            intent.putExtra("userid", FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)
                            alertDialog.dismiss()
                        }

                        alertDialog.show()
                    }
                    else {
                        if (counter > 0) {
                            textView.text = "My Story"
                            imageView.visibility = View.GONE
                        }
                        else {
                            textView.text = "Add Story"
                            imageView.visibility = View.VISIBLE
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun seenStory(viewHolder: ViewHolder, userId: String) {
        val storyRef = FirebaseDatabase.getInstance().reference
            .child("Stories")
            .child(userId)

        storyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                var i = 0
                for (snapshot in pO.children) {
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance().currentUser!!.uid).exists()
                        && System.currentTimeMillis() < snapshot.getValue(Story::class.java)!!.getTimeEnd()) {
                        i += 1
                    }
                }

                if (i>0) {
                    viewHolder.storyImage!!.visibility = View.VISIBLE
                    viewHolder.storyImageSeen!!.visibility = View.GONE
                }
                else {
                    viewHolder.storyImage!!.visibility = View.GONE
                    viewHolder.storyImageSeen!!.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}