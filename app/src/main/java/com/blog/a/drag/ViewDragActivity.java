package com.blog.a.drag;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blog.a.BaseActivity;
import com.blog.a.R;

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
