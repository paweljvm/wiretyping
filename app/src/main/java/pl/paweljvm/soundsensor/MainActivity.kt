package pl.paweljvm.soundsensor

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import pl.paweljvm.soundsensor.http.IpFinder
import pl.paweljvm.soundsensor.http.Request
import pl.paweljvm.soundsensor.http.SimpleHttpServer
import pl.paweljvm.soundsensor.service.SoundDetector
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


class MainActivity : AppCompatActivity() {

    private var soundDetector:SoundDetector? =null
    private var soundView:TextView? = null
    private var ipView:TextView? = null
    @Volatile private var captureSoundStream:InputStream? = null
    @Volatile private  var busy:Boolean = false
    @Volatile private var lastDetection:Long =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        soundView = findViewById(R.id.sound)
        ipView = findViewById(R.id.ip)
//        soundDetector = SoundDetector { it -> runOnUiThread {
//            if(it >= 10000) {
//                busy = true
//                lastDetection = System.currentTimeMillis()
//            } else {
//                if(System.currentTimeMillis() - lastDetection > 60_000) {
//                    busy=false
//                }
//            }
//            soundView?.text = it.toString()
//        } }
//        soundDetector?.start { captureSoundStream = it}
        val ip =  IpFinder.firstExternalIp()
        val server = SimpleHttpServer()
        val bufferSize = 2 * AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
        val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,44100,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,bufferSize)


        server.registerSimple(Request(Request.Companion.Method.GET,"/"),{
            "<html><body><h1>Room available = ${!busy} Last sound detection -> ${Date(lastDetection)}</h1><audio controls>\n" +
                    "  <source src=\"http://$ip:7777/capture.wav\" type=\"audio/wav\"></audio></body></html>"
        })
        server.register(Request(Request.Companion.Method.GET,"/capture.wav"),{
            req,out ->
                fun asBA(value:String) = value.toByteArray(Charset.defaultCharset())
                fun asHex(value:Int) = String.format("%x",value)
                fun wavHeader(out:OutputStream) {
                    out.write(asBA("RIFF"))
                    out.write(byteArrayOf(0xFF.toByte(),0xFF.toByte(),0xFF.toByte(),0xFF.toByte())) // Final file size not known yet, write 0
                    out.write(asBA("WAVE"))
                    out.write(asBA("fmt "))
                    val buffer = byteArrayOf(
                    0x10 ,0x00 ,0x00 ,0x00 ,0x01 ,0x00 ,0x01 ,0x00 ,0x44 ,0xac.toByte()
                            ,0x00 ,0x00 ,0x88.toByte() ,0x58 ,0x01 ,0x00
                            ,0x02 ,0x00 ,0x10 ,0x00 ,0x64 ,0x61
                            ,0x74 ,0x61 ,0xff.toByte() ,0xff.toByte() ,0xff.toByte() ,0xff.toByte() )
                    out.write(buffer)

                }

                audioRecord.startRecording()
                out.write(asBA("HTTP/1.1 200 OK\r\n"))
                out.write(asBA("Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n"))
                out.write(asBA("ContentType: audio/x-vaw\r\n"))
                out.write(asBA("Expires: -1\r\n"))
                out.write(asBA("Pragma: no-cache\r\n"))
                out.write(asBA("Connection: close\r\n\r\n"))
                out.flush()
                wavHeader(out)
                out.flush()
                val buffer = ByteArray(bufferSize)
                while(true) {
                        var size = audioRecord.read(buffer,0,buffer.size)
                        if(size > 0) {
                            out.write(buffer)
                            out.flush()
                        }

                }



        })
        ip?.also {
            ipView?.text = "Listening at $it:7777/"
            Thread {server.start(it,7777) }.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        soundDetector?.stop()
    }
}
