package pl.paweljvm.soundsensor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import pl.paweljvm.soundsensor.audio.AudioWavRequestHandlerCreator
import pl.paweljvm.soundsensor.http.IpFinder
import pl.paweljvm.soundsensor.http.Request
import pl.paweljvm.soundsensor.http.SimpleHttpServer
import pl.paweljvm.soundsensor.service.SoundDetector
import java.io.InputStream
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
        val server = SimpleHttpServer(64)
        server.registerSimple(Request(Request.Companion.Method.GET,"/"),{
            "<html><body><h1>Wiretapping ...</h1><audio controls>\n" +
                    "  <source src=\"http://$ip:7777/capture.wav\" type=\"audio/wav\"></audio>" +
                    "</body>" +
                    "</html>"
        })
        server.register(Request(Request.Companion.Method.GET,"/capture.wav"),AudioWavRequestHandlerCreator::createHandler)
        ip?.also {
            ipView?.text = "Wiretapping available -> $it:7777/"
            Thread {server.start(it,7777) }.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        soundDetector?.stop()
    }
}
