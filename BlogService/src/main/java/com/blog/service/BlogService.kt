package com.blog.service

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteCallbackList
import android.util.Log

open class BlogService : Service() {

    private var mInfo: BlogInfo? = null
    private var mInfo1: BlogInfo1? = null
    private val listeners = RemoteCallbackList<IBlogListener>()

    private val mBinder = object : IBlogManager.Stub() {

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            val check = checkCallingPermission("app.blog.service.permission")
            Log.d("zuo", "onTransact: check-$check, thread-${Thread.currentThread()}")

            if (check == PackageManager.PERMISSION_DENIED)  {
                return false
            }
            val packages = packageManager.getPackagesForUid(getCallingUid())
            if (false == packages?.first()?.startsWith("com.blog.a")) {
                return false
            }
            return super.onTransact(code, data, reply, flags)
        }

        override fun pullFromService(): String {
            return mInfo?.name ?: ""
        }
        override fun pushToService(info: BlogInfo?) {
            mInfo = info
            Log.d("zuo", "-> ${mInfo?.name}")

            for (i in 0 until listeners.beginBroadcast()) {
                val listener = listeners.getBroadcastItem(i)
                listener?.onPushSucc()
            }
        }
        override fun registerBlogListener(listener: IBlogListener?) {
            listeners.register(listener)
        }
        override fun unregisterBlogListener(listener: IBlogListener?) {
            listeners.unregister(listener)
        }

        override fun getVersionCode(): Int {
            return 0
        }
    }

    private val mBinder1 = object : IBlogManager1.Stub() {
        override fun pullFromService(): String {
            return mInfo1?.name ?: ""
        }

        override fun pushToService(info: AbstractBlogInfo?) {
            mInfo1 = info as? BlogInfo1
            Log.d("zuo", "-> ${mInfo1?.name}")

            for (i in 0 until listeners.beginBroadcast()) {
                val listener = listeners.getBroadcastItem(i)
                listener?.onPushSucc()
            }
        }

        override fun registerBlogListener(listener: IBlogListener?) {
            listeners.register(listener)
        }

        override fun unregisterBlogListener(listener: IBlogListener?) {
            listeners.unregister(listener)
        }

        override fun getVersionCode(): Int {
            return 1
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        val check = checkCallingPermission("app.blog.service.permission")
        Log.d("zuo", "onBind: check-$check, thread-${Thread.currentThread()}")

        val packages = packageManager.getPackagesForUid(IBlogManager.Stub.getCallingUid())
        Log.d("zuo", "onBind: package-${packages?.first()?.startsWith("com.blog.a")}")

        return when (intent?.getIntExtra("versionCode", -1)) {
            0 -> mBinder // IBlogManager.Stub()
            1 -> mBinder1 // IBlogManager1.Stub()
            else -> mBinder
        }
    }
}