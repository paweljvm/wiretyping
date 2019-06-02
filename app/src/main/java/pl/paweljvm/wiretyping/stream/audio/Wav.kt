package pl.paweljvm.wiretyping.stream.audio

/**
 * Created by pawel on 2019-06-02.
 */
object Wav {
     val HEADER = byteArrayOf(
            *"RIFF".toByteArray(),
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            *"WAVE".toByteArray(),
            *"fmt ".toByteArray(),
            0x10, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x44, 0xac.toByte()
            , 0x00, 0x00, 0x88.toByte(), 0x58, 0x01, 0x00
            , 0x02, 0x00, 0x10, 0x00, 0x64, 0x61
            , 0x74, 0x61, 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()
    )
}