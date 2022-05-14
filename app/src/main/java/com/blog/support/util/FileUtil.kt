@file:JvmName("FileUtil")
package com.blog.support.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun getTestDexDirectory(context: Context): String? {
    val dir = context.getExternalFilesDir("blog") ?: return null
    return dir.path
}

fun getTestDexOptimizedDirectory(context: Context): String? {
    val dir = getTestDexDirectory(context) ?: return null
    return dir + File.separator + "optimized" + File.separator + ".test.dex"
}

fun fileExists(path: String?): Boolean {
    if (null != path) {
        return File(path).exists()
    }
    return false;
}

fun copyAssetsFile(
    context: Context, assetsPath: String,
    newPathFile: String?
): Boolean {
    var inStream: InputStream? = null
    var fs: FileOutputStream? = null
    return try {
        val assetManager = context.assets
        inStream = assetManager.open(assetsPath)
        var byteRead = 0
        fs = FileOutputStream(newPathFile)
        val buffer = ByteArray(1024)
        while (inStream.read(buffer).also { byteRead = it } != -1) {
            fs.write(buffer, 0, byteRead)
        }
        inStream.close()
        true
    } catch (e: Exception) {
        false
    } finally {
        if (inStream != null) {
            try {
                inStream.close()
            } catch (ex: Exception) {
            }
            if (fs != null) {
                try {
                    fs.close()
                } catch (ex: Exception) {
                }
            }
        }
    }
}