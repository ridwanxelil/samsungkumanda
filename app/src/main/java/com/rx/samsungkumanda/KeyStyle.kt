package com.rx.samsungkumanda

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.core.graphics.ColorUtils

/** Gerçek kumanda görünümü için renkler ve şekiller. */
object KeyStyle {
    const val PAGE = 0xFF08090C.toInt()
    const val BODY_TOP = 0xFF262932.toInt()
    const val BODY_BOTTOM = 0xFF0D0E12.toInt()
    const val BODY_STROKE = 0xFF3A3D49.toInt()
    const val KEY = 0xFF2F323E.toInt()
    const val KEY_DARK = 0xFF191B22.toInt()
    const val TEXT = 0xFFE9EBF2.toInt()
    const val TEXT_DIM = 0xFF8A90A6.toInt()

    fun lighten(color: Int): Int = ColorUtils.blendARGB(color, Color.WHITE, 0.25f)

    fun shape(color: Int, radius: Float, stroke: Int? = null, strokePx: Int = 0) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
            if (stroke != null) setStroke(strokePx, stroke)
        }

    fun pressable(normal: Int, pressed: Int, radius: Float) = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), shape(pressed, radius))
        addState(intArrayOf(), shape(normal, radius))
    }

    fun body(radius: Float, strokePx: Int) = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(BODY_TOP, BODY_BOTTOM)
    ).apply {
        cornerRadius = radius
        setStroke(strokePx, BODY_STROKE)
    }
}
