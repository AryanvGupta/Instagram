package com.example.insta_clone.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.insta_clone.AccountSettingsActivity
import com.example.insta_clone.Model.Post
import com.example.insta_clone.Model.User
import com.example.insta_clone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*



/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null


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

        return view
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

}