package com.example.insta_clone.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insta_clone.AccountSettingsActivity
import com.example.insta_clone.Adapter.MyPostsAdapter
import com.example.insta_clone.Model.Post
import com.example.insta_clone.Model.User
import com.example.insta_clone.R
import com.example.insta_clone.ShowUsersActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var myPostsAdapter: MyPostsAdapter? = null

    var postListSaved: List<Post>? = null
    var myPostsAdapterSaved: MyPostsAdapter? = null
    var mySavedImg: List<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null){
            this.profileId = pref.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid) {
            view.edit_account_settings_btn.text = "Edit Profile"
        }
        else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()
        }

        // RecyclerView for Uploaded Posts
        var recyclerViewUplodedImages: RecyclerView
        recyclerViewUplodedImages = view.findViewById(R.id.recycler_view_uploaded_post)
        recyclerViewUplodedImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUplodedImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myPostsAdapter = context?.let { MyPostsAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUplodedImages.adapter = myPostsAdapter


        // RecyclerView for Saved Posts
        var recyclerViewSavedImages: RecyclerView
        recyclerViewSavedImages = view.findViewById(R.id.recycler_view_saved_post)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        postListSaved = ArrayList()
        myPostsAdapterSaved = context?.let { MyPostsAdapter(it, postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter = myPostsAdapterSaved


        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUplodedImages.visibility = View.VISIBLE

        var uploadedPostBtn: ImageButton
        uploadedPostBtn = view.findViewById(R.id.images_grid_view_btn)
        uploadedPostBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUplodedImages.visibility = View.VISIBLE
        }

        var savedPostBtn: ImageButton
        savedPostBtn = view.findViewById(R.id.images_save_btn)
        savedPostBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUplodedImages.visibility = View.GONE
        }

        view.total_followers.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }

        view.total_following.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }

        view.edit_account_settings_btn.setOnClickListener {
            val getButtonText = view.edit_account_settings_btn.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, AccountSettingsActivity::class.java))

                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(itl.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(itl.toString())
                            .setValue(true)
                    }

                    addNotification()
                }

                getButtonText == "Following" -> {
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(itl.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(itl.toString())
                            .removeValue()
                    }
                }
            }


        }

        getFollowers()
        getFollowing()
        userInfo()
        myPosts()
        getTotalNoOfPosts()
        mySaves()

        return view
    }

    private fun mySaves() {
        mySavedImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        (mySavedImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedPostData()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readSavedPostData() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    (postListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavedImg!!) {
                            if (post!!.getPostid() == key) {
                                (postListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }
                    myPostsAdapterSaved!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { itl ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(itl.toString())
                .child("Following")
        }

        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(profileId).exists()){
                        view?.edit_account_settings_btn?.text = "Following"
                    }
                    else {
                        view?.edit_account_settings_btn?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getFollowing() {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun myPosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in pO.children) {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId)) {
                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        myPostsAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
//                if (context != null) {
//                    return
//                }

                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text = user.getUsername()
                    view?.full_name_profile_frag?.text = user.getFullname()
                    view?.bio_profile_frag?.text = user.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    private fun getTotalNoOfPosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var postCounter = 0

                    for (snapshot in dataSnapshot.children) {
                        val post =  snapshot.getValue(Post::class.java)

                        if (post!!.getPublisher() == profileId) {
                            postCounter++
                        }
                    }
                    total_posts.text = " $postCounter"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addNotification() {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }

}