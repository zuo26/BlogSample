package com.blog.a.recycler;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 带回弹效果的RecyclerView。
 */
public class OverScrollRecyclerView extends RecyclerView implements View.OnTouchListener {

    // 下拉与上拉，move px / view Translation
    private static final float DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD = 2f;
    private static final float DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK = 1f;
    // 默认减速系数
    private static final float DEFAULT_DECELERATE_FACTOR = -2f;
    // 最大反弹时间
    private static final int MAX_BOUNCE_BACK_DURATION_MS = 800;
    private static final int MIN_BOUNCE_BACK_DURATION_MS = 200;

    // 状态类:未滑动状态，滑动状态，回弹状态
    private IDecoratorState mCurrentState;
    private IdleState mIdleState;
    private OverScrollingState mOverScrollingState;
    private BounceBackState mBounceBackState;

    private final OverScrollStartAttributes mStartAttr = new OverScrollStartAttributes();
    private float mVelocity;
    private final RecyclerView mRecyclerView = this;

    public OverScrollRecyclerView(Context context) {
        this(context, null);
    }

    public OverScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initParams();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                return mCurrentState.handleMoveTouchEvent(event);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                return mCurrentState.handleUpTouchEvent(event);
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detach();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void attach() {
        mRecyclerView.setOnTouchListener(this);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void detach() {
        mRecyclerView.setOnTouchListener(null);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    private void initParams() {
        mBounceBackState = new BounceBackState();
        mOverScrollingState = new OverScrollingState();
        mCurrentState = mIdleState = new IdleState();
        attach();
    }

    private void issueStateTransition(IDecoratorState state) {
        IDecoratorState oldState = mCurrentState;
        mCurrentState = state;
        // 处理动画
        mCurrentState.handleTransitionAnim(oldState);
    }

    protected static class MotionAttributes {
        // 相对于父的偏移量view.getTranslationY
        float mAbsOffset;
        // 移动的偏移量
        float mDeltaOffset;
        // mDir: true 下拉, false:上拉
        boolean mDir;
    }

    protected static class OverScrollStartAttributes {
        int mPointerId;
        float mAbsOffset;
        boolean mDir;
    }

    protected static class AnimationAttributes {
        Property<View, Float> mProperty;
        float mAbsOffset;
        float mMaxOffset;
    }

    private void initAnimationAttributes(View view, AnimationAttributes attributes) {
        attributes.mProperty = View.TRANSLATION_Y;
        attributes.mAbsOffset = view.getTranslationY();
        attributes.mMaxOffset = view.getHeight();
    }

    private boolean initMotionAttributes(View view, MotionAttributes attributes,
                                         MotionEvent event) {
        if (event.getHistorySize() == 0) {
            return false;
        }

        // 像素偏移量
        final float dy = event.getY(0) - event.getHistoricalY(0, 0);
        final float dx = event.getX(0) - event.getHistoricalX(0, 0);

        if (Math.abs(dy) < Math.abs(dx)) {
            return false;
        }

        attributes.mAbsOffset = view.getTranslationY();
        attributes.mDeltaOffset = dy;
        attributes.mDir = attributes.mDeltaOffset > 0;

        return true;
    }

    private boolean isInAbsoluteStart(View view) {
        return !view.canScrollVertically(-1);
    }

    private boolean isInAbsoluteEnd(View view) {
        return !view.canScrollVertically(1);
    }

    private void translateView(View view, float offset) {
        view.setTranslationY(offset);
    }

    private void translateViewAndEvent(View view, float offset, MotionEvent event) {
        view.setTranslationY(offset);
        event.offsetLocation(0f, offset - event.getY(0));
    }

    protected interface IDecoratorState {
        // 处理move事件
        boolean handleMoveTouchEvent(MotionEvent event);
        // 处理up事件
        boolean handleUpTouchEvent(MotionEvent event);
        // 事件结束后的动画处理
        void handleTransitionAnim(IDecoratorState fromState);
    }

    class IdleState implements IDecoratorState {
        private final MotionAttributes mMoveAttr = new MotionAttributes();

        @Override
        public boolean handleMoveTouchEvent(MotionEvent event) {
            // 是否符合move要求，不符合不拦截事件
            if (!initMotionAttributes(mRecyclerView, mMoveAttr, event)) {
                return false;
            }

            // 在RecyclerView顶部但不能下拉 或 在RecyclerView底部但不能上拉
            if (!((isInAbsoluteStart(mRecyclerView) && mMoveAttr.mDir) ||
                    (isInAbsoluteEnd(mRecyclerView) && !mMoveAttr.mDir))) {
                return false;
            }

            // 保存当前Motion信息
            mStartAttr.mPointerId = event.getPointerId(0);
            mStartAttr.mAbsOffset = mMoveAttr.mAbsOffset;
            mStartAttr.mDir = mMoveAttr.mDir;

            // 初始状态->滑动状态
            issueStateTransition(mOverScrollingState);
            return mOverScrollingState.handleMoveTouchEvent(event);
        }

        @Override
        public boolean handleUpTouchEvent(MotionEvent event) {
            return false;
        }

        @Override
        public void handleTransitionAnim(IDecoratorState fromState) { }
    }

    class OverScrollingState implements IDecoratorState {
        private final float mTouchDragRatioFwd;
        private final float mTouchDragRatioBck;

        private final MotionAttributes mMoveAttr;

        public OverScrollingState() {
            mMoveAttr = new MotionAttributes();
            // 下拉与上拉，move px / view Translation
            mTouchDragRatioFwd = DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD;
            mTouchDragRatioBck = DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK;
        }

        @Override
        public boolean handleMoveTouchEvent(MotionEvent event) {
            final OverScrollStartAttributes startAttr = mStartAttr;
            // 不是一个触摸点事件，则直接切到回弹状态
            if (startAttr.mPointerId != event.getPointerId(0)) {
                issueStateTransition(mBounceBackState);
                return true;
            }

            final View view = mRecyclerView;

            // 是否符合move要求
            if (!initMotionAttributes(view, mMoveAttr, event)) {
                return true;
            }

            // mDeltaOffset: 实际要移动的像素，可以为下拉和上拉设置不同移动比
            float deltaOffset = mMoveAttr.mDeltaOffset / (mMoveAttr.mDir == startAttr.mDir
                    ? mTouchDragRatioFwd : mTouchDragRatioBck);
            // 计算偏移
            float newOffset = mMoveAttr.mAbsOffset + deltaOffset;

            // 上拉下拉状态与滑动方向不符，则回到初始状态，并将视图归位
            if ((startAttr.mDir && !mMoveAttr.mDir && (newOffset <= startAttr.mAbsOffset)) ||
                    (!startAttr.mDir && mMoveAttr.mDir && (newOffset >= startAttr.mAbsOffset))) {
                translateViewAndEvent(view, startAttr.mAbsOffset, event);
                issueStateTransition(mIdleState);
                return true;
            }

            // 不让父类截获move事件
            if (view.getParent() != null) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
            }

            // 计算速度
            long dt = event.getEventTime() - event.getHistoricalEventTime(0);
            if (dt > 0) {
                mVelocity = deltaOffset / dt;
            }

            // 改变控件位置
            translateView(view, newOffset);
            return true;
        }

        @Override
        public boolean handleUpTouchEvent(MotionEvent event) {
            // 事件up切换状态
            issueStateTransition(mBounceBackState);
            return false;
        }

        @Override
        public void handleTransitionAnim(IDecoratorState fromState) { }
    }

    class BounceBackState implements IDecoratorState,
            Animator.AnimatorListener,
            ValueAnimator.AnimatorUpdateListener {
        private final Interpolator mBounceBackInterpolator = new DecelerateInterpolator();
        private final float mDecelerateFactor;
        private final float mDoubleDecelerateFactor;
        private final AnimationAttributes mAnimAttributes;
        final View view = mRecyclerView;

        public BounceBackState() {
            mDecelerateFactor = DEFAULT_DECELERATE_FACTOR;
            mDoubleDecelerateFactor = 2f * DEFAULT_DECELERATE_FACTOR;
            mAnimAttributes = new AnimationAttributes();
        }

        @Override
        public void handleTransitionAnim(IDecoratorState fromState) {
            Animator bounceBackAnim = createAnimator();
            bounceBackAnim.addListener(this);
            bounceBackAnim.start();
        }

        @Override
        public boolean handleMoveTouchEvent(MotionEvent event) {
            return true;
        }

        @Override
        public boolean handleUpTouchEvent(MotionEvent event) {
            return true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            // 动画结束改变状态
            issueStateTransition(mIdleState);
        }

        private Animator createAnimator() {
            initAnimationAttributes(view, mAnimAttributes);

            // 速度为0了或手势记录的状态与mDir不符合，直接回弹
            if (mVelocity == 0f || (mVelocity < 0 && mStartAttr.mDir)
                    || (mVelocity > 0 && !mStartAttr.mDir)) {
                return createBounceBackAnimator(mAnimAttributes.mAbsOffset);
            }

            // 速度减到0，即到达最大距离时，需要的动画事件
            float slowdownDuration = (0 - mVelocity) / mDecelerateFactor;
            slowdownDuration = (slowdownDuration < 0 ? 0 : slowdownDuration);

            // 速度减到0，动画的距离，dx = (Vt^2 - Vo^2) / 2a
            float slowdownDistance = -mVelocity * mVelocity / mDoubleDecelerateFactor;
            float slowdownEndOffset = mAnimAttributes.mAbsOffset + slowdownDistance;

            // 开始动画，减速->回弹
            ObjectAnimator slowdownAnim = createSlowdownAnimator(view
                    , (int) slowdownDuration, slowdownEndOffset);
            ObjectAnimator bounceBackAnim = createBounceBackAnimator(slowdownEndOffset);
            AnimatorSet wholeAnim = new AnimatorSet();
            wholeAnim.playSequentially(slowdownAnim, bounceBackAnim);
            return wholeAnim;
        }

        private ObjectAnimator createSlowdownAnimator(View view
                , int slowdownDuration, float slowdownEndOffset) {
            ObjectAnimator slowdownAnim = ObjectAnimator.ofFloat(view
                    , mAnimAttributes.mProperty, slowdownEndOffset);
            slowdownAnim.setDuration(slowdownDuration);
            slowdownAnim.setInterpolator(mBounceBackInterpolator);
            slowdownAnim.addUpdateListener(this);
            return slowdownAnim;
        }

        private ObjectAnimator createBounceBackAnimator(float startOffset) {
            float bounceBackDuration = (Math.abs(startOffset)
                    / mAnimAttributes.mMaxOffset) * MAX_BOUNCE_BACK_DURATION_MS;
            ObjectAnimator bounceBackAnim = ObjectAnimator.ofFloat(view
                    , mAnimAttributes.mProperty, mStartAttr.mAbsOffset);
            bounceBackAnim.setDuration(Math.max((int) bounceBackDuration
                    , MIN_BOUNCE_BACK_DURATION_MS));
            bounceBackAnim.setInterpolator(mBounceBackInterpolator);
            bounceBackAnim.addUpdateListener(this);
            return bounceBackAnim;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) { }

        @Override
        public void onAnimationStart(Animator animation) { }

        @Override
        public void onAnimationCancel(Animator animation) { }

        @Override
        public void onAnimationRepeat(Animator animation) { }
    }

}