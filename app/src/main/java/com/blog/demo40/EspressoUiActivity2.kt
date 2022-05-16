package com.blog.demo40

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.blog.a.BaseActivity

class EspressoUiActivity2 : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_OK, Intent().apply { putExtra("tag", "zuo") })
        finish()
    }
}