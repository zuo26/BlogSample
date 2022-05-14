package com.blog.a.drag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

public class DragViewGroup extends RelativeLayout {

    ViewDragHelper mDragHelper;

    public DragViewGroup(Context context) {
        this(context, null);
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private static class ViewDragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }
    }
}
