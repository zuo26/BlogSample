package com.blog.demo40

import android.content.Context

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blog.a.R

open class CustomAdapter(
    private val dataSet: Array<String>,
    val context: Context
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView
        var isInTheMiddle = false
        init {
            textView = v.findViewById(R.id.textView)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_layout2, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            if (position == dataSet.size / 2) {
                isInTheMiddle = true
                textView.text = context.resources.getString(R.string.middle)
            } else {
                isInTheMiddle = false
                textView.text = dataSet[position]
            }
        }
    }
}


