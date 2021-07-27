package com.example.insta_clone

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.insta_clone.Model.Story
import com.example.insta_clone.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_story.*

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    var currentUserId: String = ""
    var userId: String = ""

    var imagesList: List<String>? = null
    var storyIDsList: List<String>? = null

    var storiesProgressView: StoriesProgressView? = null

    var counter = 0

    var pressTime = 0L
    var limit = 500L

    private val onTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView!!.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        userId = intent.getStringExtra("userId").toString()

        storiesProgressView = findViewById(R.id.stories_progress)

        layout_seen.visibility = View.GONE
        story_delete.visibility = View.GONE

        if (userId == currentUserId) {
            layout_seen.visibility = View.VISIBLE
            story_delete.visibility = View.VISIBLE
        }

        getStories(userId!!)
        userInfo(userId!!)

        val reverse: View = findViewById(R.id.reverse)
        reverse.setOnClickListener { storiesProgressView!!.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        val skip: View = findViewById(R.id.skip)
        skip.setOnClickListener { storiesProgressView!!.skip() }
        skip.setOnTouchListener(onTouchListener)

        seen_number.setOnClickListener {
            val intent = Intent(this@StoryActivity, ShowUsersActivity::class.java)
            intent.putExtra("id", userId)
            intent.putExtra("storyid", storyIDsList!![counter])
            intent.putExtra("title", "views")
            startActivity(intent)
        }

        story_delete.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().reference
                .child("Stories")
                .child(userId!!)
                .child(storyIDsList!![counter])

            ref.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@StoryActivity, "Deleted...", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun getStories(userId: String) {
        imagesList = ArrayList()
        storyIDsList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference
            .child("Stories")
            .child(userId!!)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                (imagesList as ArrayList<String>).clear()
                (storyIDsList as ArrayList<String>).clear()

                for (snapshot in pO.children) {
                    val story: Story? = snapshot.getValue<Story>(Story::class.java)
                    val timeCurrent = System.currentTimeMillis()

                    if (timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd()) {
                        (imagesList as ArrayList<String>).add(story.getImageUrl())
                        (storyIDsList as ArrayList<String>).add(story.getStoryId())
                    }
                }

                storiesProgressView!!.setStoriesCount((imagesList as ArrayList<String>).size)
                storiesProgressView!!.setStoryDuration(6000L)
                storiesProgressView!!.setStoriesListener(this@StoryActivity)
                storiesProgressView!!.startStories(counter)
                Picasso.get().load(imagesList!![counter]).placeholder(R.drawable.profile).into(image_story)

                addViewToStory(storyIDsList!!.get(counter))
                seenNumber(storyIDsList!!.get(counter))
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun userInfo(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(story_profile_image)

                    story_username.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addViewToStory(storyId: String) {
        FirebaseDatabase.getInstance().reference
            .child("Stories")
            .child(userId!!)
            .child(storyId)
            .child("views")
            .child(currentUserId)
            .setValue(true)
    }

    private fun seenNumber(storyId: String) {
        val ref = FirebaseDatabase.getInstance().reference
            .child("Stories")
            .child(userId!!)
            .child(storyId)
            .child("views")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                seen_number.text = "" + pO.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onNext() {
        Picasso.get().load(imagesList!![++counter]).placeholder(R.drawable.profile).into(image_story)

        addViewToStory(storyIDsList!![counter])
        seenNumber(storyIDsList!![counter])
    }

    override fun onPrev() {
        if (counter-1 < 0)
            return
        Picasso.get().load(imagesList!![--counter]).placeholder(R.drawable.profile).into(image_story)

        seenNumber(storyIDsList!![counter])
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesProgressView!!.destroy()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView!!.pause()
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView!!.resume()
    }
}