package com.blog.demo41

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.blog.AbstractLoggerActivity
import com.blog.R
import com.blog.demo39.TAG
import com.blog.support.logger.Logger
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class StrokeOrderActivity : AbstractLoggerActivity() {

    var svgSix: String? = null
    var svgOne: String? = null
    lateinit var strokeOrderView1: StrokeOrderView
    lateinit var strokeOrderView2: StrokeOrderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stroke_order_layout)
        initLoggerLayout()

        strokeOrderView1 = findViewById(R.id.stroke_order_view1)
        strokeOrderView2 = findViewById(R.id.stroke_order_view2)

        findViewById<Button>(R.id.btn_load_svg_six).setOnClickListener {
            val name = "六.json" // 需要将 svg.json 放在 assets 或特定路径下
            svgSix = loadSvgFromAssets(name)
            showTips("加载$name ->$svgSix")
            svgSix?.let {
                showTips("start draw -> $name")
                strokeOrderView1.setStrokesBySvg(it)
            }
        }

        findViewById<Button>(R.id.btn_load_svg_one).setOnClickListener {
            val name = "一.json"
            svgOne = loadSvgFromAssets(name)
            showTips("加载$name ->$svgOne")
            svgOne?.let {
                showTips("start draw -> $name")
                strokeOrderView2.setStrokesBySvg(it)
            }
        }

    }

    private fun loadSvgFromAssets(name: String): String? {
        try {
            assets.list("data")?.let {
                for (s in it) {
                    if (name == s) {
                        Log.d("zuo", "svgName-> $s")
                        return loadSvgJson("data/$s") ?: "NULL"
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun loadSvgJson(file: String): String? {
        var reader: BufferedReader? = null
        var inputStreamReader: InputStreamReader? = null
        try {
            val inputStream: InputStream = assets.open(file)
            inputStreamReader = InputStreamReader(inputStream)
            reader = BufferedReader(inputStreamReader)
            var line: String?
            val entity = java.lang.StringBuilder()
            while (reader.readLine().also { line = it } != null) {
                entity.append(line)
            }
            return entity.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStreamReader?.close()
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun showTips(str: String) {
        Log.d(TAG, str)
        Logger.i(TAG, str)
    }
}