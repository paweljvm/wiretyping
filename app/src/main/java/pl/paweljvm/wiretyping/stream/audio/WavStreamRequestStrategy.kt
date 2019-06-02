package pl.paweljvm.wiretyping.stream.audio

import pl.paweljvm.wiretyping.extension.write
import pl.paweljvm.wiretyping.stream.StreamRequestStrategy
import java.io.OutputStream

/**
 * Created by pawel on 2019-06-02.
 */
class WavStreamRequestStrategy : StreamRequestStrategy {

    private  val HTTP_RESPONSE=
            arrayOf(
                    "HTTP/1.1 200 OK\r\n",
                    "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n",
                    "ContentType: audio/x-vaw\r\n",
                    "Expires: -1\r\n",
                    "Pragma: no-cache\r\n",
                    "Connection: close\r\n\r\n").joinToString(separator = "")

    override fun writeHttpPrefixData(out: OutputStream) {
        out.write(HTTP_RESPONSE)
        out.flush()
        out.write(Wav.HEADER)
        out.flush()
    }

    override fun writeStreamData(out: OutputStream, count: Int, buffer: ByteArray) {
        out.write(buffer,0,count)
        out.flush()
    }

}