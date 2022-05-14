package com.blog.service

import android.os.Parcel
import android.os.Parcelable

open class BlogInfo() : Parcelable {
    var name: String? = "hello BlogInfo"

    companion object CREATOR : Parcelable.Creator<BlogInfo> {
        override fun createFromParcel(parcel: Parcel): BlogInfo {
            return BlogInfo(parcel)
        }
        override fun newArray(size: Int): Array<BlogInfo?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
    }
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun readFromParcel(p: Parcel) {
        name = p.readString();
    }
}