package com.rx.samsungkumanda

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.appcompat.app.AppCompatActivity
import com.rx.samsungkumanda.ir.IrTransmitter

class MainActivity : AppCompatActivity() {

    private lateinit var ir: IrTransmitter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ir = IrTransmitter(this)

        val textEntry = TextEntry { command -> ir.send(command) }
        val ui = RemoteUi(this, ir.available, textEntry) { key, view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ir.send(key.command)
        }
        setContentView(ui.build())
    }
}
