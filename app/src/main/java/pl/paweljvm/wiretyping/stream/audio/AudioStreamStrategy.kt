package pl.paweljvm.wiretyping.stream.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import pl.paweljvm.wiretyping.stream.StreamStrategy

/**
 * Created by pawel on 2019-05-26.
 */
object AudioStreamStrategy : StreamStrategy {



    private val SAMPLE_RATE = 44100

    private val bufferSize = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)

    private var audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT ,bufferSize)



    override fun handleStart(newBufferListener: (size: Int, buffer: ByteArray) -> Unit) {
        audioRecord.startRecording()
        Thread {
            var currentBuffer = ByteArray(bufferSize)
            while(true) {
                val size = audioRecord.read(currentBuffer,0, bufferSize)
                newBufferListener(size,currentBuffer)
            }
        } .start()
    }
    override fun handleStop() {
        audioRecord.stop()
        audioRecord.release()
    }

}
