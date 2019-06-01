package pl.paweljvm.soundsensor.audio

import pl.paweljvm.soundsensor.http.Request
import java.io.OutputStream
import java.nio.charset.Charset

/**
 * Created by pawel on 2019-06-01.
 */
object AudioWavRequestHandlerCreator {
   private  val HTTP_RESPONSE=
           arrayOf(
            "HTTP/1.1 200 OK\r\n",
            "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n",
            "ContentType: audio/x-vaw\r\n",
            "ContentType: audio/x-vaw\r\n",
            "ContentType: audio/x-vaw\r\n",
            "Expires: -1\r\n",
            "Pragma: no-cache\r\n",
            "Connection: close\r\n\r\n").joinToString(separator = "")



    fun createHandler(request:Request,out:OutputStream) {
        fun asBA(value:String) = value.toByteArray(Charset.defaultCharset())
        out.write(asBA(HTTP_RESPONSE))
        out.flush()
        out.write( AudioStreamProvider.wavHeader)
        out.flush()
        AudioStreamProvider.start()
        var listenerContaner:((Int,ByteArray)->Unit)? = null
        val listener =  {count:Int,buffer:ByteArray ->
            try {
                if(count> 0) {
                    out.write(buffer,0,count)
                    out.flush()
                }
            } catch(e:Exception) {
                AudioStreamProvider.removeBufferListener(listenerContaner)
            }


        }
        listenerContaner=listener
        AudioStreamProvider.registerBufferListener(listener)
        while(AudioStreamProvider.isStarted());
    }



}