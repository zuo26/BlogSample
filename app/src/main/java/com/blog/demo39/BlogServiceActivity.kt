package com.blog.demo39

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.View
import com.blog.R
import com.blog.AbstractLoggerActivity
import com.blog.service.*
import com.blog.support.logger.Logger

const val TAG = "zuo"

class BlogServiceActivity: AbstractLoggerActivity() {

    private var mBinder: IBlogManager? = null
    private var mBinder1: IBlogManager1? = null
    private var isBind: Boolean = false
    private var versionCode: Int? = null;

    private var blogListener = object : IBlogListener.Stub() {
        override fun onPushSucc() {
            showTips("push data success")
        }
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            showTips("onServiceConnected, " + Thread.currentThread())

            try {
                mBinder = IBlogManager.Stub.asInterface(service) as IBlogManager
                versionCode = mBinder?.versionCode
            } catch (ex: SecurityException) {
                mBinder1 = IBlogManager1.Stub.asInterface(service) as IBlogManager1
                versionCode = mBinder1?.versionCode
            }
            when (versionCode) {
                0 -> mBinder?.registerBlogListener(blogListener)
                1 -> mBinder1?.registerBlogListener(blogListener)
            }
            isBind = true

            service?.linkToDeath({
                showTips("binder dead!")
            }, 0)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            showTips("onServiceDisconnected, " + Thread.currentThread())
            when (versionCode) {
                0 -> mBinder?.unregisterBlogListener(blogListener)
                1 -> mBinder1?.unregisterBlogListener(blogListener)
            }
            mBinder = null
            isBind = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_service_layout)
        initLoggerLayout()
        showTips("please open service app!")
    }

    fun btnBindService(v: View) { // 点击按钮，绑定Service服务
        val intent = Intent("android.intent.action.BlogService")
        intent.setPackage("com.blog.service")
        //intent.component = ComponentName("com.blog.service", "com.blog.service.BlogService")
        intent.putExtra("versionCode", 1)
        bindService(intent, mConnection, BIND_AUTO_CREATE)
    }

    fun btnUnbindService(v: View) {
        unbindService(mConnection)
        isBind = false
        showTips("unbind service")
    }

    fun btnPullData(v: View) { // 点击按钮，执行aidl文件内的接口
        if (!isBind) {
            showTips("no bind service")
            return
        }
        try {
            when (versionCode) {
                0 -> mBinder?.pullFromService()?.let { showTips("pull from service-> $it") }
                1 -> mBinder1?.pullFromService()?.let { showTips("pull from service-> $it") }
            }
        } catch (ex: RemoteException) {
            showTips("${ex.message}")
        }
    }

    fun btnPushData(v: View) {
        if (!isBind) {
            showTips("no bind service")
            return
        }
        try {
            when (versionCode) {
                0 -> mBinder?.pushToService(BlogInfo().apply { name = "{data by v0}" })
                1 -> mBinder1?.pushToService(BlogInfo1().apply { name = "{data by v1}" })
            }
        } catch (ex: RemoteException) {
            showTips("${ex.message}")
        }
    }

    private fun showTips(str: String) {
        Log.d(TAG, str)
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        Logger.i(TAG, str)
    }
}