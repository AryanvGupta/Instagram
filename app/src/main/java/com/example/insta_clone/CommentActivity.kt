package com.example.insta_clone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CommentActivity : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()
    }

}