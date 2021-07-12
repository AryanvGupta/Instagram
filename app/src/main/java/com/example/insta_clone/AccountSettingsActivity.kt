package com.example.insta_clone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.insta_clone.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked"){

            }
            else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {
        if (full_name_profile_frag.text.toString() == "" || user_name_profile_frag.text.toString() == ""){
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_LONG).show()
        }
        else {
            val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
            val userMap = HashMap<String, Any>()

            userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
            userMap["username"] = user_name_profile_frag.text.toString().toLowerCase()
            userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()

            usersRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this, "Account Info. has been updated successfully", Toast.LENGTH_LONG).show()

            val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }



    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_view_profile_frag)
                    user_name_profile_frag.setText(user.getUsername())
                    full_name_profile_frag?.setText(user.getFullname())
                    bio_profile_frag?.setText(user.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}