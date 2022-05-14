package com.blog.a.nested;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blog.a.R;
import com.blog.a.utils.CommonUtils;

/**
 * 嵌套滑动栗子。
 */
public class NestedViewGroup extends ViewGroup {

    // VIEW初始距离和当前实际距离
    private int mHeaderInitTop;
    private int mTargetInitTop;
    private int mHeaderCurrTop;
    private int mTargetCurrTop;
    private int mTargetInitBottom;

    // VIEW
    private View mHeaderView;
    private View mTargetView;
    private View mInnerScrollView;

    // VIEW ID
    private int mHeaderResId;
    private int mTargetResId;
    private int mInnerScrollId;

    // 能识别的最小滑动距离
    private int mTouchSlop;
    // 投掷的最大速度，单位：像素/秒。
    private int maxFlingVelocity;
    private int minFlingVelocity;

    // 拖拽是否拦截事件
    private boolean mIsDragging;
    // 是否点击在Header 区域
    boolean isDownInTop = false;
    // 用户手指按下的Y坐标
    private float mDownY;
    // 距离VIEW GROUP滑动时的Y坐标
    private float mLastMotionY;

    // 弹性滑动
    private Scroller mScroller;
    // 速度追踪
    private VelocityTracker mVelocityTracker;
    // 500ms内比例的像素数
    private int vyPxCount;

    // mTargetView距离顶部距离监听
    private IScrollListener mListener;

    public NestedViewGroup(Context context) {
        this(context, null);
    }

    public NestedViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 如果存在固定TitleBar, 可直接通过marginTop设置NestedViewGroup留白大小

    public NestedViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取配置参数
        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.NestedViewGroup
                , defStyleAttr, 0);
        mHeaderResId = array.getResourceId
                (R.styleable.NestedViewGroup_header_id, -1);
        mTargetResId = array.getResourceId
                (R.styleable.NestedViewGroup_target_id, -1);
        mInnerScrollId = array.getResourceId
                (R.styleable.NestedViewGroup_inn_id, -1);
        if (mHeaderResId == -1 || mTargetResId == -1
                || mInnerScrollId == -1)
            throw new RuntimeException("VIEW ID is null");

        final Context c = context.getApplicationContext();

        mHeaderInitTop = CommonUtils.dip2px(c, array.getInt(
                R.styleable.NestedViewGroup_header_init_top, 0));
        mHeaderCurrTop = mHeaderInitTop;
        // 屏幕高度 - 底部距离 - 状态栏高度
        mTargetInitBottom = CommonUtils.dip2px(c, array.getInt(
                R.styleable.NestedViewGroup_target_init_bottom, 0));
        // 注意：当前activity默认去掉了标题栏
        mTargetInitTop = CommonUtils.getScreenHeight(c) - mTargetInitBottom
                - CommonUtils.getStatusBarHeight(c);
        mTargetCurrTop = mTargetInitTop;

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity();

        mScroller = new Scroller(getContext());
        mScroller.setFriction(0.9f);

        childDrawOrder();
    }

    /**
     * 设置滑动距离监听。
     */
    public void setOnScrollListener(IScrollListener listener) {
        this.mListener = listener;
    }

    /**
     * @return 初始设置的target_init_bottom
     */
    public int getTargetInitBottom() {
        return mTargetInitBottom;
    }

    /**
     * 允许子视图排序。
     *
     * <p>通过重新{@link #getChildDrawingOrder}方法，
     * 来决定子视图绘制顺序。确保TargetView在上层。
     */
    protected void childDrawOrder() {
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = findViewById(mHeaderResId);
        mTargetView = findViewById(mTargetResId);
        mInnerScrollView = findViewById(mInnerScrollId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 计算子VIEW的尺寸
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthModle = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightModle = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthModle) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                // TODO:wrap_content 暂不考虑
                break;

            case MeasureSpec.EXACTLY:
                // 全屏或者固定尺寸
                break;
        }

        switch (heightModle) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;

            case MeasureSpec.EXACTLY:
                break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        if (childCount == 0)
            return;
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        // 注意：原始bottom不是height高度，而是又向下挪了mTargetInitTop
        mTargetView.layout(getPaddingLeft()
                , getPaddingTop() + mTargetCurrTop
                , width - getPaddingRight()
                , height + mTargetCurrTop
                        + getPaddingTop() + getPaddingBottom());

        int headerWidth = mHeaderView.getMeasuredWidth();
        int headerHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout((width - headerWidth)/2
                , mHeaderCurrTop + getPaddingTop()
                , (width + headerWidth)/2
                , headerHeight + mHeaderCurrTop + getPaddingTop());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        // 如果上次滚动还未结束，则先停下
        if (!mScroller.isFinished())
            mScroller.forceFinished(true);

        // 不拦截事件，将事件传递给TargetView
        if (canChildScrollDown())
            return false;

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                mIsDragging = false;
                // 如果点击在Header区域，则不拦截事件
                isDownInTop = mDownY <= mTargetCurrTop - mTouchSlop;
                break;

            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                if (isDownInTop) {
                    return false;
                } else {
                    startDragging(y);
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                break;
        }

        return mIsDragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (canChildScrollDown())
            return false;

        // 添加速度监听
        acquireVelocityTracker(event);

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                startDragging(y);

                if (mIsDragging) {
                    float dy = y - mLastMotionY;
                    if (dy >= 0) {
                        moveTargetView(dy);
                    } else {
                        /**
                         * 此时，事件在ViewGroup内，
                         * 需手动分发给TargetView
                         */
                        if (mTargetCurrTop + dy <= 0) {
                            moveTargetView(dy);
                            int oldAction = event.getAction();
                            event.setAction(MotionEvent.ACTION_DOWN);
                            dispatchTouchEvent(event);
                            event.setAction(oldAction);
                        } else {
                            moveTargetView(dy);
                        }
                    }
                    mLastMotionY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    mIsDragging = false;
                    mVelocityTracker.computeCurrentVelocity(500, maxFlingVelocity);
                    final float vy = mVelocityTracker.getYVelocity();
                    vyPxCount = (int)(vy * 1.25);
                    finishDrag(vyPxCount);
                }
                releaseVelocityTracker();
                return false;

            case MotionEvent.ACTION_CANCEL:
                // 回收滑动监听
                releaseVelocityTracker();
                return false;

        }

        return mIsDragging;
    }

    /**
     * 决定子视图绘制顺序。
     *
     * <p>确保header在滚动列表之前绘制，
     * 否则会被盖住。
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mHeaderView == null || mTargetView == null)
            return i;

        int headerIndex = indexOfChild(mHeaderView);
        int scrollIndex = indexOfChild(mTargetView);
        if (headerIndex < scrollIndex) {
            return i;
        }
        if (headerIndex == i) {
            return scrollIndex;
        } else if (scrollIndex == i) {
            return headerIndex;
        }
        return i;
    }

    /**
     * 由TargetView来处理滑动事件。
     *
     * <p>注意{@link RecyclerView#canScrollVertically}
     * 来判断当前视图是否可以继续滚动。
     * <ul>
     * <li>正数：实际是判断手指能否向上滑动
     * <li>负数：实际是判断手指能否向下滑动
     * </ul>
     */
    public boolean canChildScrollDown() {
        RecyclerView rv;
        // 当前只做了RecyclerView的适配
        if (mInnerScrollView instanceof RecyclerView) {
            rv = (RecyclerView) mInnerScrollView;
            if (android.os.Build.VERSION.SDK_INT < 14) {
                RecyclerView.LayoutManager lm = rv.getLayoutManager();
                boolean isFirstVisible;
                if (lm != null && lm instanceof LinearLayoutManager) {
                    isFirstVisible = ((LinearLayoutManager)lm)
                            .findFirstVisibleItemPosition() > 0;
                    return rv.getChildCount() > 0
                            && (isFirstVisible || rv.getChildAt(0)
                            .getTop() < rv.getPaddingTop());
                }
            } else {
                return rv.canScrollVertically(-1);
            }
        }
        return false;
    }

    /**
     * 向上能够滑动的距离顶部距离。
     *
     * <p>如果Item数量太少，导致rv不能占满一屏时，
     * 注意向上滑动的距离。
     */
    public int toTopMaxOffset() {
        final RecyclerView rv;
        if (mInnerScrollView instanceof RecyclerView) {
            rv = (RecyclerView) mInnerScrollView;
            if (android.os.Build.VERSION.SDK_INT >= 14) {

                return Math.max(0, mTargetInitTop -
                        (rv.computeVerticalScrollRange() - mTargetInitBottom));
            }
        }
        return 0;
    }

    /**
     * 手指向下滑动或TargetView距离顶部距离>0，
     * 则ViewGroup拦截事件。
     *
     * <p>targetView拦截也需见{@link #canChildScrollDown}
     */
    private void startDragging(float y) {
        if (y > mDownY || mTargetCurrTop > toTopMaxOffset()) {
            final float yDiff = Math.abs(y - mDownY);
            if (yDiff > mTouchSlop && !mIsDragging) {
                mLastMotionY = mDownY + mTouchSlop;
                mIsDragging = true;
            }
        }
    }

    /**
     * 更新TargetView和HeaderView位置
     */
    private void moveTargetView(float dy) {
        int target = (int) (mTargetCurrTop + dy);
        moveTargetViewTo(target);
    }

    private void moveTargetViewTo(int target) {
        target = Math.max(target, toTopMaxOffset());
        if (target >= mTargetInitTop)
            target = mTargetInitTop;
        // TargetView的top、bottom两个方向都是加上offsetY
        ViewCompat.offsetTopAndBottom(mTargetView, target - mTargetCurrTop);
        // 更新当前TargetView距离顶部高度H
        mTargetCurrTop = target;

        int headerTarget;
        // 下拉超过定值H
        if (mTargetCurrTop >= mTargetInitTop) {
            headerTarget = mHeaderInitTop;
        } else if (mTargetCurrTop <= 0) {
            headerTarget = 0;
        } else {
            // 滑动比例
            float percent = mTargetCurrTop * 1.0f / mTargetInitTop;
            headerTarget = (int) (percent * mHeaderInitTop);
        }
        // HeaderView的top、bottom两个方向都是加上offsetY
        ViewCompat.offsetTopAndBottom(mHeaderView, headerTarget - mHeaderCurrTop);
        mHeaderCurrTop = headerTarget;

        if (mListener != null) {
            mListener.onTargetToTopDistance(mTargetCurrTop);
            mListener.onHeaderToTopDistance(mHeaderCurrTop);
        }
    }

    private void finishDrag(int vyPxCount) {
        if ((vyPxCount >= 0 && vyPxCount <= minFlingVelocity)
                || (vyPxCount <= 0 && vyPxCount >= -minFlingVelocity))
            return;

        // 速度 > 0，说明正向下滚动
        if (vyPxCount > 0) {
            // 防止超出临界值
            if (mTargetCurrTop < mTargetInitTop) {
                mScroller.startScroll(0, mTargetCurrTop
                        , 0, Math.min(vyPxCount, mTargetInitTop - mTargetCurrTop)
                        , 500);
                invalidate();
            }
        }
        // 速度 < 0，说明正向上滚动
        else if (vyPxCount < 0) {
            if (mTargetCurrTop <= 0) {
                if (mScroller.getCurrVelocity() > 0) {
                    // inner scroll 接着滚动
                }
            }

            mScroller.startScroll(0, mTargetCurrTop
                    , 0, Math.max(vyPxCount, -mTargetCurrTop)
                    , 500);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        // 判断是否完成滚动，true:未结束
        if (mScroller.computeScrollOffset()) {
            moveTargetViewTo(mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 去掉默认行为。
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {

    }

    /**
     * 追踪当前点击事件的速度
     */
    private void acquireVelocityTracker(MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 重置并回收内存
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * TargetView距离顶部距离监听
     */
    public interface IScrollListener {
        void onTargetToTopDistance(int distance);
        void onHeaderToTopDistance(int distance);
    }
}
