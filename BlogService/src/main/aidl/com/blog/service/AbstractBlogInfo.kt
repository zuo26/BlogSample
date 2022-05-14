package com.blog.service

import android.os.Parcel
import android.os.Parcelable
import java.lang.Exception

abstract class AbstractBlogInfo() : Parcelable {

    abstract fun readFromParcel(p: Parcel);

    companion object CREATOR : Parcelable.Creator<AbstractBlogInfo> {
        override fun createFromParcel(parcel: Parcel): AbstractBlogInfo? {
            val className = parcel.readString() ?: return null
            return try {
                val subClass = Class.forName(className);
                val blogInfo = subClass.newInstance() as AbstractBlogInfo
                blogInfo.readFromParcel(parcel)
                blogInfo
            } catch (ex: Exception) {
                null
            }
        }

        override fun newArray(size: Int): Array<AbstractBlogInfo?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.javaClass.name)
    }

    override fun describeContents(): Int {
        return 0
    }
}