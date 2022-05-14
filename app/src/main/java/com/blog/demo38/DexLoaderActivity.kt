package com.blog.demo38

import android.os.Bundle
import android.widget.Button
import com.blog.AbstractLoggerActivity
import com.blog.a.R
import com.blog.demo39.TAG
import com.blog.support.util.fileExists
import com.blog.support.util.getTestDexDirectory
import com.blog.support.util.getTestDexOptimizedDirectory
import com.blog.support.logger.Logger
import com.blog.support.util.copyAssetsFile
import dalvik.system.DexClassLoader
import java.io.File

class DexLoaderActivity : AbstractLoggerActivity() {

    private var dexPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dex_loader_layout)
        initLoggerLayout()
        initListener()

        Logger.i(TAG, "default dex path= ${getTestDexDirectory(applicationContext)}")

        if (!isValidLocalDexFile()) {
            Logger.e(TAG, "local dex not exists!")
        }
    }

    private fun isValidLocalDexFile() : Boolean {
        dexPath = getTestDexDirectory(applicationContext) + File.separator + "test.dex"
        return fileExists(dexPath)
    }

    private fun initListener() {
        // 拷贝 assets/test.dex 到设备 $dexPath
        findViewById<Button>(R.id.btn_copy_dex).setOnClickListener {
            Logger.i(TAG, "start copy to $dexPath")
            copyAssetsFile(applicationContext, "test.dex", dexPath)
            Logger.i(TAG, "finish copy")
        }

        // 通过 DexClassLoader，可去掉 base.dex 的 Test.class
        findViewById<Button>(R.id.btn_dex_class_loader).setOnClickListener {
            Logger.i(TAG, "start load dex:")
            loadDexByDexClassLoader()
        }

        // class.forName
        findViewById<Button>(R.id.btn_class_forName).setOnClickListener {
            Logger.i(TAG, "start forName:")
            loadDexByDexClassLoader2()
        }

        // 调整 dex 加载顺序
        findViewById<Button>(R.id.btn_dex_priority).setOnClickListener {
            Logger.i(TAG, "please reopen App if not First!")

            Logger.i(TAG, "-dex priority-")
            val loader = DexClassLoader(dexPath
                , getTestDexOptimizedDirectory(applicationContext)
                ,null, classLoader)

            install(classLoader, loader)
            printDexElements(classLoader)
        }
    }

    private fun loadDexByDexClassLoader2() {
        val c = Class.forName("com.blog.demo38.Test") as Class<*>
        val m = c.getDeclaredMethod("getTestStr") ?: null
        Logger.i(TAG, "getTestStr()= ${m?.invoke(c.newInstance()).toString()}")
    }

    private fun loadDexByDexClassLoader() {
        try {
            val loader = DexClassLoader(dexPath
                , getTestDexOptimizedDirectory(applicationContext)
                , null, classLoader)
            val c = loader.loadClass("com.blog.demo38.Test") as Class<*>
            val m = c.getDeclaredMethod("getTestStr") ?: null
            Logger.i(TAG, "getTestStr()= ${m?.invoke(c.newInstance()).toString()}")
        } catch (ex: java.lang.Exception) {
            Logger.e(TAG, ex.message)
        }
    }
}