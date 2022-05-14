package com.blog.a.jni

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blog.a.R
import com.blog.a.cpp.HelloCMakeJni
import java.lang.StringBuilder

class JNISampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jni_show_layout)
        findViewById<TextView>(R.id.sample_text).text = showJNIStr()
    }

    fun showJNIStr() = StringBuilder().apply {
        /* hello 库已经引用了 libmyJniTest.so，所以当 hello.so 加载后，myJniTest 自动就会被关联*/
        append(HelloCMakeJni().stringFromCmakeJNI())
        append("\n")
        append(HelloJni().stringFromJNI())
    }.toString()
}