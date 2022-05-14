package com.blog.test

import android.os.Parcel
import com.blog.service.BlogInfo
import org.junit.Assert
import org.junit.Before
import org.junit.Test

const val NAME = "hello world"

class BlogInfoTest {

    private lateinit var blogInfo: BlogInfo

    @Before
    fun createBlogInfo() {
        blogInfo = BlogInfo()
    }

    @Test
    fun parcelableTest() {
        val parcel = Parcel.obtain()
        blogInfo.apply {
            name = NAME
            writeToParcel(parcel, describeContents())
        }
        parcel.setDataPosition(0)
        val p = BlogInfo.CREATOR.createFromParcel(parcel)
        Assert.assertEquals(p.name, NAME)
    }

}