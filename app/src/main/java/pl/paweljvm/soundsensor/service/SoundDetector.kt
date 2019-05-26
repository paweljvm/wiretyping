package pl.paweljvm.soundsensor.service

import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by pawel on 2019-05-10.
 */
class SoundDetector(val amplitudeListener:(Int) -> Unit) {
    private val FILE="/dev/null"
    private var recorder: MediaRecorder? = null
    private val DELAY= 100L
    fun start(streamReader:(InputStream)->Unit) {
        initRecorder(streamReader)
        startMeasuring()
    }

    private fun startMeasuring() {
        val handler = Handler()
        fun sendAmplitude() {
            val amplitude = recorder?.maxAmplitude ?: 0
            amplitudeListener(amplitude)
            handler.postDelayed({ sendAmplitude() }, DELAY)
        }
        handler.postDelayed({ sendAmplitude() }, DELAY)
    }

    private fun initRecorder(streamReader: (InputStream)->Unit) {
        recorder = MediaRecorder().apply {
            val socketName = "pl.paweljvm.soundsensor-${Date().time}"
            val localServerSocket = LocalServerSocket(socketName)
            val receiver = LocalSocket()

            receiver.connect(LocalSocketAddress(socketName))
            val sender = localServerSocket.accept()
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(sender.fileDescriptor)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("startRecorderFailed", "prepare() failed")
            }

            start()

            streamReader(receiver.inputStream)

        }
    }
    fun stop() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}