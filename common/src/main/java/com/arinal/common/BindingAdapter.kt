package com.arinal.common

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("bindLoading")
fun ShimmerFrameLayout.bindLoading(isLoading: Boolean) {
    visibility = if (isLoading) View.VISIBLE else View.GONE
    if (isLoading) startShimmer() else stopShimmer()
}

@BindingAdapter("bindIsEnabled")
fun SwipeRefreshLayout.bindIsEnabled(isEnabled: Boolean) = setEnabled(isEnabled)

@BindingAdapter("bindShowing")
fun FloatingActionButton.bindShowing(show: Boolean) = if (show) show() else hide()

@BindingAdapter("bindImageUrl")
fun ImageView.bindImageUrl(url: String) {
    Glide.with(this).load(url).into(this)
}

@BindingAdapter("bindWidthPercentage")
fun View.bindWidthPercentage(width: MutableLiveData<Float>) {
    width.value?.let {
        (layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = it
        requestLayout()
    }
}