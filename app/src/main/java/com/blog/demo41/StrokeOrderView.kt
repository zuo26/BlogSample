package com.blog.demo41

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.PathParser
import com.blog.support.util.parseSvgJson


const val SVG_STROKE_WIDTH = 1024F
const val SVG_STROKE_HEIGHT = 1024F

class StrokeOrderView : View {

    private val strokePaths = ArrayList<Path>()
    private val medians = ArrayList<Path>()
    private val strokePaint = Paint()
    private val medianPaint = Paint()
    private var medianMeasures = ArrayList<PathMeasure>()
    private val tempPath = Path()
    private var progress = 0F
    private var currIndex = 0
    private val points = ArrayList<Point>()

    var srcBmp: Bitmap? = null
    var srcCanvas: Canvas? = null
    var srcPaint = Paint()

    var dstBmp: Bitmap? = null
    var dstCanvas: Canvas? = null
    var dstPaint = Paint()

    var clearMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    var srcMode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    var porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(ctx, attrs, defStyleAttr)

    init {
        strokePaint.isAntiAlias = true
        strokePaint.style = Paint.Style.FILL
        strokePaint.color = Color.RED

        medianPaint.isAntiAlias = true
        medianPaint.style = Paint.Style.FILL
        medianPaint.color = Color.BLACK

        srcPaint.isAntiAlias = true
        srcPaint.strokeWidth = 100f
        srcPaint.style = Paint.Style.STROKE
        srcPaint.color = Color.BLACK

        dstPaint.isAntiAlias = true
        dstPaint.style = Paint.Style.FILL
        dstPaint.color = Color.BLACK
    }

    fun setStrokesBySvg(svgJson: String) {
        strokePaths.clear()
        medians.clear()
        medianMeasures.clear()
        points.clear()
        val strokes = ArrayList<String>()
        parseSvgJson(svgJson, strokes, medians, points)
        for (stroke in strokes) {
            strokePaths.add(PathParser.createPathFromPathData(stroke))
        }
        for (median in medians) {
            medianMeasures.add(PathMeasure(median, false))
        }

        startAnimation() // 开始动画
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (0 == currIndex) {
            dstPaint.xfermode = clearMode // 清除模式
            dstCanvas?.drawPaint(dstPaint)
            srcPaint.xfermode = clearMode
            srcCanvas?.drawPaint(srcPaint)
        }

        val xTmp = measuredWidth / SVG_STROKE_WIDTH // 缩放比例
        val yTmp = measuredHeight / SVG_STROKE_HEIGHT

        val restore = canvas.save()
        // 1. 沿着 y 轴旋转
        // 2. 将 View 下移自身高度，注意汉字上下有边，一般为字体高度的 1/8
        // 3. 按控件大小等比缩放汉字，注意默认(0, 0)左下缩，现在要求左上缩
        canvas.scale(1F, -1F)
        canvas.translate(0F, -SVG_STROKE_HEIGHT * 7 / 8) // -1024 + 1024/8
        canvas.scale(xTmp, yTmp, 0F, SVG_STROKE_HEIGHT * 7 / 8) // 1024 - 1024/8
        for (stroke in strokePaths) {
            canvas.drawPath(stroke, strokePaint)
        }
        canvas.restoreToCount(restore)

        val w = measuredWidth
        val h = measuredHeight
        val layer = canvas.saveLayer(0F, 0F, w.toFloat(), h.toFloat(), null)

        // 目标Bitmap

        if (dstBmp == null) {
            dstBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            dstCanvas = Canvas(dstBmp!!)
        }
        dstPaint.xfermode = srcMode // 只保留 srcBmp 的 alpha 和 color ，所以绘制出来只有源图

        val c1 = dstCanvas!!.save()
        dstCanvas!!.scale(1F, -1F)
        dstCanvas!!.translate(0F, -SVG_STROKE_HEIGHT * 7 / 8)
        dstCanvas!!.scale(xTmp, yTmp, 0F, SVG_STROKE_HEIGHT * 7 / 8)
        for (i in strokePaths.size - 1 downTo 0) { // 进度内的 stroke path
            if (i <= currIndex && currIndex < strokePaths.size) {
                dstCanvas!!.drawPath(strokePaths[i], dstPaint)
            }
        }

        dstCanvas!!.restoreToCount(c1)
        dstPaint.xfermode = null

        Log.d("zuo", "dstBmp over")
        canvas.drawBitmap(dstBmp!!, 0F, 0F, medianPaint)

        // 在两者相交的地方绘制源图像
        medianPaint.xfermode = porterDuffXfermode

        // src bitmap

        if (srcBmp == null) {
            srcBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            srcCanvas = Canvas(srcBmp!!)
        }
        srcPaint.xfermode = srcMode // 只保留 srcBmp 的 alpha 和 color ，所以绘制出来只有源图

        val c2 = srcCanvas!!.save()
        srcCanvas!!.scale(1F, -1F)
        srcCanvas!!.translate(0F, -SVG_STROKE_HEIGHT * 7 / 8)
        srcCanvas!!.scale(xTmp, yTmp, 0F, SVG_STROKE_HEIGHT * 7 / 8)
        if (medianMeasures.isNotEmpty()) { // 绘制当前进度下的笔画

            // 起笔落笔 都添加一个圆，防止不完整
            Log.d("zuo", "index->$currIndex, progress->$progress")
            drawBackbonePointCircle(currIndex * 2, 20F)
            if (progress > 0.99) {
                drawBackbonePointCircle(currIndex * 2 + 1, 30F)
            }

            tempPath.reset()
            val m = medianMeasures[currIndex]
            m.getSegment(0F, m.length * progress, tempPath, true)
            srcCanvas!!.drawPath(tempPath, srcPaint)
        }
        srcCanvas!!.restoreToCount(c2);
        srcPaint.xfermode = null;

        Log.d("zuo", "srcBmp over")
        canvas.drawBitmap(srcBmp!!, 0F, 0F, medianPaint);
        medianPaint.xfermode = null;

        canvas.restoreToCount(layer)
    }

    private fun drawBackbonePointCircle(index: Int, radius: Float) {
        srcPaint.style = Paint.Style.FILL
        // 其中points 为起笔落笔骨干点，size = 2 * medianPaint
        srcCanvas!!.drawCircle(points[index].x.toFloat(), points[index].y.toFloat(), radius, srcPaint)
        srcPaint.style = Paint.Style.STROKE // 恢复
    }

    private fun startAnimation() {
        val animator = createAnimation()
        animator.start()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d("zuo", "anim end")
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d("zuo", "anim start")
                progress = 0F
                currIndex = 0
            }

            override fun onAnimationRepeat(animation: Animator?) {
                Log.d("zuo", "anim repeat")
                progress = 0F
                currIndex = 0
            }
        })
    }

    private fun createAnimation(): AnimatorSet {
        val set = AnimatorSet()
        val animators = ArrayList<Animator>()
        for ((index, _) in medians.withIndex()) {
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener { valueAnimator ->
                progress = valueAnimator.animatedValue as Float // 进度值
                currIndex = index // 当前 path 索引
                sleepAnimation()
                postInvalidate()
            }
            animator.duration = 750
            animators.add(animator)
        }
        set.playSequentially(animators)
        return set
    }

    private fun sleepAnimation() {
        try {
            Thread.sleep(15)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}