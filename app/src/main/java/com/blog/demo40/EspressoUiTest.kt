package com.blog.demo40

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blog.a.BaseActivity
import com.blog.a.R

class EspressoUiTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espresso_layout)

        test1(findViewById(R.id.btn_test1), findViewById(R.id.tv_show_test1))
    }

    private fun test1(btn: Button, tv: TextView) {
        tv.text = getString(R.string.txt_hello_world)
        btn.setOnClickListener {
            tv.text = getString(R.string.txt_espresso)
        }
    }
}