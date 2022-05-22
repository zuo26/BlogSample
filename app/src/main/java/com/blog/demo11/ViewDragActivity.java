package com.blog.demo11;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.blog.BaseActivity;
import com.blog.R;

/**
 * 可拖拽效果类。
 */
public class ViewDragActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_layout);
    }
}
