package com.rx.samsungkumanda

/**
 * Kumanda üzerindeki bir tuş. [command] Samsung32 komut kodudur.
 * [repeat] true ise basılı tutulunca tekrar gönderilir.
 */
data class RemoteKey(
    val label: String,
    val command: Int,
    val repeat: Boolean = false,
    val color: Int? = null
)

object SamsungKeys {
    // Üst sıra
    val POWER = RemoteKey("⏻", 0x02, color = 0xFFD32F2F.toInt())
    val SOURCE = RemoteKey("Kaynak", 0x01)
    val HDMI = RemoteKey("HDMI", 0x8B)

    // Kısayollar
    val CH_LIST = RemoteKey("Kanal Listesi", 0x6B)
    val FAVORITES = RemoteKey("Favoriler", 0x44)
    val GUIDE = RemoteKey("Rehber", 0x4F)
    val INFO = RemoteKey("Bilgi", 0x1F)

    // Ses / kanal
    val MUTE = RemoteKey("Sessiz", 0x0F)
    val VOL_UP = RemoteKey("Ses +", 0x07, repeat = true)
    val VOL_DOWN = RemoteKey("Ses −", 0x0B, repeat = true)
    val CH_UP = RemoteKey("Kanal +", 0x12, repeat = true)
    val CH_DOWN = RemoteKey("Kanal −", 0x10, repeat = true)
    val PRE_CH = RemoteKey("Önceki", 0x13)
    val TTX = RemoteKey("TTX", 0x2C)

    // Gezinme
    val UP = RemoteKey("▲", 0x60, repeat = true)
    val DOWN = RemoteKey("▼", 0x61, repeat = true)
    val LEFT = RemoteKey("◀", 0x65, repeat = true)
    val RIGHT = RemoteKey("▶", 0x62, repeat = true)
    val OK = RemoteKey("OK", 0x68)

    val BACK = RemoteKey("↩ Geri", 0x58)
    val HOME = RemoteKey("⌂ Ana Sayfa", 0x79)
    val MENU = RemoteKey("☰ Menü", 0x1A)
    val EXIT = RemoteKey("Çıkış", 0x2D)
    val TOOLS = RemoteKey("Araçlar", 0x4B)
    val SLEEP = RemoteKey("Uyku", 0x03)
    val PSIZE = RemoteKey("Boyut", 0x3E)

    /** İndeks = rakam (0..9). */
    val DIGITS = listOf(
        RemoteKey("0", 0x11), RemoteKey("1", 0x04), RemoteKey("2", 0x05),
        RemoteKey("3", 0x06), RemoteKey("4", 0x08), RemoteKey("5", 0x09),
        RemoteKey("6", 0x0A), RemoteKey("7", 0x0C), RemoteKey("8", 0x0D),
        RemoteKey("9", 0x0E)
    )

    val RED = RemoteKey("", 0x6C, color = 0xFFD32F2F.toInt())
    val GREEN = RemoteKey("", 0x14, color = 0xFF388E3C.toInt())
    val YELLOW = RemoteKey("", 0x15, color = 0xFFF9A825.toInt())
    val BLUE = RemoteKey("", 0x16, color = 0xFF1976D2.toInt())

    val REWIND = RemoteKey("⏪", 0x45)
    val PLAY = RemoteKey("▶", 0x47)
    val PAUSE = RemoteKey("⏸", 0x4A)
    val STOP = RemoteKey("⏹", 0x46)
    val FORWARD = RemoteKey("⏩", 0x48)
}
