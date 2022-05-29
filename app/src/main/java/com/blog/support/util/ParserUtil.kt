@file:JvmName("ParserUtil")
package com.blog.support.util

import android.graphics.Path
import android.graphics.Point
import org.json.JSONObject

fun parseSvgJson(json: String, list: ArrayList<String>, medians: ArrayList<Path>, points: ArrayList<Point>) {
    val obj = JSONObject(json)
    val array = obj.getJSONArray("strokes")
    for (i in 0 until array.length()) {
        list.add(array.getString(i))
    }
    val array2 = obj.getJSONArray("medians")
    for (i in 0 until array2.length()) {
        val array3 = array2.getJSONArray(i)
        val path = Path()
        for (j in 0 until array3.length()) {
            val array4 = array3.getJSONArray(j)
            val x = array4.getInt(0).toFloat()
            val y = array4.getInt(1).toFloat()
            if (0 == j) {
                path.moveTo(x, y)
                points.add(Point(x.toInt(), y.toInt())) // 起笔骨干点
            } else {
                path.lineTo(x, y)
            }
            if (array3.length() - 1 == j) { // 落笔骨干点
                points.add(Point(x.toInt(), y.toInt()))
            }
        }
        medians.add(path)
    }
}