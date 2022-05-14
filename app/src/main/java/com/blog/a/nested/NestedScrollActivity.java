package com.blog.a.nested;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blog.a.BaseActivity;
import com.blog.a.BaseDensityActivity;
import com.blog.a.R;
import com.blog.a.SimpleAdapter;
import com.blog.a.utils.CommonUtils;
import com.tencent.tencentmap.mapsdk.maps.MapView;

/**
 * 嵌套滑动效果类。
 */
public class NestedScrollActivity extends BaseActivity {
    static final String LOG_TAG = "tag";
    // 腾讯地图
    private MapView mapView;
    // 标题栏
    private Toolbar myToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无标题栏情况
        requestNoTitle(this);
        setContentView(R.layout.nested_scroll_layout);
        mapView = findViewById(R.id.t_map_view);
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("嵌套滑动Toolbar");
        myToolbar.getBackground().setAlpha(0);
        init();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.inner_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new SimpleAdapter(getExamples(), null));

        // 如果存在固定TitleBar, 可通过marginTop设置NestedViewGroup位置

        final NestedViewGroup nestedViewGroup = findViewById(R.id.dd_view_group);
        nestedViewGroup.setOnScrollListener(new NestedViewGroup.IScrollListener() {
            @Override
            public void onTargetToTopDistance(int distance) {
                Log.e(LOG_TAG, "target top :" + distance);
                final float total = CommonUtils.getScreenHeight(getApplicationContext())
                        - CommonUtils.getStatusBarHeight(getApplicationContext())
                        - nestedViewGroup.getTargetInitBottom();
                // 设置标题栏透明度
                if (null != myToolbar) {
                    myToolbar.getBackground().setAlpha((int) (255 * (1 - distance/total)));
                }
            }

            @Override
            public void onHeaderToTopDistance(int distance) {
                Log.e(LOG_TAG, "header top :" + distance);
            }
        });
    }

    protected void requestNoTitle(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity)activity).getSupportActionBar().hide();
        } else {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    private String[] getExamples() {
        String[] examples = new String[20];
        for (int i = 0; i < 20; i++) {
            examples[i] = "第" + i + "项";
        }
        return examples;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mapView)
            mapView.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mapView) {
            mapView.onRestart();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mapView)
            mapView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mapView)
            mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mapView)
            mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mapView)
            mapView.onDestroy();
    }

}
