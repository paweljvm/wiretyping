package pl.paweljvm.wiretyping.extension

import java.io.OutputStream
import java.nio.charset.Charset

/**
 * Created by pawel on 2019-06-02.
 */
fun OutputStream.write(value:String) {
    this.write(value.toByteArray(Charset.defaultCharset()))
}