package com.blog

import android.os.Bundle
import com.blog.a.BaseActivity
import com.blog.a.R
import com.blog.support.logger.LogFragment
import com.blog.support.logger.LogWrapper
import com.blog.support.logger.Logger
import com.blog.support.logger.MessageOnlyLogFilter

abstract class AbstractLoggerActivity : BaseActivity() {

    private var logFragment: LogFragment? = null
    private var msgFilter: MessageOnlyLogFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeLogging()
    }

    fun initLoggerLayout() {
        logFragment = supportFragmentManager.findFragmentById(R.id.log_fragment) as LogFragment?
        msgFilter?.next = logFragment?.logView
    }

    /** Set up targets to receive log data  */
    private fun initializeLogging() {
        val logWrapper = LogWrapper()
        Logger.setLogNode(logWrapper)

        // Filter strips out everything except the message text.
        msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter
    }
}