package com.arinal.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arinal.R
import com.arinal.data.model.WatchListModel

class WatchListAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: (Int) -> Unit
) : ListAdapter<WatchListModel.Data, WatchListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<WatchListModel.Data>() {
    override fun areItemsTheSame(oldItem: WatchListModel.Data, newItem: WatchListModel.Data) = oldItem.coinInfo.id == oldItem.coinInfo.id
    override fun areContentsTheSame(oldItem: WatchListModel.Data, newItem: WatchListModel.Data) = oldItem.coinInfo.id == oldItem.coinInfo.id
}) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_watchlist, parent, false))
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            val data = getItem(position)
            view.findViewById<TextView>(R.id.tv_name).text = data.coinInfo.name
            val fullName = data.coinInfo.fullName
            view.findViewById<TextView>(R.id.tv_full_name).text = if (fullName.length > 50) fullName.substring(0, 47) + "..." else fullName
            view.findViewById<TextView>(R.id.tv_price).text = data.display.idr.price.removePrefix("IDR ")
            val change = data.display.idr.change.removePrefix("IDR ")
            view.findViewById<TextView>(R.id.tv_change).apply {
                text = change
                setTextColor(ContextCompat.getColor(context, if (change.startsWith("-")) R.color.color_red else R.color.color_secondary))
            }
            view.setOnClickListener { onClickListener(position) }
        }
    }

}
