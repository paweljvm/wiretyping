package pl.paweljvm.soundsensor.http

import android.util.Log
import java.io.*
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Created by pawel on 2019-05-10.
 */
class SimpleHttpServer {
    private val threadPool = Executors.newFixedThreadPool(64)
    private val handlersMap = mutableMapOf<Request,(Request,OutputStream)->Unit>()
    fun registerSimple(request:Request, handler:(Request)->String) {
        handlersMap[request]=packSimpleRequestHandler(handler)
    }
    fun register(request:Request,handler:(Request,OutputStream) -> Unit) {
        handlersMap[request]=handler
    }

    private fun packSimpleRequestHandler(handler:(Request)->String):(Request,OutputStream)->Unit {
        return {req,out ->
            var writer:PrintWriter? = null
            try {
                val response = handler(req)
                writer =PrintWriter(out)
                with(writer) {
                    print("HTTP/1.1 200\r\n")
                    print("ContentType: application/json\r\n")
                    print("Server: SIMPLE_HTTP\r\n")
                    print("ContentLength: ${response.length}\r\n\r\n")
                    print(response)
                    print("\r\n")
                    flush()
                }
            } catch(e:Exception) {
                writer?.print("HTTP/1.1 500\r\n")
                writer?.flush()
                e.printStackTrace()
                Log.e("error",e.message)
            } finally {
                writer?.close()
            }

        }

    }

    fun start(ip:String,port:Int) {
        val addr = InetAddress.getByName(ip)
        var socket = ServerSocket(port,0,addr)
        while(true) {
            var clientSocket = socket.accept()
            threadPool.submit({
                handleClient(clientSocket)
            })
        }
    }
    private fun handleClient(clientSocket:Socket) {
        var reader:BufferedReader? = null
        var out:OutputStream? = null
        try {
                reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                val request = Request.parse(reader.readLine())
                out = clientSocket.getOutputStream()
                handlersMap[request]?.invoke(request,out)

        } catch(e:Exception) {
            e.printStackTrace()
        } finally {
            reader?.close()
            out?.close()
            clientSocket.close()
        }


    }




}