package pl.paweljvm.wiretyping.stream.video

import pl.paweljvm.wiretyping.extension.write
import pl.paweljvm.wiretyping.stream.StreamRequestStrategy
import java.io.OutputStream

/**
 * Created by pawel on 2019-06-02.
 */
class JpegStreamRequestStrategy : StreamRequestStrategy{
    private  val BOUNDARY = "--NEXT"
    private  val HTTP_RESPONSE =
            "HTTP/1.1 200 OK\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: multipart/x-mixed-replace;boundary=$BOUNDARY\r\n" +
                    "Expires: -1\r\n" +
                    "Pragma: no-cache\r\n\r\n"

    override fun writeHttpPrefixData(out: OutputStream) {
        out.write(HTTP_RESPONSE)
        out.flush()
    }

    override fun writeStreamData(out: OutputStream, count: Int, buffer: ByteArray) {
        out.write("$BOUNDARY\r\n")
        out.write("ContentType: image/jpeg\r\n")
        out.write("Content-Length: $count\r\n\r\n")
        out.write(buffer, 0, count)
        out.write("\r\n")
        out.flush()
    }

}