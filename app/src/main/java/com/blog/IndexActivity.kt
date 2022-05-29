package com.blog

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blog.demo18.LottieActivity
import com.blog.demo16.VectorAnimActivity
import com.blog.demo11.ViewDragActivity
import com.blog.jni.JNISampleActivity
import com.blog.location.LocationActivity
import com.blog.demo10.NestedScrollActivity
import com.blog.demo17.IndexActivity
import com.blog.demo38.DexLoaderActivity
import com.blog.demo39.BlogServiceActivity
import com.blog.demo40.EspressoUiActivity
import com.blog.demo41.StrokeOrderActivity

class IndexActivity : BaseActivity(), SimpleAdapter.ViewHolderListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index_layout)

        findViewById<RecyclerView>(R.id.rv_list).also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = SimpleAdapter(ITEMS, this)
        }
    }

    override fun onItemClicked(view: View?, adapterPosition: Int) {
        val intent = Intent(this, CLASS[adapterPosition]).also {
            if (EspressoUiActivity::class.java == CLASS[adapterPosition]) {
                it.putExtra("tag", "zuo")
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Toast.makeText(this, getString(R.string.txt_elastic_recycler_view), Toast.LENGTH_SHORT).show()
    }
}

val ITEMS = arrayOf(
    "简单定位",
    "demo10: nested scroll example",
    "demo11: ViewDragHelper",
    "demo12: elastic RecyclerView",
    "demo16: AnimatedVectorDrawable",
    "demo17: Activity Transition Animation",
    "demo18: Lottie Animation",
    "demo19: Jni and build so",
    "demo38: DexClassLoader",
    "demo39: AIDL",
    "demo40: Espresso UI Test",
    "demo41: Stroke Order Animation"
)

val CLASS = arrayOf(
    LocationActivity::class.java,
    NestedScrollActivity::class.java,
    ViewDragActivity::class.java,
    com.blog.IndexActivity::class.java,
    VectorAnimActivity::class.java,
    IndexActivity::class.java,
    LottieActivity::class.java,
    JNISampleActivity::class.java,
    DexLoaderActivity::class.java,
    BlogServiceActivity::class.java,
    EspressoUiActivity::class.java,
    StrokeOrderActivity::class.java
)