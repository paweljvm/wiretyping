package pl.paweljvm.soundsensor.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

/**
 * Created by pawel on 2019-05-26.
 */
object AudioStreamProvider {

    private const val SAMPLE_RATE = 44100

    private val bufferSize = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)

    private val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT ,bufferSize)

    @Volatile private var listeners =  listOf<(Int,ByteArray)->Unit>()



   val wavHeader = byteArrayOf(
            *"RIFF".toByteArray(),
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            *"WAVE".toByteArray(),
            *"fmt ".toByteArray(),
            0x10, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x44, 0xac.toByte()
            , 0x00, 0x00, 0x88.toByte(), 0x58, 0x01, 0x00
            , 0x02, 0x00, 0x10, 0x00, 0x64, 0x61
            , 0x74, 0x61, 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()
    )

    fun start() {
        audioRecord.startRecording()
        Thread {
            var currentBuffer = ByteArray(bufferSize)
            while(true) {
               val size = audioRecord.read(currentBuffer,0, bufferSize)
               listeners.forEach { it(size,currentBuffer)}
            }
        } .start()

    }
    fun registerBufferListener(listener:(Int,ByteArray)->Unit) {
        listeners += listOf(listener)
    }


    fun stop() {
        audioRecord.stop()
    }


}
