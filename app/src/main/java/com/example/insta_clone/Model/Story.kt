package com.example.insta_clone.Model

class Story {

    private var imageurl: String = ""
    private var timestart: Long = 0
    private var timeend: Long = 0
    private var storyid: String = ""
    private var userid: String = ""

    constructor()
    constructor(imageurl: String, timestart: Long, timeend: Long, storyid: String, userid: String) {
        this.imageurl = imageurl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }

    fun getImageUrl(): String{
        return imageurl
    }

    fun setImageUrl(imageurl: String) {
        this.imageurl = imageurl
    }

    fun getTimeStart(): Long{
        return timestart
    }

    fun setTimeStart(timestart: Long) {
        this.timestart = timestart
    }

    fun getTimeEnd(): Long{
        return timeend
    }

    fun setTimeEnd(timeend: Long) {
        this.timeend = timeend
    }

    fun getStoryId(): String{
        return storyid
    }

    fun setStoryId(storyid: String) {
        this.storyid = storyid
    }

    fun getUserId(): String{
        return userid
    }

    fun setUserId(userid: String) {
        this.userid = userid
    }


}