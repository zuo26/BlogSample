package com.blog.a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
    private String[] examples;

    public interface ViewHolderListener {

        void onItemClicked(View view, int adapterPosition);
    }

    private final ViewHolderListener viewHolderListener;

    public SimpleAdapter(String[] strs, ViewHolderListener viewHolderListener) {
        examples = strs;
        this.viewHolderListener = viewHolderListener;
    }

    @NonNull
    @Override
    public SimpleAdapter.ViewHolder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        return new SimpleAdapter.ViewHolder(view, viewHolderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleAdapter.ViewHolder holder, int position) {
        holder.tvContent.setText(examples[position]);
    }

    @Override
    public int getItemCount() {
        return examples.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final TextView tvContent;
        private final ViewHolderListener viewHolderListener;

        ViewHolder(View itemView, ViewHolderListener viewHolderListener) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            this.viewHolderListener = viewHolderListener;
            itemView.findViewById(R.id.all_layout).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != viewHolderListener) {
                viewHolderListener.onItemClicked(view, getAdapterPosition());
            }
        }
    }
}
