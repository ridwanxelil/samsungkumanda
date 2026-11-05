package com.rx.samsungkumanda

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

/**
 * Telefon klavyesinden yazılan karakterleri TV’ye rakam tuşlarıyla (çoklu dokunuş) gönderir.
 * Örn. "h" → 4 tuşuna 2 kez. Aynı tuştaki ardışık harfler arasında TV’nin imleci
 * ilerletmesi için bekleme yapılır. Tüm gönderimler sıraya alınır.
 */
class TextEntry(private val send: (Int) -> Unit) {

    private val handler = Handler(Looper.getMainLooper())
    private var lastAt = 0L
    private var lastDigit = -1

    fun type(text: CharSequence) {
        text.forEach { enqueueChar(it) }
    }

    /** Sıradaki harflerden sonra tek bir tuş gönderir (ör. Enter). */
    fun press(command: Int) = schedule(command, 1, -1)

    private fun enqueueChar(raw: Char) {
        val ch = normalize(raw)
        for ((digit, letters) in KEYPAD) {
            val index = letters.indexOf(ch)
            if (index >= 0) {
                schedule(SamsungKeys.DIGITS[digit].command, index + 1, digit)
                return
            }
        }
    }

    private fun schedule(command: Int, presses: Int, digit: Int) {
        val wait = if (digit != -1 && digit == lastDigit) COMMIT_MS else GAP_MS
        var t = maxOf(SystemClock.uptimeMillis(), lastAt + wait)
        repeat(presses) {
            handler.postAtTime({ send(command) }, t)
            lastAt = t
            t += TAP_MS
        }
        lastDigit = digit
    }

    private fun normalize(c: Char): Char = when (val l = c.lowercaseChar()) {
        'ç' -> 'c'
        'ş' -> 's'
        'ğ' -> 'g'
        'ı' -> 'i'
        'ö' -> 'o'
        'ü' -> 'u'
        else -> l
    }

    private companion object {
        const val TAP_MS = 260L      // aynı tuşa ardışık basış aralığı
        const val GAP_MS = 320L      // farklı tuşlar arası
        const val COMMIT_MS = 1150L  // aynı tuştaki sonraki harften önce bekleme

        val KEYPAD = mapOf(
            1 to ".,?!1", 2 to "abc2", 3 to "def3", 4 to "ghi4", 5 to "jkl5",
            6 to "mno6", 7 to "pqrs7", 8 to "tuv8", 9 to "wxyz9", 0 to " 0"
        )
    }
}
