package com.arinal.common

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener : RecyclerView.OnScrollListener() {
    private var previousTotal = 0
    private var isLoading = true
    private var firstInit = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = recyclerView.layoutManager!!.itemCount
        val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (isLoading) {
            if (totalItemCount > previousTotal) {
                isLoading = false
                previousTotal = totalItemCount
            }
        }
        val visibleThreshold = 5
        if (totalItemCount != 0 && !isLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            if (firstInit && totalItemCount > 1) firstInit = false
            else {
                onLoadMore()
                isLoading = true
            }
        }
    }

    fun resetData() {
        previousTotal = 0
        isLoading = true
        firstInit = true
    }

    abstract fun onLoadMore()

}
