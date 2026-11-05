package com.rx.samsungkumanda.ir

/**
 * Samsung32 kızılötesi protokolü.
 *
 * Taşıyıcı: 38 kHz
 * Çerçeve: başlık (4500µs yanık / 4500µs sönük) + 32 bit (LSB önce) + bitiş biti
 * Veri: adres, adres, komut, ~komut  (Samsung TV adresi: 0x07)
 */
object SamsungProtocol {

    const val CARRIER_HZ = 38_000
    const val TV_ADDRESS = 0x07

    private const val HEADER_MARK = 4500
    private const val HEADER_SPACE = 4500
    private const val BIT_MARK = 560
    private const val ONE_SPACE = 1690
    private const val ZERO_SPACE = 560
    private const val TRAILING_GAP = 47_000

    /** ConsumerIrManager.transmit() için mikrosaniye dizisi üretir (yanık, sönük, yanık...). */
    fun buildPattern(command: Int): IntArray {
        val cmd = command and 0xFF
        val data = TV_ADDRESS or
            (TV_ADDRESS shl 8) or
            (cmd shl 16) or
            ((cmd.inv() and 0xFF) shl 24)

        val pattern = ArrayList<Int>(68)
        pattern.add(HEADER_MARK)
        pattern.add(HEADER_SPACE)
        for (bit in 0 until 32) {
            pattern.add(BIT_MARK)
            pattern.add(if ((data shr bit) and 1 == 1) ONE_SPACE else ZERO_SPACE)
        }
        pattern.add(BIT_MARK)
        pattern.add(TRAILING_GAP)
        return pattern.toIntArray()
    }
}
