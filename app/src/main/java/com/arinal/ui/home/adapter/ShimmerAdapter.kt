package com.arinal.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arinal.R

class ShimmerAdapter(
    private val layoutInflater: LayoutInflater,
    private val itemCount: Int
) : RecyclerView.Adapter<ShimmerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit
    override fun getItemCount() = itemCount
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_shimmer_list, parent, false))
    }
}
