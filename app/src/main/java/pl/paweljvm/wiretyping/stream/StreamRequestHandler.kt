package pl.paweljvm.wiretyping.stream

import pl.paweljvm.wiretyping.http.Request
import java.io.OutputStream

/**
 * Created by pawel on 2019-06-02.
 */
class StreamRequestHandler(private val requestStrategy:StreamRequestStrategy,private val streamProvider: StreamProvider) {

    fun invoke(request:Request,out:OutputStream) {
        requestStrategy.writeHttpPrefixData(out)
        streamProvider.start()
        var listenerContaner:((Int,ByteArray)->Unit)? = null
        val listener =  {count:Int,buffer:ByteArray ->
            try {
                if(count> 0) {
                   requestStrategy.writeStreamData(out,count,buffer)
                }
            } catch(e:Exception) {
                streamProvider.removeBufferListener(listenerContaner)
            }


        }
        listenerContaner=listener
        streamProvider.registerBufferListener(listener)
        while(streamProvider.isStarted());
    }

}

interface  StreamRequestStrategy {
    fun writeHttpPrefixData(out:OutputStream)
    fun writeStreamData(out:OutputStream,count:Int,buffer:ByteArray)
}
