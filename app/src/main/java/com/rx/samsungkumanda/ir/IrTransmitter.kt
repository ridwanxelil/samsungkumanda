package com.rx.samsungkumanda.ir

import android.content.Context
import android.hardware.ConsumerIrManager

/** Telefonun IR vericisini saran ince katman. */
class IrTransmitter(context: Context) {

    private val manager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    val available: Boolean
        get() = manager?.hasIrEmitter() == true

    fun send(command: Int): Boolean {
        val m = manager ?: return false
        if (!m.hasIrEmitter()) return false
        return try {
            m.transmit(SamsungProtocol.CARRIER_HZ, SamsungProtocol.buildPattern(command))
            true
        } catch (e: Exception) {
            false
        }
    }
}
