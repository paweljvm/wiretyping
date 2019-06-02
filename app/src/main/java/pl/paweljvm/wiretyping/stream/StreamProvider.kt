package pl.paweljvm.wiretyping.stream

/**
 * Created by pawel on 2019-06-02.
 */
class StreamProvider(private val strategy: StreamStrategy) {

    @Volatile private var listeners =  listOf<(Int,ByteArray)->Unit>()
    @Volatile private var started = false
    fun start() {
        if(started)
            return
        strategy.handleStart(this::notifyListeners)
        started=true
    }
    fun registerBufferListener(listener:(Int,ByteArray)->Unit) {
        listeners += listOf(listener)
    }
    fun removeBufferListener(listener:((Int,ByteArray)->Unit)?) {
        listener?.let {  listeners -=it }
    }
    fun notifyListeners(size:Int,buffer:ByteArray) {
        listeners.forEach { it(size,buffer)}
    }

    fun stop() {
        strategy.handleStop()
        started=false
    }
    fun isStarted() = started
}
interface StreamStrategy {
    fun handleStart(newBufferListener:(size:Int,buffer:ByteArray)->Unit)

    fun handleStop()
}