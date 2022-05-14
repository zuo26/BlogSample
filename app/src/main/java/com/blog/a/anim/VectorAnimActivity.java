package com.blog.a.anim;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blog.a.R;

public class VectorAnimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vector_anim_layout);

        final ImageView imageView = findViewById(R.id.vector_image);
        imageView.setBackgroundResource(R.drawable.animatorvectordrawable);
        //  Android 5.0 (API 21) 及更高版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable rocketAnimation = (AnimatedVectorDrawable) imageView.getBackground();
            imageView.setOnClickListener(View -> rocketAnimation.start());
        }

    }
}
