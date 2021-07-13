package com.example.insta_clone.Model

class Post {
    private var postid: String = ""
    private var postimage: String = ""
    private var description: String = ""
    private var publisher: String = ""

    constructor()
    constructor(postid: String, postimage: String, description: String, publisher: String) {
        this.postid = postid
        this.postimage = postimage
        this.description = description
        this.publisher = publisher
    }

    fun getPostid(): String {
        return postid
    }

    fun setPostid(postid: String) {
        this.postid = postid
    }

    fun getPostimage(): String {
        return postimage
    }

    fun setPostimage(postimage: String) {
        this.postimage = postimage
    }

    fun getDescription(): String {
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getPublisher(): String {
        return publisher
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

}