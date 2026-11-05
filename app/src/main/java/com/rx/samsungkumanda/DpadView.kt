package com.rx.samsungkumanda

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.min

/** Yön tuşları halkası ve ortada OK. Yön bölgelerine basılı tutunca tekrar gönderir. */
class DpadView(
    context: Context,
    private val onKey: (RemoteKey, View) -> Unit
) : View(context) {

    private enum class Zone { UP, DOWN, LEFT, RIGHT, OK }

    private val density = resources.displayMetrics.density
    private val handler = Handler(Looper.getMainLooper())
    private var pressed: Zone? = null

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = KeyStyle.KEY }
    private val ringStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = density
        color = KeyStyle.BODY_STROKE
    }
    private val pressedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF565B70.toInt() }
    private val okPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = KeyStyle.KEY_DARK }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = KeyStyle.TEXT }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = KeyStyle.TEXT
        textAlign = Paint.Align.CENTER
        textSize = 18f * density
        isFakeBoldText = true
    }

    private val repeater = object : Runnable {
        override fun run() {
            pressed?.let { onKey(keyOf(it), this@DpadView) }
            handler.postDelayed(this, 160)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = resolveSize((240 * density).toInt(), widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val outer = min(width, height) / 2f - 3 * density
        val inner = outer * 0.38f

        canvas.drawCircle(cx, cy, outer, ringPaint)

        val rect = RectF(cx - outer, cy - outer, cx + outer, cy + outer)
        when (pressed) {
            Zone.RIGHT -> canvas.drawArc(rect, -45f, 90f, true, pressedPaint)
            Zone.DOWN -> canvas.drawArc(rect, 45f, 90f, true, pressedPaint)
            Zone.LEFT -> canvas.drawArc(rect, 135f, 90f, true, pressedPaint)
            Zone.UP -> canvas.drawArc(rect, 225f, 90f, true, pressedPaint)
            else -> Unit
        }

        okPaint.color = if (pressed == Zone.OK) 0xFF3A3F52.toInt() else KeyStyle.KEY_DARK
        canvas.drawCircle(cx, cy, inner, okPaint)
        canvas.drawCircle(cx, cy, outer, ringStroke)
        canvas.drawCircle(cx, cy, inner, ringStroke)

        val d = (outer + inner) / 2f
        val s = 8 * density
        for (angle in floatArrayOf(0f, 90f, 180f, 270f)) {
            canvas.save()
            canvas.rotate(angle, cx, cy)
            val p = Path().apply {
                moveTo(cx, cy - d - s)
                lineTo(cx - s, cy - d + s * 0.7f)
                lineTo(cx + s, cy - d + s * 0.7f)
                close()
            }
            canvas.drawPath(p, arrowPaint)
            canvas.restore()
        }

        val fm = textPaint.fontMetrics
        canvas.drawText("OK", cx, cy - (fm.ascent + fm.descent) / 2f, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val zone = zoneAt(event.x, event.y) ?: return false
                pressed = zone
                invalidate()
                onKey(keyOf(zone), this)
                if (zone != Zone.OK) handler.postDelayed(repeater, 450)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(repeater)
                pressed = null
                invalidate()
                if (event.actionMasked == MotionEvent.ACTION_UP) performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun zoneAt(x: Float, y: Float): Zone? {
        val dx = x - width / 2f
        val dy = y - height / 2f
        val outer = min(width, height) / 2f - 3 * density
        val dist = hypot(dx, dy)
        return when {
            dist > outer -> null
            dist <= outer * 0.38f -> Zone.OK
            abs(dx) > abs(dy) -> if (dx > 0) Zone.RIGHT else Zone.LEFT
            else -> if (dy > 0) Zone.DOWN else Zone.UP
        }
    }

    private fun keyOf(zone: Zone): RemoteKey = when (zone) {
        Zone.UP -> SamsungKeys.UP
        Zone.DOWN -> SamsungKeys.DOWN
        Zone.LEFT -> SamsungKeys.LEFT
        Zone.RIGHT -> SamsungKeys.RIGHT
        Zone.OK -> SamsungKeys.OK
    }
}
