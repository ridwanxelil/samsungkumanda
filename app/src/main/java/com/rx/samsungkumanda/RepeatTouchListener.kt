package com.rx.samsungkumanda

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

/** Dokununca anında gönderir; [RemoteKey.repeat] true ise basılı tutulunca tekrarlar. */
class RepeatTouchListener(
    private val key: RemoteKey,
    private val onKey: (RemoteKey, View) -> Unit
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())
    private var target: View? = null

    private val repeater = object : Runnable {
        override fun run() {
            target?.let { onKey(key, it) }
            handler.postDelayed(this, REPEAT_INTERVAL_MS)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                v.isPressed = true
                target = v
                onKey(key, v)
                if (key.repeat) handler.postDelayed(repeater, INITIAL_DELAY_MS)
            }
            MotionEvent.ACTION_UP -> {
                v.isPressed = false
                stop()
                v.performClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                v.isPressed = false
                stop()
            }
        }
        return true
    }

    private fun stop() {
        handler.removeCallbacks(repeater)
        target = null
    }

    private companion object {
        const val INITIAL_DELAY_MS = 450L
        const val REPEAT_INTERVAL_MS = 160L
    }
}
