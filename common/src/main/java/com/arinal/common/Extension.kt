package com.arinal.common

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.TextView
import androidx.core.graphics.ColorUtils.blendARGB

fun TextView.onTouchDarkerEffect() = onTouchColorEffect(BLACK)

fun TextView.onTouchBrighterEffect() = onTouchColorEffect(WHITE)

fun TextView.onTouchColorEffect(effectColor: Int) {
    isClickable = true
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) focusable = View.FOCUSABLE
    val defaultColor = currentTextColor
    val onTouchColor = blendARGB(defaultColor, effectColor, 0.3f)
    setOnTouchListener { v, event ->
        if (event.action == ACTION_DOWN) v.parent.requestDisallowInterceptTouchEvent(true)
        val color = if (event.action == ACTION_UP) defaultColor else onTouchColor
        setTextColor(color)
        for (drawable in compoundDrawables) drawable?.colorFilter = PorterDuffColorFilter(color, SRC_IN)
        performClick()
    }
}
