package com.blog.service

import android.os.Parcel

class BlogInfo1 : AbstractBlogInfo() {

    var name: String = "hello BlogInfo1"

    override fun readFromParcel(p: Parcel) {
        name = p.readString() ?: "name: null"
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.writeString(name)
    }
}