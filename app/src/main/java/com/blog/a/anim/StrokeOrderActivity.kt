package com.blog.a.anim

import android.os.Bundle
import com.blog.a.BaseActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 笔顺动画。
 *
 * <p>汉字的 svg 数据都在 assets/data 下，
 * 本示例仅展示"婵娟"两字的笔顺动画。
 */
class StrokeOrderActivity: BaseActivity() {

    var svgs: List<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        svgs = loadSvgFromAssets()
    }

    private fun loadSvgFromAssets(): List<String?> {
        val list: ArrayList<String?> = ArrayList()
        val files: Array<String?>?
        try {
            files = assets.list("data")
            if (null != files) {
                for (s in files) {
                    if (null == s) {
                        continue
                    }
                    list.add(loadSvgJson(s))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }

    private fun loadSvgJson(file: String): String? {
        var reader: BufferedReader? = null
        try {
            val inputStream = assets.open(file)
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            val entity = StringBuilder()

            while (reader.readLine().also { line = it } != null) {
                entity.append(line)
            }
            return entity.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

}