package com.blog

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity

open class BaseDensityActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDensity();
        supportActionBar?.hide()
    }

    /* @TargetApi(17)
    override fun getResources(): Resources {
        val resources = super.getResources();
        val configContext = createConfigurationContext(resources.configuration)

        return configContext.resources.apply {
            Log.e("tag", "scale: " + configuration.fontScale);
            configuration.fontScale = 1.0f
            displayMetrics.scaledDensity = displayMetrics.density * configuration.fontScale
        }

        /* return super.getResources().apply {
            configuration.fontScale = 1f
            updateConfiguration(configuration, displayMetrics)
        } */

    } */

    private fun setDensity() {
        val systemMetrics = getSystemMetrics()
        val scale = 1.0f // 根据需求定义系数
        with(resources.displayMetrics) {
            density = systemMetrics.density * scale
            scaledDensity = systemMetrics.density * scale
            densityDpi = (systemMetrics.densityDpi * scale).toInt()
        }
    }

    private fun getSystemMetrics(): DisplayMetrics {
        return applicationContext.resources.displayMetrics;
    }

    open fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.applicationContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}