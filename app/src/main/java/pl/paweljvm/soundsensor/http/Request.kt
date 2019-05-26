package pl.paweljvm.soundsensor.http

import java.util.*

/**
 * Created by pawel on 2019-05-10.
 */
data class Request(val method:Method,val uri:String) {



    companion object {

        private var DEFAULT=Request(Method.GET,"/")

        fun parse(request:String?):Request {
            val requestParts = request?.split(" ")
            fun validate() =  requestParts?.size == 3 || Method.exists(requestParts?.get(0))
            return if(!validate()) DEFAULT
            else Request(Method.valueOf(requestParts!![0]),requestParts[1])
        }


        enum class Method {
            GET,POST,HEAD,PUT,DELETE;
            companion object {
                fun exists(method:String?):Boolean {
                    if(method.isNullOrEmpty())
                        return false
                    for( value in values()) {
                        if(value.toString().equals(method?.toUpperCase()))
                            return true
                    }
                    return false
                }
            }


        }
    }
}