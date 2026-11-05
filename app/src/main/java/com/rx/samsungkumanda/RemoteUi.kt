package com.rx.samsungkumanda

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/** Gerçek Samsung kumandasına benzeyen arayüzü kodla oluşturur. */
class RemoteUi(
    private val context: Context,
    private val irAvailable: Boolean,
    private val textEntry: TextEntry,
    private val onKey: (RemoteKey, View) -> Unit
) {

    private val density = context.resources.displayMetrics.density
    private fun dp(v: Int) = (v * density).toInt()
    private fun dpf(v: Float) = v * density

    fun build(): View {
        val body = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(18), dp(22), dp(18), dp(24))
            background = KeyStyle.body(dpf(40f), dp(1))
        }

        // Üst sıra: güç — HDMI — kaynak
        body.addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(circleKey(SamsungKeys.POWER, 58, 22f))
                addView(View(context), LinearLayout.LayoutParams(0, 1, 1f))
                addView(key(SamsungKeys.HDMI, 44, 13f), fixed(84, 44))
                addView(key(SamsungKeys.SOURCE, 44, 13f), fixed(90, 44))
            }
        )

        body.addView(gap(14))
        body.addView(
            row(
                44, 12f,
                SamsungKeys.CH_LIST, SamsungKeys.FAVORITES, SamsungKeys.GUIDE, SamsungKeys.INFO
            )
        )

        body.addView(gap(10))
        val d = SamsungKeys.DIGITS
        body.addView(row(52, 20f, d[1], d[2], d[3]))
        body.addView(row(52, 20f, d[4], d[5], d[6]))
        body.addView(row(52, 20f, d[7], d[8], d[9]))
        body.addView(row(52, 15f, SamsungKeys.TTX, d[0], SamsungKeys.PRE_CH))

        body.addView(gap(16))
        body.addView(rockerRow())

        body.addView(gap(16))
        body.addView(
            DpadView(context, onKey),
            LinearLayout.LayoutParams(dp(250), dp(250))
        )

        body.addView(gap(12))
        body.addView(row(48, 14f, SamsungKeys.BACK, SamsungKeys.HOME, SamsungKeys.MENU))
        body.addView(
            row(
                44, 13f,
                SamsungKeys.EXIT, SamsungKeys.TOOLS, SamsungKeys.SLEEP, SamsungKeys.PSIZE
            )
        )

        body.addView(gap(12))
        body.addView(
            row(
                30, 12f,
                SamsungKeys.RED, SamsungKeys.GREEN, SamsungKeys.YELLOW, SamsungKeys.BLUE
            )
        )
        body.addView(
            row(
                48, 18f,
                SamsungKeys.REWIND, SamsungKeys.PLAY, SamsungKeys.PAUSE,
                SamsungKeys.STOP, SamsungKeys.FORWARD
            )
        )

        body.addView(gap(16))
        body.addView(textCard())

        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(20), dp(14), dp(20))
            addView(
                TextView(context).apply {
                    text = context.getString(if (irAvailable) R.string.ir_hint else R.string.ir_missing)
                    setTextColor(if (irAvailable) KeyStyle.TEXT_DIM else 0xFFEF5350.toInt())
                    textSize = 12f
                    gravity = Gravity.CENTER
                    setPadding(0, 0, 0, dp(12))
                }
            )
            addView(body)
        }

        return ScrollView(context).apply {
            setBackgroundColor(KeyStyle.PAGE)
            isFillViewport = true
            addView(column)
        }
    }

    // --- Tuşlar -----------------------------------------------------------

    private fun key(k: RemoteKey, heightDp: Int, sp: Float): TextView = TextView(context).apply {
        text = k.label
        textSize = sp
        gravity = Gravity.CENTER
        setTextColor(KeyStyle.TEXT)
        typeface = Typeface.DEFAULT_BOLD
        contentDescription = k.label.ifEmpty { "renkli tuş" }
        val base = k.color ?: KeyStyle.KEY
        background = KeyStyle.pressable(base, KeyStyle.lighten(base), dpf(100f))
        setOnTouchListener(RepeatTouchListener(k, onKey))
    }

    private fun circleKey(k: RemoteKey, sizeDp: Int, sp: Float): View {
        val v = key(k, sizeDp, sp)
        v.layoutParams = LinearLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
        return v
    }

    private fun fixed(wDp: Int, hDp: Int) =
        LinearLayout.LayoutParams(dp(wDp), dp(hDp)).apply { leftMargin = dp(8) }

    private fun row(heightDp: Int, sp: Float, vararg keys: RemoteKey): LinearLayout =
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            keys.forEach { k ->
                val lp = LinearLayout.LayoutParams(0, dp(heightDp), 1f)
                lp.setMargins(dp(4), dp(4), dp(4), dp(4))
                addView(key(k, heightDp, sp), lp)
            }
        }

    private fun gap(hDp: Int): View =
        View(context).apply { layoutParams = LinearLayout.LayoutParams(1, dp(hDp)) }

    // --- Ses / Kanal ------------------------------------------------------

    private fun rockerRow(): LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        addView(
            rocker(R.string.label_vol, SamsungKeys.VOL_UP, SamsungKeys.VOL_DOWN),
            LinearLayout.LayoutParams(dp(76), dp(170))
        )
        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                addView(circleKey(SamsungKeys.MUTE, 64, 12f))
                addView(gap(14))
                addView(circleKey(RemoteKey("Menü", SamsungKeys.MENU.command), 52, 12f))
            },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        addView(
            rocker(R.string.label_ch, SamsungKeys.CH_UP, SamsungKeys.CH_DOWN),
            LinearLayout.LayoutParams(dp(76), dp(170))
        )
    }

    private fun rocker(titleRes: Int, plus: RemoteKey, minus: RemoteKey): LinearLayout =
        LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = KeyStyle.shape(KeyStyle.KEY, dpf(100f))
            addView(half("+", plus), LinearLayout.LayoutParams(-1, 0, 1f))
            addView(
                TextView(context).apply {
                    text = context.getString(titleRes)
                    textSize = 11f
                    gravity = Gravity.CENTER
                    setTextColor(KeyStyle.TEXT_DIM)
                    typeface = Typeface.DEFAULT_BOLD
                }
            )
            addView(half("−", minus), LinearLayout.LayoutParams(-1, 0, 1f))
        }

    private fun half(symbol: String, k: RemoteKey): TextView = TextView(context).apply {
        text = symbol
        textSize = 28f
        gravity = Gravity.CENTER
        setTextColor(KeyStyle.TEXT)
        contentDescription = k.label
        background = KeyStyle.pressable(0x00000000, 0x40FFFFFF, dpf(40f))
        setOnTouchListener(RepeatTouchListener(k, onKey))
    }

    // --- Klavye ile yazma -------------------------------------------------

    private fun textCard(): View {
        val input = EditText(context).apply {
            hint = context.getString(R.string.type_hint)
            setHintTextColor(KeyStyle.TEXT_DIM)
            setTextColor(KeyStyle.TEXT)
            textSize = 16f
            // Öneri/yazım denetimi kapalı: her karakter tek tek ve anında gelir
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imeOptions = EditorInfo.IME_ACTION_SEND
            background = KeyStyle.shape(KeyStyle.KEY_DARK, dpf(16f), KeyStyle.BODY_STROKE, dp(1))
            setPadding(dp(16), dp(12), dp(16), dp(12))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (before == 0 && count > 0) textEntry.type(s.subSequence(start, start + count))
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        fun sendEnter(from: View) {
            from.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            textEntry.press(SamsungKeys.OK.command)
            input.text.clear()
        }
        input.setOnEditorActionListener { v, _, _ -> sendEnter(v); true }

        val enter = TextView(context).apply {
            text = "↵"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(KeyStyle.TEXT)
            background = KeyStyle.pressable(KeyStyle.KEY, KeyStyle.lighten(KeyStyle.KEY), dpf(100f))
            contentDescription = "Enter"
            setOnClickListener { sendEnter(it) }
        }

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    addView(input, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                    addView(enter, LinearLayout.LayoutParams(dp(52), dp(52)).apply { leftMargin = dp(8) })
                }
            )
            addView(
                TextView(context).apply {
                    text = context.getString(R.string.type_note)
                    textSize = 11f
                    setTextColor(KeyStyle.TEXT_DIM)
                    setPadding(dp(6), dp(8), dp(6), 0)
                }
            )
        }
    }
}
