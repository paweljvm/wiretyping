package pl.paweljvm.soundsensor.audio

import pl.paweljvm.soundsensor.http.Request
import java.io.OutputStream
import java.nio.charset.Charset

/**
 * Created by pawel on 2019-06-01.
 */
object AudioWavRequestHandlerCreator {
   private const val HTTP_RESPONSE=
            """HTTP/1.1 200 OK\r\n
               Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n
               ContentType: audio/x-vaw\r\n
               Expires: -1\r\n
               Pragma: no-cache\r\n
               Connection: close\r\n\r\n"""
    fun createHandler(request:Request,out:OutputStream) {
        fun asBA(value:String) = value.toByteArray(Charset.defaultCharset())
        out.write(asBA(HTTP_RESPONSE))
        out.flush()
        out.write( AudioStreamProvider.wavHeader)
        out.flush()
        AudioStreamProvider.registerBufferListener {count,buffer ->
            if(count> 0) {
                out.write(buffer,0,count)
                out.flush()
            }
        }
    }



}