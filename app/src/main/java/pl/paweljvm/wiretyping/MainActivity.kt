package pl.paweljvm.wiretyping

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import pl.paweljvm.wiretyping.http.IpFinder
import pl.paweljvm.wiretyping.http.Request
import pl.paweljvm.wiretyping.http.SimpleHttpServer
import pl.paweljvm.wiretyping.service.SoundDetector
import pl.paweljvm.wiretyping.stream.StreamProvider
import pl.paweljvm.wiretyping.stream.StreamRequestHandler
import pl.paweljvm.wiretyping.stream.audio.AudioStreamStrategy
import pl.paweljvm.wiretyping.stream.audio.WavStreamRequestStrategy
import pl.paweljvm.wiretyping.stream.video.CameraStreamStrategy
import pl.paweljvm.wiretyping.stream.video.JpegStreamRequestStrategy


class MainActivity : AppCompatActivity() {

    private var soundDetector:SoundDetector? =null
    private var soundView:TextView? = null
    private var ipView:TextView? = null
    private var server:SimpleHttpServer?=null
    private var audioStreamProvider:StreamProvider?=null
    private var videoStreamProvider:StreamProvider?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        soundView = findViewById(R.id.sound)
        ipView = findViewById(R.id.ip)
        val ip =  IpFinder.firstExternalIp()
        server = SimpleHttpServer(64)
        server?.let {
            with(it) {
                registerSimple(Request(Request.Companion.Method.GET,"/"),{
                    "<html><body><h1>Wiretapping ...</h1><audio controls>\n" +
                            "  <source src=\"http://$ip:7777/capture.wav\" type=\"audio/wav\"></audio>" +
                            " <img style='width:640px;height:480px'   src='/video'> "+
                            "</body>" +
                            "</html>"
                })
                val audio= StreamProvider(AudioStreamStrategy)
                val video=StreamProvider(CameraStreamStrategy)
                audioStreamProvider=audio
                videoStreamProvider=video
                register(Request(Request.Companion.Method.GET,"/capture.wav"),StreamRequestHandler(WavStreamRequestStrategy(),audio)::invoke)
                register(Request(Request.Companion.Method.GET,"/video"),StreamRequestHandler(JpegStreamRequestStrategy(),video )::invoke)
            }
        }
        ip?.also {
            ipView?.text = "Wiretapping available -> $it:7777/"
            Thread {server?.start(it,7777) }.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        audioStreamProvider?.stop()
        videoStreamProvider?.stop()
        server?.stop()
        soundDetector?.stop()
    }
}
