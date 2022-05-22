package com.blog.demo18

import android.animation.Animator
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.blog.R

/**
 * https://github.com/airbnb/lottie-android
 * http://airbnb.io/lottie/#/android
 * https://lottiefiles.com/
 */
class LottieActivity : AppCompatActivity() {

    private lateinit var lottieAnimaView: LottieAnimationView;
    private lateinit var lottieAnimaViewByUrl: LottieAnimationView

    private val url = "https://assets1.lottiefiles.com/packages/lf20_gygeywbl.json"
    private var cacheKey : String? = null;

    private lateinit var tvLoad: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lottie_layout)
        tvLoad = findViewById(R.id.tv_lottie_load);
        lottieAnimaView = findViewById(R.id.animationView)
        lottieAnimaViewByUrl = findViewById(R.id.animationViewByUrl)
        showLottieByAssetsJson()
    }

    fun playAnimaFromUrl() {
        tvLoad.visibility = View.VISIBLE
        lottieAnimaViewByUrl.visibility = View.VISIBLE
        lottieAnimaView.visibility = View.INVISIBLE
        if (lottieAnimaView.isAnimating) {lottieAnimaView.cancelAnimation()}

         lottieAnimaViewByUrl.setAnimationFromUrl(url, cacheKey)
//        LottieCompositionFactory.fromUrl(application, url, cacheKey)
//                .addListener { lottieAnimaViewByUrl.setComposition(it) }
        lottieAnimaViewByUrl.playAnimation()
        lottieAnimaViewByUrl.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                tvLoad.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
    }

    fun showLottieByAssetsJson() {
        tvLoad.visibility = View.INVISIBLE
        lottieAnimaViewByUrl.visibility = View.INVISIBLE
        lottieAnimaView.visibility = View.VISIBLE
        if (lottieAnimaViewByUrl.isAnimating) {lottieAnimaViewByUrl.cancelAnimation()}

        lottieAnimaView.playAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater;
        inflater.inflate(R.menu.lottie_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.assets_json -> {
                showLottieByAssetsJson()
                true
            }
            R.id.url_json -> {
                playAnimaFromUrl()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}