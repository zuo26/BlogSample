package com.blog.demo17;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.blog.a.R;
import com.squareup.picasso.Picasso;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setReturnTransition(new AutoTransition());
        }
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_transtion_grid);

        final GridView grid = findViewById(R.id.grid);
        grid.setOnItemClickListener(mOnItemClickListener);
        GridAdapter adapter = new GridAdapter();
        grid.setAdapter(adapter);
    }

    private final AdapterView.OnItemClickListener mOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Item item = (Item) adapterView.getItemAtPosition(position);

            // Construct an Intent as normal
            Intent intent = new Intent(IndexActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.getId());

            // BEGIN_INCLUDE(start_activity)
            /*
             * Now create an {@link android.app.ActivityOptions} instance using the
             * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])} factory
             * method.
             */
            @SuppressWarnings("unchecked")
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    IndexActivity.this,

                    new Pair<>(view.findViewById(R.id.imageview_item),
                            DetailActivity.VIEW_NAME_HEADER_IMAGE),
                    new Pair<>(view.findViewById(R.id.textview_name),
                            DetailActivity.VIEW_NAME_HEADER_TITLE));

            ActivityCompat.startActivity(IndexActivity.this, intent, activityOptions.toBundle());
        }
    };

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Item.ITEMS.length;
        }

        @Override
        public Item getItem(int position) {
            return Item.ITEMS[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater()
                        .inflate(R.layout.activity_transtion_grid_item
                                , parent, false);
            }

            final Item item = getItem(position);

            ImageView image = view.findViewById(R.id.imageview_item);
            Picasso.with(image.getContext()).load(item.getThumbnailUrl()).into(image);

            TextView name = view.findViewById(R.id.textview_name);
            name.setText(item.getName());

            return view;
        }
    }
}
